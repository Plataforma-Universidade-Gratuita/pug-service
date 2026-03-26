package com.pug.project.service.impl;

import com.pug.academic.service.SchoolService;
import com.pug.partner.service.EntityService;
import com.pug.partner.service.StaffService;
import com.pug.project.domain.Project;
import com.pug.project.domain.ProjectRepository;
import com.pug.project.domain.ProjectsBySchool;
import com.pug.project.domain.enums.ProjectStatus;
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

/**
 * Implementation of the {@link ProjectService} command interface.
 *
 * <p>This application-scoped service orchestrates state mutations for projects. It manages
 * transaction boundaries, enforces cross-domain constraints, and manages project lifecycle
 * transitions.
 */
@ApplicationScoped
public class ProjectServiceImpl implements ProjectService {

  private static final Logger LOG = Logger.getLogger(ProjectServiceImpl.class);

  @Inject ProjectRepository repo;
  @Inject EntityService entityService;
  @Inject StaffService staffService;
  @Inject EnrollmentService enrollmentService;
  @Inject SchoolService schoolService;

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean delete(UUID id) {
    if (id == null) {
      return false;
    }
    //    if (enrollmentService.existsAnyByProjectId(id)) {
    //      LOG.warnf("Delete failed: Project ID %s has active enrollments", id);
    //      throw ExceptionHelper.projectHasEnrollments();
    //    }
    return repo.deleteById(id);
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsAnyByEntityId(UUID entityId) {
    if (entityId == null) {
      return false;
    }
    return repo.existsByEntityId(entityId);
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByCreatedBy(UUID accountId) {
    if (accountId == null) {
      return false;
    }
    return repo.existsByCreatedBy(accountId);
  }

  /** {@inheritDoc} */
  @Override
  public Project getById(UUID id) {
    Project project = repo.findOptionalById(id).orElseThrow(ExceptionHelper::projectNotFound);

    if (project.hasFieldErrors()) {
      LOG.errorf("DATA CORRUPTION DETECTED: Project %s: %s", id, project.getProblemsSummary());
      throw ExceptionHelper.projectNotFound();
    }
    return project;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Project save(ProjectCreateCommand cmd) {
    entityService.getById(cmd.entityId());
    staffService.getByAccountId(cmd.createdBy());
    schoolService.getById(cmd.schoolId());

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

    Project savedProject = repo.persist(project);
    ProjectsBySchool association =
        ProjectProcessor.processCreateProjectBySchoolInput(savedProject.getId(), cmd.schoolId());

    if (association.hasFieldErrors()) {
      throw new AppValidationException(association.getFieldErrors());
    }

    repo.persistAssociation(association);

    return savedProject;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Project transitionStatus(UUID id, ProjectStatus status) {
    Project project = getById(id);
    Project updated;

    switch (status) {
      case IN_PROGRESS -> updated = project.start();
      case COMPLETED -> updated = project.complete();
      case CANCELED -> updated = project.cancel();
      case ON_HOLD -> updated = project.putOnHold();
      case PLANNED -> updated = project.retake();
      default -> throw new IllegalArgumentException("Unsupported status: " + status);
    }

    repo.update(updated);
    return updated;
  }

  /** {@inheritDoc} */
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

    if (cmd.schoolId() != null) {
      schoolService.getById(cmd.schoolId());
      ProjectsBySchool association =
          ProjectProcessor.processCreateProjectBySchoolInput(id, cmd.schoolId());

      if (association.hasFieldErrors()) {
        throw new AppValidationException(association.getFieldErrors());
      }
      repo.updateAssociation(association);
    }

    return getById(id);
  }
}
