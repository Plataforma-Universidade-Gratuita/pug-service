package br.org.catolicasc.pug.project.service.impl;

import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.service.FormerStudentsService;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.domain.EnrollmentRepository;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentIdentifier;
import br.org.catolicasc.pug.project.service.EnrollmentsService;
import br.org.catolicasc.pug.project.service.ProjectService;
import br.org.catolicasc.pug.project.service.dtos.enrollments.EnrollmentCreateCommand;
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

@ApplicationScoped
public class EnrollmentsServiceImpl implements EnrollmentsService {

  private static final Logger LOG = Logger.getLogger(EnrollmentsServiceImpl.class);

  /** {@inheritDoc} */
  @Inject AuditPublisher auditPublisher;

  @Inject EnrollmentRepository repo;
  @Inject AuthService authService;
  @Inject ProjectService projectService;
  @Inject FormerStudentsService studentService;

  @Transactional
  @Override
  public Enrollment changeStatus(EnrollmentIdentifier identifier, EnrollmentStatus status) {
    LOG.debugf("Attempting to transition Enrollment %s to status %s", identifier, status);
    Enrollment current = getByIds(identifier);
    Enrollment updated = current.changeStatus(status);

    if (updated.hasFieldErrors()) {
      throw new AppValidationException(updated.getFieldErrors());
    }

    repo.update(updated);
    auditPublisher.fireUpdate(
        Enrollment.class.getName(), identifier.getProjectId(), current, updated);
    return updated;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public long changeStatusByProjectId(UUID projectId, EnrollmentStatus targetStatus) {
    if (projectId == null || targetStatus == null) {
      return 0L;
    }

    long changed = 0L;
    for (Enrollment enrollment : repo.listAllByProjectId(projectId)) {
      try {
        changeStatus(enrollment.getIdentifier(), targetStatus);
        changed++;
      } catch (RuntimeException ignored) {
        // Intentionally skip enrollments whose lifecycle does not allow the forced transition.
      }
    }
    return changed;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public long changeStatusByProjectId(
      UUID projectId, EnrollmentStatus currentStatus, EnrollmentStatus targetStatus) {
    if (projectId == null || currentStatus == null || targetStatus == null) {
      return 0L;
    }

    long changed = 0L;
    for (Enrollment enrollment : repo.listAllByProjectId(projectId)) {
      if (enrollment.getStatus() != currentStatus) {
        continue;
      }
      try {
        changeStatus(enrollment.getIdentifier(), targetStatus);
        changed++;
      } catch (RuntimeException ignored) {
      }
    }
    return changed;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public long completeAllByFormerStudentId(UUID formerStudentId) {
    if (formerStudentId == null) {
      return 0L;
    }

    long changed = 0L;
    for (Enrollment enrollment : repo.listAllByFormerStudentId(formerStudentId)) {
      if (enrollment.getStatus() != EnrollmentStatus.APPROVED) {
        continue;
      }
      try {
        changeStatus(enrollment.getIdentifier(), EnrollmentStatus.COMPLETED);
        changed++;
      } catch (RuntimeException ignored) {
      }
    }
    return changed;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean delete(EnrollmentIdentifier identifier) {
    if (identifier == null) {
      return false;
    }
    boolean deleted = repo.deleteById(identifier);
    if (deleted) {
      auditPublisher.fireDelete(Enrollment.class.getName(), identifier.getProjectId());
    }
    return deleted;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsAnyByProjectId(UUID projectId) {
    return projectId != null && repo.existsByProjectId(projectId);
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsAnyByFormerStudentId(UUID formerStudentId) {
    return formerStudentId != null && repo.existsByFormerStudentId(formerStudentId);
  }

  /** {@inheritDoc} */
  @Override
  public Enrollment getByIds(EnrollmentIdentifier identifier) {
    Enrollment enrollment =
        repo.findOptionalById(identifier).orElseThrow(ExceptionHelper::enrollmentNotFound);

    if (enrollment.hasFieldErrors()) {
      throw ExceptionHelper.enrollmentNotFound();
    }
    return enrollment;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Enrollment save(EnrollmentCreateCommand cmd) {
    Project project = projectService.getById(cmd.projectId());
    FormerStudent formerStudent;

    if (authService.getCurrentAccountType() == AccountType.ADMIN && cmd.formerStudentId() != null) {
      formerStudent = studentService.getById(cmd.formerStudentId());
    } else {
      authService.requireCurrentAccountOfType(AccountType.FORMER_STUDENT);
      formerStudent = studentService.getById(authService.getCurrentAccountId());
    }

    EnrollmentIdentifier identifier =
        EnrollmentIdentifier.factory(formerStudent.getAccountId(), project.getId());

    if (identifier.hasFieldErrors()) {
      throw new AppValidationException(identifier.getFieldErrors());
    }
    if (repo.existsById(identifier)) {
      throw ExceptionHelper.enrollmentAlreadyExists();
    }

    Enrollment enrollment = EnrollmentProcessor.processCreateInput(formerStudent, project);
    if (enrollment.hasFieldErrors()) {
      throw new AppValidationException(enrollment.getFieldErrors());
    }

    Enrollment saved = repo.persist(enrollment);
    auditPublisher.fireCreate(Enrollment.class.getName(), identifier.getProjectId());
    return saved;
  }
}
