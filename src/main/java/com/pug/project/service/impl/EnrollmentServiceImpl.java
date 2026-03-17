package com.pug.project.service.impl;

import com.pug.academic.domain.Student;
import com.pug.academic.service.StudentService;
import com.pug.project.domain.Enrollment;
import com.pug.project.domain.EnrollmentRepository;
import com.pug.project.domain.Project;
import com.pug.project.domain.enums.EnrollmentStatus;
import com.pug.project.service.EnrollmentService;
import com.pug.project.service.ProjectService;
import com.pug.project.service.dtos.EnrollmentCreateCommand;
import com.pug.project.service.utils.EnrollmentProcessor;
import com.pug.project.service.utils.ExceptionHelper;
import com.pug.shared.exceptions.AppValidationException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EnrollmentServiceImpl implements EnrollmentService {

  private static final Logger LOG = Logger.getLogger(EnrollmentServiceImpl.class);

  @Inject EnrollmentRepository repo;
  @Inject ProjectService projectService;
  @Inject StudentService studentService;

  @Transactional
  @Override
  public Enrollment accept(UUID projectId, UUID studentId) {
    return changeStatus(projectId, studentId, EnrollmentStatus.APPROVED);
  }

  @Transactional
  @Override
  public Enrollment cancel(UUID projectId, UUID studentId) {
    return changeStatus(projectId, studentId, EnrollmentStatus.CANCELED);
  }

  @Transactional
  @Override
  public Enrollment complete(UUID projectId, UUID studentId) {
    return changeStatus(projectId, studentId, EnrollmentStatus.COMPLETED);
  }

  @Transactional
  @Override
  public boolean delete(UUID projectId, UUID studentId) {
    if (projectId == null || studentId == null) return false;
    return repo.deleteByIds(projectId, studentId);
  }

  @Override
  public boolean existsAnyByStudentId(UUID studentId) {
    if (studentId == null) return false;
    return repo.existsByStudentId(studentId);
  }

  @Transactional
  @Override
  public Enrollment exit(UUID projectId, UUID studentId) {
    return changeStatus(projectId, studentId, EnrollmentStatus.EXITED);
  }

  @Override
  public Enrollment getByIds(UUID projectId, UUID studentId) {
    Enrollment enrollment =
        repo.findOptionalByIds(projectId, studentId)
            .orElseThrow(ExceptionHelper::enrollmentNotFound);

    if (enrollment.hasFieldErrors()) {
      LOG.errorf(
          "DATA CORRUPTION DETECTED: Enrollment [P:%s, S:%s]: %s",
          projectId, studentId, enrollment.getProblemsSummary());
      throw ExceptionHelper.enrollmentNotFound();
    }
    return enrollment;
  }

  @Transactional
  @Override
  public Enrollment reject(UUID projectId, UUID studentId) {
    return changeStatus(projectId, studentId, EnrollmentStatus.REJECTED);
  }

  @Transactional
  @Override
  public Enrollment remove(UUID projectId, UUID studentId) {
    return changeStatus(projectId, studentId, EnrollmentStatus.REMOVED);
  }

  @Transactional
  @Override
  public Enrollment save(EnrollmentCreateCommand cmd) {
    Project project = projectService.getById(cmd.projectId());
    Student student = studentService.getById(cmd.studentId());

    if (repo.existsByIds(cmd.projectId(), cmd.studentId())) {
      throw ExceptionHelper.enrollmentAlreadyExists();
    }

    Enrollment enrollment = EnrollmentProcessor.processCreateInput(student, project);

    if (enrollment.hasFieldErrors()) {
      throw new AppValidationException(enrollment.getFieldErrors());
    }

    return repo.persist(enrollment);
  }

  /** Centralized private helper to handle status transitions */
  private Enrollment changeStatus(UUID projectId, UUID studentId, EnrollmentStatus newStatus) {
    Enrollment current = getByIds(projectId, studentId);
    Enrollment updated = current.changeStatus(newStatus);

    if (updated.hasFieldErrors()) {
      throw new AppValidationException(updated.getFieldErrors());
    }

    repo.update(updated);
    return updated;
  }
}
