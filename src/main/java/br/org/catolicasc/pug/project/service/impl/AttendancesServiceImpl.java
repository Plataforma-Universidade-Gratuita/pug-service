package br.org.catolicasc.pug.project.service.impl;

import br.org.catolicasc.pug.academic.service.FormerStudentsService;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.partner.service.StaffService;
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
  @Inject StaffService staffService;
  @Inject AuthService authService;

  @ConfigProperty(name = "security.qr.pepper", defaultValue = "default-pepper")
  String pepper;

  @Override
  @Transactional
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
  @Override
  @Transactional
  public long deleteAllWaitingValidationByEnrollmentIdentifier(EnrollmentIdentifier identifier) {
    if (identifier == null) {
      return 0;
    }

    LOG.debugf("Deleting waiting-validation attendances for Enrollment: %s", identifier);

    long deleted =
        repo.deleteAllWaitingValidationByEnrollmentId(
            identifier.getProjectId(), identifier.getFormerStudentId());

    if (deleted > 0) {
      auditPublisher.fireDelete(Attendance.class.getName(), identifier.getProjectId());
    }

    return deleted;
  }

  @Override
  @Transactional
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

  @Override
  public boolean existsByValidatedBy(UUID accountId) {
    if (accountId == null) {
      return false;
    }
    return repo.existsByValidatedBy(accountId);
  }

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

  @Override
  @Transactional
  public Attendance save(AttendanceCreateCommand cmd) {
    LOG.debugf(
        "Attempting to create Attendance for Project: %s, FormerStudent: %s",
        cmd.projectId(), cmd.formerStudentId());
    EnrollmentIdentifier identifier =
        EnrollmentIdentifier.factory(cmd.formerStudentId(), cmd.projectId());

    if (identifier.hasFieldErrors()) {
      throw new AppValidationException(identifier.getFieldErrors());
    }

    authorizeAttendanceCreation(cmd);

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

  @Override
  @Transactional
  public Attendance validate(UUID id, AttendanceValidateCommand cmd) {
    LOG.debugf("Attempting to validate Attendance ID: %s", id);
    Attendance current = getById(id);

    if (!current.getQrValidationInfo().getQrValidationHash().equals(cmd.qrValidationHash())) {
      LOG.warnf("Validation failed: QR Hash mismatch for Attendance ID: %s", id);
      throw ExceptionHelper.attendanceNotFound();
    }

    Project project = projectService.getById(current.getEnrollmentIdentifier().getProjectId());
    authorizeAttendanceValidation(project);

    if (cmd.status() == AttendanceStatus.PRESENT) {
      projectService.validateIsInProgress(project.getId());
    }

    UUID validatorAccountId = authService.getCurrentAccountId();
    Attendance validated =
        AttendanceProcessor.processValidationInput(current, validatorAccountId, cmd.status());

    if (validated.hasFieldErrors()) {
      throw new AppValidationException(validated.getFieldErrors());
    }

    repo.update(validated);
    LOG.infof("Attendance validated successfully. ID: %s, Status: %s", id, cmd.status());

    boolean becamePresent =
        current.getStatus() != AttendanceStatus.PRESENT
            && validated.getStatus() == AttendanceStatus.PRESENT;

    boolean stoppedBeingPresent =
        current.getStatus() == AttendanceStatus.PRESENT
            && validated.getStatus() == AttendanceStatus.ABSENT;

    if (becamePresent) {
      var formerStudent =
          formerStudentsService.getById(validated.getEnrollmentIdentifier().getFormerStudentId());
      formerStudent.validateCanAddCompletedHours(validated.getQrValidationInfo().getDuration());

      project.validateCanAddCompletedHours(validated.getQrValidationInfo().getDuration());

      formerStudentsService.addCompletedHours(
          validated.getEnrollmentIdentifier().getFormerStudentId(),
          validated.getQrValidationInfo().getDuration());
      projectService.addCompletedHours(
          validated.getEnrollmentIdentifier().getProjectId(),
          validated.getQrValidationInfo().getDuration());
    } else if (stoppedBeingPresent) {
      var formerStudent =
          formerStudentsService.getById(validated.getEnrollmentIdentifier().getFormerStudentId());
      formerStudent.validateCanRemoveCompletedHours(validated.getQrValidationInfo().getDuration());

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

  private void authorizeAttendanceCreation(AttendanceCreateCommand cmd) {
    AccountType currentType = authService.getCurrentAccountType();

    if (currentType == AccountType.ADMIN) {
      return;
    }

    if (currentType == AccountType.FORMER_STUDENT) {
      UUID currentAccountId = authService.getCurrentAccountId();

      if (currentAccountId != null && currentAccountId.equals(cmd.formerStudentId())) {
        return;
      }
    }

    throw ExceptionHelper.attendanceEnrollmentNotFound();
  }

  private void authorizeAttendanceValidation(Project project) {
    AccountType currentType = authService.getCurrentAccountType();

    if (currentType == AccountType.ADMIN) {
      return;
    }

    if (currentType != AccountType.PARTNER) {
      authService.requireCurrentAccountNotOfType(AccountType.FORMER_STUDENT);
      throw ExceptionHelper.attendanceNotFound();
    }

    UUID currentAccountId = authService.getCurrentAccountId();
    Staff staff = staffService.getByAccountId(currentAccountId);

    if (!staff.getEntityId().equals(project.getEntityId())) {
      throw ExceptionHelper.attendanceNotFound();
    }
  }
}
