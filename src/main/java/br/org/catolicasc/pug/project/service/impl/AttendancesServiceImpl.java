package br.org.catolicasc.pug.project.service.impl;

import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.service.FormerStudentsService;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.domain.AttendanceRepository;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentIdentifier;
import br.org.catolicasc.pug.project.service.AttendancesService;
import br.org.catolicasc.pug.project.service.ProjectService;
import br.org.catolicasc.pug.project.service.dtos.attendance.AttendanceCreateCommand;
import br.org.catolicasc.pug.project.service.dtos.attendance.AttendanceValidateCommand;
import br.org.catolicasc.pug.project.service.utils.AttendanceProcessor;
import br.org.catolicasc.pug.project.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

/** Implementation of the attendance command-side application service. */
@ApplicationScoped
public class AttendancesServiceImpl implements AttendancesService {

  private static final Logger LOG = Logger.getLogger(AttendancesServiceImpl.class);

  @Inject AuditPublisher auditPublisher;
  @Inject AttendanceRepository repo;
  @Inject ProjectService projectService;
  @Inject FormerStudentsService studentService;
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
    Project project = projectService.getById(cmd.projectId());
    FormerStudent formerStudent = studentService.getById(cmd.formerStudentId());

    String qrHash = generateQrHash(cmd.projectId(), cmd.formerStudentId());

    Attendance attendance =
        AttendanceProcessor.processCreateInput(project, formerStudent, cmd.duration(), qrHash);

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

    Attendance validated =
        AttendanceProcessor.processValidationInput(current, validatorAccountId, cmd.status());

    if (validated.hasFieldErrors()) {
      throw new AppValidationException(validated.getFieldErrors());
    }

    repo.update(validated);
    LOG.infof("Attendance validated successfully. ID: %s, Status: %s", id, cmd.status());

    if (validated.getStatus() == AttendanceStatus.PRESENT) {
      studentService.addCompletedHours(
          validated.getEnrollmentIdentifier().getFormerStudentId(),
          validated.getQrValidationInfo().getDuration());
      projectService.addCompletedHours(
          validated.getEnrollmentIdentifier().getProjectId(),
          validated.getQrValidationInfo().getDuration());
    }

    auditPublisher.fireUpdate(Attendance.class.getName(), id, current, validated);
    return getById(id);
  }

  private String generateQrHash(UUID projectId, UUID formerStudentId) {
    String raw = projectId.toString() + formerStudentId.toString() + LocalDateTime.now() + pepper;
    return BcryptUtil.bcryptHash(raw);
  }
}
