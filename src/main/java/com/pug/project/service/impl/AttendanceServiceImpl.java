package com.pug.project.service.impl;

import com.pug.academic.domain.Student;
import com.pug.academic.service.StudentService;
import com.pug.partner.service.StaffService;
import com.pug.project.domain.Attendance;
import com.pug.project.domain.AttendanceRepository;
import com.pug.project.domain.Project;
import com.pug.project.service.AttendanceService;
import com.pug.project.service.EnrollmentService;
import com.pug.project.service.ProjectService;
import com.pug.project.service.dtos.AttendanceCreateCommand;
import com.pug.project.service.dtos.AttendanceValidateCommand;
import com.pug.project.service.utils.AttendanceProcessor;
import com.pug.project.service.utils.ExceptionHelper;
import com.pug.shared.exceptions.AppValidationException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AttendanceServiceImpl implements AttendanceService {

  private static final Logger LOG = Logger.getLogger(AttendanceServiceImpl.class);

  @Inject AttendanceRepository repo;
  @Inject ProjectService projectService;
  @Inject StudentService studentService;
  @Inject EnrollmentService enrollmentService;
  @Inject StaffService staffService;

  @Transactional
  @Override
  public boolean delete(UUID id) {
    if (id == null) return false;
    return repo.deleteById(id);
  }

  @Override
  public boolean existsByValidatedBy(UUID accountId) {
    if (accountId == null) return false;
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

  @Transactional
  @Override
  public Attendance save(AttendanceCreateCommand cmd) {
    // Validate structural dependencies
    Project project = projectService.getById(cmd.projectId());
    Student student = studentService.getById(cmd.studentId());
    //    enrollmentService.getByIds(cmd.projectId(), cmd.studentId());

    Attendance attendance =
        AttendanceProcessor.processCreateInput(project, student, cmd.duration());

    if (attendance.hasFieldErrors()) {
      throw new AppValidationException(attendance.getFieldErrors());
    }

    return repo.persist(attendance);
  }

  @Transactional
  @Override
  public Attendance validate(UUID id, AttendanceValidateCommand cmd) {
    Attendance current = getById(id);

    // Ensure the validator is an active Staff member
    staffService.getByAccountId(cmd.validatorId());

    Attendance validated =
        AttendanceProcessor.processValidationInput(
            current, cmd.validatorId(), cmd.latitude(), cmd.longitude(), cmd.qrValidationHash());

    if (validated.hasFieldErrors()) {
      throw new AppValidationException(validated.getFieldErrors());
    }

    if (repo.existsByQrHash(cmd.qrValidationHash())) {
      throw ExceptionHelper.attendanceAlreadyExists(); // Hash already consumed
    }

    repo.update(validated);
    return getById(id);
  }
}
