package com.pug.project.service.impl;

import com.pug.academic.domain.Student;
import com.pug.academic.service.StudentService;
import com.pug.identity.service.AccountService;
import com.pug.identity.service.AuthService;
import com.pug.project.domain.Attendance;
import com.pug.project.domain.AttendanceRepository;
import com.pug.project.domain.Project;
import com.pug.project.domain.vos.EnrollmentIdentifier;
import com.pug.project.service.AttendanceService;
import com.pug.project.service.ProjectService;
import com.pug.project.service.dtos.AttendanceCreateCommand;
import com.pug.project.service.dtos.AttendanceValidateCommand;
import com.pug.project.service.utils.AttendanceProcessor;
import com.pug.project.service.utils.ExceptionHelper;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.exceptions.AppValidationException;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link AttendanceService} command interface.
 *
 * <p>Orchestrates state mutations for attendance records, coordinating with domain services and
 * utilizing the {@link AttendanceProcessor} for logic encapsulation.
 */
@ApplicationScoped
public class AttendanceServiceImpl implements AttendanceService {

  private static final Logger LOG = Logger.getLogger(AttendanceServiceImpl.class);

  @Inject AttendanceRepository repo;
  @Inject ProjectService projectService;
  @Inject StudentService studentService;
  @Inject AccountService accountService;
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
    return repo.deleteAllByEnrollmentId(identifier.getProjectId(), identifier.getStudentId());
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean delete(UUID id) {
    if (id == null) {
      return false;
    }
    return repo.deleteById(id);
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
    Project project = projectService.getById(cmd.projectId());
    Student student = studentService.getById(cmd.studentId());

    String qrHash = generateQrHash(cmd.projectId(), cmd.studentId());

    Attendance attendance =
        AttendanceProcessor.processCreateInput(project, student, cmd.duration(), qrHash);

    if (attendance.hasFieldErrors()) {
      throw new AppValidationException(attendance.getFieldErrors());
    }

    return repo.persist(attendance);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Attendance validate(UUID id, AttendanceValidateCommand cmd) {
    Attendance current = getById(id);
    UUID validatorAccountId = authService.getCurrentAccountId();
    authService.requireCurrentAccountNotOfType(AccountType.STUDENT);

    if (!current.getQrValidationInfo().getQrValidationHash().equals(cmd.qrValidationHash())) {
      throw ExceptionHelper.attendanceNotFound();
    }

    Attendance validated =
        AttendanceProcessor.processValidationInput(current, validatorAccountId, cmd.status());

    if (validated.hasFieldErrors()) {
      throw new AppValidationException(validated.getFieldErrors());
    }

    repo.update(validated);
    return getById(id);
  }

  /** Generates a unique QR hash based on project, student, timestamp, and system pepper. */
  private String generateQrHash(UUID projectId, UUID studentId) {
    String raw = projectId.toString() + studentId.toString() + LocalDateTime.now() + pepper;
    return BcryptUtil.bcryptHash(raw);
  }
}
