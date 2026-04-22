package br.org.catolicasc.pug.project.service.impl;

import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.academic.service.StudentService;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.domain.EnrollmentRepository;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentIdentifier;
import br.org.catolicasc.pug.project.service.EnrollmentService;
import br.org.catolicasc.pug.project.service.ProjectService;
import br.org.catolicasc.pug.project.service.dtos.EnrollmentCreateCommand;
import br.org.catolicasc.pug.project.service.utils.EnrollmentProcessor;
import br.org.catolicasc.pug.project.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link EnrollmentService} command interface.
 *
 * <p>This application-scoped service orchestrates state mutations for student enrollments. It
 * coordinates with {@link ProjectService} and {@link StudentService} to validate structural
 * references and delegates pure lifecycle transitions to the {@link Enrollment} aggregate.
 */
@ApplicationScoped
public class EnrollmentServiceImpl implements EnrollmentService {

  private static final Logger LOG = Logger.getLogger(EnrollmentServiceImpl.class);

  @Inject AuditPublisher auditPublisher;
  @Inject EnrollmentRepository repo;
  @Inject AuthService authService;
  @Inject ProjectService projectService;
  @Inject StudentService studentService;

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Enrollment accept(EnrollmentIdentifier identifier) {
    return changeStatus(identifier, EnrollmentStatus.APPROVED);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Enrollment cancel(EnrollmentIdentifier identifier) {
    return changeStatus(identifier, EnrollmentStatus.CANCELED);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Enrollment complete(EnrollmentIdentifier identifier) {
    return changeStatus(identifier, EnrollmentStatus.COMPLETED);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean delete(EnrollmentIdentifier identifier) {
    LOG.debugf("Attempting to delete Enrollment: %s", identifier);
    if (identifier == null) {
      return false;
    }

    boolean deleted = repo.deleteById(identifier);
    if (deleted) {
      LOG.infof("Enrollment deleted successfully. Identifier: %s", identifier);
      auditPublisher.fireDelete(Enrollment.class.getName(), identifier.getProjectId());
    } else {
      LOG.debugf("Delete failed: Enrollment %s not found (idempotent)", identifier);
    }
    return deleted;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsAnyByStudentId(UUID studentId) {
    if (studentId == null) {
      return false;
    }
    return repo.existsByStudentId(studentId);
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsAnyByProjectId(UUID projectId) {
    if (projectId == null) {
      return false;
    }
    return repo.existsByProjectId(projectId);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Enrollment exit(EnrollmentIdentifier identifier) {
    return changeStatus(identifier, EnrollmentStatus.EXITED);
  }

  /** {@inheritDoc} */
  @Override
  public Enrollment getByIds(EnrollmentIdentifier identifier) {
    Enrollment enrollment =
        repo.findOptionalById(identifier).orElseThrow(ExceptionHelper::enrollmentNotFound);

    if (enrollment.hasFieldErrors()) {
      LOG.errorf(
          "DATA CORRUPTION DETECTED: Enrollment [P:%s, S:%s]: %s",
          identifier.getProjectId(), identifier.getStudentId(), enrollment.getProblemsSummary());
      throw ExceptionHelper.enrollmentNotFound();
    }
    return enrollment;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Enrollment reject(EnrollmentIdentifier identifier) {
    return changeStatus(identifier, EnrollmentStatus.REJECTED);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Enrollment remove(EnrollmentIdentifier identifier) {
    return changeStatus(identifier, EnrollmentStatus.REMOVED);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Enrollment save(EnrollmentCreateCommand cmd) {
    LOG.debugf("Attempting to create Enrollment for Project ID: %s", cmd.projectId());
    authService.requireCurrentAccountOfType(AccountType.STUDENT);
    Project project = projectService.getById(cmd.projectId());
    Student student = studentService.getById(authService.getCurrentAccountId());

    EnrollmentIdentifier identifier =
        EnrollmentIdentifier.factory(student.getAccountId(), project.getId());

    if (identifier.hasFieldErrors()) {
      throw new AppValidationException(identifier.getFieldErrors());
    }
    if (repo.existsById(identifier)) {
      LOG.warnf(
          "Creation failed: Enrollment already exists for Project: %s, Student: %s",
          project.getId(), student.getAccountId());
      throw ExceptionHelper.enrollmentAlreadyExists();
    }

    Enrollment enrollment = EnrollmentProcessor.processCreateInput(student, project);

    if (enrollment.hasFieldErrors()) {
      throw new AppValidationException(enrollment.getFieldErrors());
    }

    Enrollment saved = repo.persist(enrollment);
    LOG.infof("Enrollment created successfully. Identifier: %s", identifier);

    auditPublisher.fireCreate(Enrollment.class.getName(), identifier.getProjectId());
    return saved;
  }

  /**
   * Centralized private helper to handle enrollment status transitions.
   *
   * <p>This method reconstitutes the current {@link Enrollment} aggregate, delegates the status
   * transition to {@link Enrollment#changeStatus(EnrollmentStatus)}, validates the resulting
   * aggregate, and persists the updated state.
   *
   * @param identifier the composite {@link EnrollmentIdentifier} uniquely identifying the
   *     enrollment (project + student)
   * @param newStatus the target {@link EnrollmentStatus} to transition to
   * @return the updated {@link Enrollment} aggregate
   * @throws AppValidationException if the updated enrollment violates domain constraints
   */
  private Enrollment changeStatus(EnrollmentIdentifier identifier, EnrollmentStatus newStatus) {
    LOG.debugf("Attempting to transition Enrollment %s to status %s", identifier, newStatus);
    Enrollment current = getByIds(identifier);
    Enrollment updated = current.changeStatus(newStatus);

    if (updated.hasFieldErrors()) {
      throw new AppValidationException(updated.getFieldErrors());
    }

    repo.update(updated);
    LOG.infof("Enrollment status updated to %s. Identifier: %s", newStatus, identifier);

    auditPublisher.fireUpdate(
        Enrollment.class.getName(), identifier.getProjectId(), current, updated);
    return updated;
  }
}
