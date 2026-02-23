package com.pug.academic.service.impl;

import com.pug.academic.domain.Student;
import com.pug.academic.domain.StudentRepository;
import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.service.CourseService;
import com.pug.academic.service.StudentService;
import com.pug.academic.service.dtos.StudentCreateCommand;
import com.pug.academic.service.dtos.StudentUpdateCommand;
import com.pug.academic.service.utils.StudentProcessor;
import com.pug.identity.domain.Account;
import com.pug.identity.service.AccountService;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.jboss.logging.Logger;

/** Service class for managing Student entities. */
@ApplicationScoped
public class StudentServiceImpl implements StudentService {

  private static final Logger LOG = Logger.getLogger(StudentServiceImpl.class);

  @Inject StudentRepository repo;

  @Inject AccountService accountService;

  @Inject CourseService courseService;

  @Transactional
  @Override
  public Student save(StudentCreateCommand cmd) {
    LOG.debugf("Attempting to create Student with registration: %s", cmd.academicRegistration());
    courseService.getById(cmd.courseId());
    Account account = accountService.save(cmd.accountCreateCommand());

    Student studentToPersist =
        StudentProcessor.processCreateInput(
            account.getId(),
            cmd.academicRegistration(),
            cmd.campus(),
            cmd.courseId(),
            cmd.requiredHours(),
            cmd.startDate(),
            cmd.dueDate());

    if (studentToPersist.hasErrors()) {
      throw new AppValidationException(studentToPersist.getProblems());
    }

    if (existsByRegistration(studentToPersist.getAcademicRegistration().toString())) {
      LOG.warnf(
          "Creation failed: Student with registration %s already exists",
          studentToPersist.getAcademicRegistration());
      throw new DuplicateResourceException(
          AcademicErrorCodes.STUDENT_ALREADY_EXISTS,
          "academicRegistration",
          studentToPersist.getAcademicRegistration().toString());
    }

    Student savedStudent = repo.persist(studentToPersist);
    LOG.infof("Student created successfully. Account ID: %s", savedStudent.getAccountId());
    return savedStudent;
  }

  @Transactional
  @Override
  public Student update(UUID accountId, StudentUpdateCommand cmd) {
    LOG.debugf("Attempting to update Student Account ID: %s", accountId);
    Student current = getById(accountId);

    if (cmd.accountUpdateCommand() != null) {
      accountService.update(accountId, cmd.accountUpdateCommand());
    }
    if (cmd.courseId() != null && !cmd.courseId().equals(current.getCourseId())) {
      courseService.getById(cmd.courseId());
    }

    Student studentToUpdate =
        StudentProcessor.processUpdateInput(
            current,
            cmd.academicRegistration(),
            cmd.campus(),
            cmd.courseId(),
            cmd.requiredHours(),
            cmd.startDate(),
            cmd.dueDate());

    if (studentToUpdate.hasErrors()) {
      throw new AppValidationException(studentToUpdate.getProblems());
    }

    if (cmd.academicRegistration() != null
        && !cmd.academicRegistration().equals(current.getAcademicRegistration().toString())
        && existsByRegistration(cmd.academicRegistration())) {
      LOG.warnf(
          "Update failed: Student Account ID %s tried to use existing registration %s",
          accountId, cmd.academicRegistration());
      throw new DuplicateResourceException(
          AcademicErrorCodes.STUDENT_ALREADY_EXISTS,
          "academicRegistration",
          cmd.academicRegistration());
    }

    repo.update(studentToUpdate);
    LOG.infof("Student updated successfully. Account ID: %s", accountId);
    return getById(accountId);
  }

  @Transactional
  @Override
  public boolean delete(UUID accountId) {
    LOG.debugf("Attempting to delete Student Account ID: %s", accountId);
    if (accountId == null) {
      return false;
    }

    boolean deleted = repo.deleteById(accountId);

    if (deleted) {
      LOG.infof("Student deleted successfully. Account ID: %s", accountId);
      accountService.delete(accountId);
    } else {
      LOG.debugf("Delete failed: Student Account ID %s not found (idempotent)", accountId);
    }

    return deleted;
  }

  @Override
  public Student getById(UUID accountId) {
    Student student =
        repo.findOptionalById(accountId)
            .orElseThrow(
                () -> {
                  LOG.debugf("Student lookup failed: Account ID %s not found", accountId);
                  return new ResourceNotFoundException(
                      AcademicErrorCodes.STUDENT_NOT_FOUND, "accountId", accountId.toString());
                });

    if (student.hasErrors()) {
      LOG.errorf(
          "DATA CORRUPTION DETECTED: Student %s violates domain rules: %s",
          accountId, student.getProblemsSummary());
      throw new ResourceNotFoundException(
          AcademicErrorCodes.STUDENT_NOT_FOUND, "accountId", accountId.toString());
    }

    return student;
  }

  /* --------------- INTERNAL HELPER METHODS --------------- */

  /**
   * Checks if a student with the given academic registration already exists.
   *
   * @param registration the academic registration string to check for existence.
   * @return true if a student with the given registration exists, false otherwise.
   */
  private boolean existsByRegistration(String registration) {
    if (StringUtils.isEmpty(registration)) {
      return false;
    }
    return repo.existsByRegistration(registration);
  }
}
