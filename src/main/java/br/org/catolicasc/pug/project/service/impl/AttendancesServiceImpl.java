package br.org.catolicasc.pug.project.service.impl;

import br.org.catolicasc.pug.academic.service.FormerStudentsService;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.domain.AttendanceRepository;
import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentIdentifier;
import br.org.catolicasc.pug.project.service.AttendancesService;
import br.org.catolicasc.pug.project.service.EnrollmentsService;
import br.org.catolicasc.pug.project.service.ProjectService;
import br.org.catolicasc.pug.project.service.dtos.attendance.AttendanceCreateCommand;
import br.org.catolicasc.pug.project.service.dtos.attendance.AttendanceValidateCommand;
import br.org.catolicasc.pug.project.service.utils.AttendanceProcessor;
import br.org.catolicasc.pug.project.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

/** Implementation of the attendance command-side application service. */
@ApplicationScoped
public class AttendancesServiceImpl implements AttendancesService {

  private static final Logger LOG = Logger.getLogger(AttendancesServiceImpl.class);

  @Inject AuditPublisher auditPublisher;
  @Inject AttendanceRepository repo;
  @Inject EnrollmentsService enrollmentsService;
  @Inject ProjectService projectService;
  @Inject FormerStudentsService formerStudentsService;
  @Inject AuthService authService;

  @ConfigProperty(name = "security.qr.pepper", defaultValue = "default-pepper")
  String pepper;

  /** {@inheritDoc} */
  @Transactional
  @Override
  public long deleteAllByEnrollmentIdentifier(EnrollmentIdentifier identifier) {
    if (identifier == null) {
      return 0;
    }
    LOG.debugf("Deleting all attendances for Enrollment: %s", identifier);
    long deleted =
        repo.deleteAllByEnrollmentId(identifier.getProjectId(), identifier.getFormerStudentId());
    if (deleted > 0) {
      auditPublisher.fireDelete(Attendance.class.getName(), identifier.getProjectId());
    }
    return deleted;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public long deleteAllWaitingValidationByProjectId(UUID projectId) {
    if (projectId == null) {
      return 0;
    }
    LOG.debugf("Deleting all waiting-validation attendances for Project: %s", projectId);
    long deleted = repo.deleteAllWaitingValidationByProjectId(projectId);
    if (deleted > 0) {
      auditPublisher.fireDelete(Attendance.class.getName(), projectId);
    }
    return deleted;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean delete(UUID id) {
    LOG.debugf("Attempting to delete Attendance ID: %s", id);
    if (id == null) {
      return false;
    }

    boolean deleted = repo.deleteById(id);
    if (deleted) {
      LOG.infof("Attendance deleted successfully. ID: %s", id);
      auditPublisher.fireDelete(Attendance.class.getName(), id);
    } else {
      LOG.debugf("Delete failed: Attendance ID %s not found (idempotent)", id);
    }
    return deleted;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByValidatedBy(UUID accountId) {
    if (accountId == null) {
      return false;
    }
    return repo.existsByValidatedBy(accountId);
  }

  /** {@inheritDoc} */
  @Override
  public Attendance getById(UUID id) {
    Attendance attendance =
        repo.findOptionalById(id).orElseThrow(ExceptionHelper::attendanceNotFound);

    if (attendance.hasFieldErrors()) {
      LOG.errorf(
          "DATA CORRUPTION DETECTED: Attendance %s: %s", id, attendance.getProblemsSummary());
      throw ExceptionHelper.attendanceNotFound();
    }
    return attendance;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Attendance save(AttendanceCreateCommand cmd) {
    LOG.debugf(
        "Attempting to create Attendance for Project: %s, FormerStudent: %s",
        cmd.projectId(), cmd.formerStudentId());
    EnrollmentIdentifier identifier =
        EnrollmentIdentifier.factory(cmd.formerStudentId(), cmd.projectId());

    if (identifier.hasFieldErrors()) {
      throw new AppValidationException(identifier.getFieldErrors());
    }

    Enrollment enrollment;
    try {
      enrollment = enrollmentsService.getByIds(identifier);
    } catch (ResourceNotFoundException ex) {
      throw ExceptionHelper.attendanceEnrollmentNotFound();
    }
    enrollment.validateCanCreateAttendance();

    Attendance attendance =
        AttendanceProcessor.processCreateInput(enrollment, cmd.duration(), pepper);

    if (attendance.hasFieldErrors()) {
      throw new AppValidationException(attendance.getFieldErrors());
    }

    Attendance saved = repo.persist(attendance);
    LOG.infof("Attendance created successfully. ID: %s", saved.getId());

    auditPublisher.fireCreate(Attendance.class.getName(), saved.getId());
    return saved;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Attendance validate(UUID id, AttendanceValidateCommand cmd) {
    LOG.debugf("Attempting to validate Attendance ID: %s", id);
    Attendance current = getById(id);
    UUID validatorAccountId = authService.getCurrentAccountId();
    authService.requireCurrentAccountNotOfType(AccountType.FORMER_STUDENT);

    if (!current.getQrValidationInfo().getQrValidationHash().equals(cmd.qrValidationHash())) {
      LOG.warnf("Validation failed: QR Hash mismatch for Attendance ID: %s", id);
      throw ExceptionHelper.attendanceNotFound();
    }

    if (cmd.status() == AttendanceStatus.PRESENT) {
      projectService.validateIsInProgress(current.getEnrollmentIdentifier().getProjectId());
    }

    Attendance validated =
        AttendanceProcessor.processValidationInput(current, validatorAccountId, cmd.status());

    if (validated.hasFieldErrors()) {
      throw new AppValidationException(validated.getFieldErrors());
    }

    repo.update(validated);
    LOG.infof("Attendance validated successfully. ID: %s, Status: %s", id, cmd.status());

    if (validated.getStatus() == AttendanceStatus.PRESENT) {
      var formerStudent =
          formerStudentsService.getById(validated.getEnrollmentIdentifier().getFormerStudentId());
      formerStudent.validateCanAddCompletedHours(validated.getQrValidationInfo().getDuration());

      Project project = projectService.getById(validated.getEnrollmentIdentifier().getProjectId());
      project.validateCanAddCompletedHours(validated.getQrValidationInfo().getDuration());

      formerStudentsService.addCompletedHours(
          validated.getEnrollmentIdentifier().getFormerStudentId(),
          validated.getQrValidationInfo().getDuration());
      projectService.addCompletedHours(
          validated.getEnrollmentIdentifier().getProjectId(),
          validated.getQrValidationInfo().getDuration());
    } else if (current.getStatus() == AttendanceStatus.PRESENT
        && validated.getStatus() == AttendanceStatus.ABSENT) {
      var formerStudent =
          formerStudentsService.getById(validated.getEnrollmentIdentifier().getFormerStudentId());
      formerStudent.validateCanRemoveCompletedHours(validated.getQrValidationInfo().getDuration());

      Project project = projectService.getById(validated.getEnrollmentIdentifier().getProjectId());
      project.validateCanRemoveCompletedHours(validated.getQrValidationInfo().getDuration());

      formerStudentsService.removeCompletedHours(
          validated.getEnrollmentIdentifier().getFormerStudentId(),
          validated.getQrValidationInfo().getDuration());
      projectService.removeCompletedHours(
          validated.getEnrollmentIdentifier().getProjectId(),
          validated.getQrValidationInfo().getDuration());
    }

    auditPublisher.fireUpdate(Attendance.class.getName(), id, current, validated);
    return getById(id);
  }
}
