package com.pug.project.service.impl;

import com.pug.partner.service.EntityService;
import com.pug.partner.service.StaffService;
import com.pug.project.domain.Project;
import com.pug.project.domain.ProjectRepository;
import com.pug.project.service.EnrollmentService;
import com.pug.project.service.ProjectService;
import com.pug.project.service.dtos.ProjectCreateCommand;
import com.pug.project.service.dtos.ProjectUpdateCommand;
import com.pug.project.service.utils.ExceptionHelper;
import com.pug.project.service.utils.ProjectProcessor;
import com.pug.shared.exceptions.AppValidationException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ProjectServiceImpl implements ProjectService {

  private static final Logger LOG = Logger.getLogger(ProjectServiceImpl.class);

  @Inject ProjectRepository repo;
  @Inject EntityService entityService;
  @Inject StaffService staffService;
  @Inject EnrollmentService enrollmentService; // Need this to check before delete

  @Transactional
  @Override
  public Project cancel(UUID id) {
    Project project = getById(id).cancel();
    repo.update(project);
    return project;
  }

  @Transactional
  @Override
  public Project complete(UUID id) {
    Project project = getById(id).complete();
    repo.update(project);
    return project;
  }

  @Transactional
  @Override
  public boolean delete(UUID id) {
    if (id == null) {
      return false;
    }
    // Business rule: Cannot delete project with enrollments
    // We will assume enrollmentService has a method existsByProjectId soon
    // For now, we proceed with repo deletion directly
    return repo.deleteById(id);
  }

  @Override
  public boolean existsAnyByEntityId(UUID entityId) {
    if (entityId == null) return false;
    return repo.existsByEntityId(entityId);
  }

  @Override
  public boolean existsByCreatedBy(UUID accountId) {
    if (accountId == null) return false;
    return repo.existsByCreatedBy(accountId);
  }

  @Override
  public Project getById(UUID id) {
    Project project = repo.findOptionalById(id).orElseThrow(ExceptionHelper::projectNotFound);

    if (project.hasFieldErrors()) {
      LOG.errorf("DATA CORRUPTION DETECTED: Project %s: %s", id, project.getProblemsSummary());
      throw ExceptionHelper.projectNotFound();
    }
    return project;
  }

  @Transactional
  @Override
  public Project putOnHold(UUID id) {
    Project project = getById(id).putOnHold();
    repo.update(project);
    return project;
  }

  @Transactional
  @Override
  public Project retake(UUID id) {
    Project project = getById(id).retake();
    repo.update(project);
    return project;
  }

  @Transactional
  @Override
  public Project save(ProjectCreateCommand cmd) {
    // Validate structural dependencies
    entityService.getById(cmd.entityId());
    staffService.getByAccountId(cmd.createdBy());

    if (repo.existsByNameAndEntityId(cmd.name(), cmd.entityId())) {
      throw ExceptionHelper.projectAlreadyExists();
    }

    Project project =
        ProjectProcessor.processCreateInput(
            cmd.name(),
            cmd.entityId(),
            cmd.description(),
            cmd.createdBy(),
            cmd.maxParticipants(),
            cmd.offeredHours());

    if (project.hasFieldErrors()) {
      throw new AppValidationException(project.getFieldErrors());
    }

    return repo.persist(project);
  }

  @Transactional
  @Override
  public Project start(UUID id) {
    Project project = getById(id).start();
    repo.update(project);
    return project;
  }

  @Transactional
  @Override
  public Project update(UUID id, ProjectUpdateCommand cmd) {
    Project current = getById(id);

    Project updated =
        ProjectProcessor.processUpdateInput(
            current, cmd.name(), cmd.description(), cmd.maxParticipants(), cmd.offeredHours());

    if (updated.hasFieldErrors()) {
      throw new AppValidationException(updated.getFieldErrors());
    }

    if (cmd.name() != null
        && !cmd.name().equals(current.getName())
        && repo.existsByNameAndEntityId(updated.getName(), current.getEntityId())) {
      throw ExceptionHelper.projectAlreadyExists();
    }

    repo.update(updated);
    return getById(id);
  }
}
