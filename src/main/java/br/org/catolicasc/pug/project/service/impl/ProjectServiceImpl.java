/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.service.impl;

import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.partner.service.EntitiesService;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.ProjectRepository;
import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import br.org.catolicasc.pug.project.service.EnrollmentsService;
import br.org.catolicasc.pug.project.service.ProjectAreaOfExpertiseService;
import br.org.catolicasc.pug.project.service.ProjectService;
import br.org.catolicasc.pug.project.service.dtos.projects.ProjectCreateCommand;
import br.org.catolicasc.pug.project.service.dtos.projects.ProjectUpdateCommand;
import br.org.catolicasc.pug.project.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.project.service.utils.ProjectProcessor;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
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

  @Inject AuditPublisher auditPublisher;
  @Inject ProjectRepository repo;
  @Inject ProjectAreaOfExpertiseService associationService;
  @Inject AuthService authService;
  @Inject EntitiesService entityService;
  @Inject EnrollmentsService enrollmentsService;

  /** {@inheritDoc} */
  @Override
  @Transactional
  public Project addCompletedHours(UUID id, BigDecimal hours) {
    LOG.debugf("Adicionando %s horas completadas ao projeto: %s", hours, id);
    Project current = getById(id);

    Project updated = current.addCompletedHours(hours);

    if (updated.hasFieldErrors()) {
      throw new AppValidationException(updated.getFieldErrors());
    }

    repo.update(updated);
    if (updated.getProjectStatus() == ProjectStatus.COMPLETED) {
      propagateEnrollmentStatusChange(id, current.getProjectStatus(), updated.getProjectStatus());
    }
    LOG.infof("Horas adicionadas ao projeto %s. Status atual: %s", id, updated.getProjectStatus());
    return updated;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public Project removeCompletedHours(UUID id, BigDecimal hours) {
    LOG.debugf("Removing %s completed hours from project: %s", hours, id);
    Project current = getById(id);

    Project updated = current.removeCompletedHours(hours);

    if (updated.hasFieldErrors()) {
      throw new AppValidationException(updated.getFieldErrors());
    }

    repo.update(updated);
    LOG.infof("Hours removed from project %s. Current status: %s", id, updated.getProjectStatus());
    return updated;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public boolean delete(UUID id) {
    LOG.debugf("Attempting to delete Project ID: %s", id);
    if (id == null) {
      return false;
    }

    if (enrollmentsService.existsAnyByProjectId(id)) {
      LOG.warnf("Delete failed: Project ID %s has active enrollments", id);
      throw ExceptionHelper.projectHasEnrollments();
    }
    associationService.deleteAllByProjectId(id);

    boolean deleted = repo.deleteById(id);
    if (deleted) {
      LOG.infof("Project deleted successfully. ID: %s", id);
      auditPublisher.fireDelete(Project.class.getName(), id);
    } else {
      LOG.debugf("Delete failed: Project ID %s not found (idempotent)", id);
    }
    return deleted;
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
  public void validateIsInProgress(UUID projectId) {
    if (!repo.isInProgress(projectId)) {
      throw ExceptionHelper.attendanceProjectNotInProgress();
    }
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
  @Override
  @Transactional
  public Project save(ProjectCreateCommand cmd) {
    LOG.debugf("Attempting to create Project: %s", cmd.name());
    authService.requireCurrentAccountNotOfType(AccountType.FORMER_STUDENT);
    entityService.getById(cmd.entityId());

    if (repo.existsByNameAndEntityId(cmd.name(), cmd.entityId())) {
      LOG.warnf(
          "Creation failed: Project with name %s already exists for entity %s",
          cmd.name(), cmd.entityId());
      throw ExceptionHelper.projectAlreadyExists();
    }

    Project project =
        ProjectProcessor.processCreateInput(
            cmd.name(),
            cmd.entityId(),
            cmd.description(),
            authService.getCurrentAccountId(),
            cmd.maxParticipants(),
            cmd.offeredHours());

    if (project.hasFieldErrors()) {
      throw new AppValidationException(project.getFieldErrors());
    }

    Project savedProject = repo.persist(project);
    LOG.infof("Project created successfully. ID: %s", savedProject.getId());

    auditPublisher.fireCreate(Project.class.getName(), savedProject.getId());
    return savedProject;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public Project transitionStatus(UUID id, ProjectStatus status) {
    Project project = getById(id);
    Project updated;

    switch (status) {
      case IN_PROGRESS ->
          updated =
              project.getProjectStatus() == ProjectStatus.ON_HOLD
                  ? project.retake()
                  : project.start();
      case COMPLETED -> updated = project.complete();
      case CANCELED -> updated = project.cancel();
      case ON_HOLD -> updated = project.putOnHold();
      default -> throw new IllegalArgumentException("Unsupported status: " + status);
    }

    repo.update(updated);
    propagateEnrollmentStatusChange(id, project.getProjectStatus(), updated.getProjectStatus());

    auditPublisher.fireUpdate(Project.class.getName(), id, project, updated);
    return updated;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public Project update(UUID id, ProjectUpdateCommand cmd) {
    LOG.debugf("Attempting to update Project ID: %s", id);
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
      LOG.warnf(
          "Update failed: Project name %s already exists for entity %s",
          updated.getName(), current.getEntityId());
      throw ExceptionHelper.projectAlreadyExists();
    }

    repo.update(updated);
    LOG.infof("Project updated successfully. ID: %s", id);

    auditPublisher.fireUpdate(Project.class.getName(), id, current, updated);
    return getById(id);
  }

  private void propagateEnrollmentStatusChange(
      UUID projectId, ProjectStatus previousStatus, ProjectStatus nextStatus) {
    if (projectId == null || previousStatus == null || nextStatus == null) {
      return;
    }

    switch (nextStatus) {
      case CANCELED ->
          enrollmentsService.changeStatusByProjectId(projectId, EnrollmentStatus.CANCELED);
      case COMPLETED ->
          enrollmentsService.changeStatusByProjectId(projectId, EnrollmentStatus.COMPLETED);
      case ON_HOLD ->
          enrollmentsService.changeStatusByProjectId(
              projectId, EnrollmentStatus.APPROVED, EnrollmentStatus.ON_HOLD);
      case IN_PROGRESS -> {
        if (previousStatus == ProjectStatus.ON_HOLD) {
          enrollmentsService.changeStatusByProjectId(
              projectId, EnrollmentStatus.ON_HOLD, EnrollmentStatus.APPROVED);
        }
      }
      case PLANNED ->
          enrollmentsService.changeStatusByProjectId(
              projectId, EnrollmentStatus.ON_HOLD, EnrollmentStatus.APPROVED);
      default -> {
        // No enrollment status propagation is required for this transition.
      }
    }
  }
}
