package br.org.catolicasc.pug.academic.service.impl;

import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.academic.domain.StudentRepository;
import br.org.catolicasc.pug.academic.service.CourseService;
import br.org.catolicasc.pug.academic.service.StudentService;
import br.org.catolicasc.pug.academic.service.dtos.StudentCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.StudentUpdateCommand;
import br.org.catolicasc.pug.academic.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.academic.service.utils.StudentProcessor;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.service.AccountService;
import br.org.catolicasc.pug.identity.service.dtos.AccountCreateCommand;
import br.org.catolicasc.pug.project.service.EnrollmentService;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link StudentService} command interface.
 *
 * <p>This application-scoped service orchestrates state mutations for student enrollments. Because
 * a student is inherently an extension of an authentication account tied to a specific course, this
 * service delegates identity concerns down to the {@link AccountService} and structural course
 * validations to the {@link CourseService}.
 */
@ApplicationScoped
public class StudentServiceImpl implements StudentService {

  private static final Logger LOG = Logger.getLogger(StudentServiceImpl.class);

  @Inject AuditPublisher auditPublisher;

  @Inject StudentRepository repo;

  @Inject AccountService accountService;

  @Inject CourseService courseService;

  @Inject EnrollmentService enrollmentService;

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Student addCompletedHours(UUID accountId, BigDecimal hours) {
    LOG.debugf("Adicionando %s horas completadas ao estudante: %s", hours, accountId);
    Student current = getById(accountId);

    Student updated = current.addCompletedHours(hours);

    if (updated.hasFieldErrors()) {
      throw new AppValidationException(updated.getFieldErrors());
    }

    repo.update(updated);
    LOG.infof("Horas adicionadas com sucesso ao estudante %s", accountId);
    return updated;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean delete(UUID accountId) {
    LOG.debugf("Attempting to delete Student Account ID: %s", accountId);
    if (accountId == null) {
      return false;
    }

    if (enrollmentService.existsAnyByStudentId(accountId)) {
      LOG.warnf("Delete failed: Student ID %s is enrolled in projects", accountId);
      throw ExceptionHelper.studentHasEnrollments();
    }

    boolean deleted = repo.deleteById(accountId);

    if (deleted) {
      LOG.infof("Student deleted successfully. Account ID: %s", accountId);
      accountService.delete(accountId);
      auditPublisher.fireDelete(Student.class.getName(), accountId);
    } else {
      LOG.debugf("Delete failed: Student Account ID %s not found (idempotent)", accountId);
    }

    return deleted;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsAnyByCourseId(UUID courseId) {
    return repo.existsByCourseId(courseId);
  }

  /** {@inheritDoc} */
  @Override
  public Student getById(UUID accountId) {
    Student student =
        repo.findOptionalById(accountId)
            .orElseThrow(
                () -> {
                  LOG.debugf("Student lookup failed: Account ID %s not found", accountId);
                  return ExceptionHelper.studentNotFound();
                });

    if (student.hasFieldErrors()) {
      LOG.errorf(
          "DATA CORRUPTION DETECTED: Student %s violates domain rules: %s",
          accountId, student.getProblemsSummary());
      throw ExceptionHelper.studentNotFound();
    }

    return student;
  }

  /** {@inheritDoc} */
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

    if (studentToPersist.hasFieldErrors()) {
      throw new AppValidationException(studentToPersist.getFieldErrors());
    }

    if (existsByRegistration(studentToPersist.getAcademicRegistration().toString())) {
      LOG.warnf(
          "Creation failed: Student with registration %s already exists",
          studentToPersist.getAcademicRegistration());
      throw ExceptionHelper.studentAlreadyExists();
    }

    Student savedStudent = repo.persist(studentToPersist);
    LOG.infof("Student created successfully. Account ID: %s", savedStudent.getAccountId());

    auditPublisher.fireCreate(Student.class.getName(), savedStudent.getAccountId());
    return savedStudent;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public List<Student> saveInBulk(List<StudentCreateCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }
    LOG.debugf("Attempting to bulk create %d Students", cmds.size());

    cmds.stream().map(StudentCreateCommand::courseId).distinct().forEach(courseService::getById);

    List<String> registrations =
        cmds.stream().map(StudentCreateCommand::academicRegistration).toList();
    long uniqueCount = registrations.stream().distinct().count();

    if (uniqueCount < cmds.size() || repo.existsAnyByRegistrations(registrations)) {
      LOG.warn(
          "Bulk creation failed: Duplicate academic registrations detected in payload or database");
      throw ExceptionHelper.studentAlreadyExists();
    }

    List<AccountCreateCommand> accountCmds =
        cmds.stream().map(StudentCreateCommand::accountCreateCommand).toList();

    List<Account> createdAccounts = accountService.saveInBulk(accountCmds);
    List<UUID> accountIds = createdAccounts.stream().map(Account::getId).toList();

    List<Student> studentsToPersist = StudentProcessor.processBulkCreateInput(cmds, accountIds);

    List<Student> savedStudents = repo.persistAll(studentsToPersist);
    LOG.infof("Successfully bulk created %d Students", savedStudents.size());

    return savedStudents;
  }

  /** {@inheritDoc} */
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

    if (studentToUpdate.hasFieldErrors()) {
      throw new AppValidationException(studentToUpdate.getFieldErrors());
    }

    if (cmd.academicRegistration() != null
        && !cmd.academicRegistration().equals(current.getAcademicRegistration().toString())
        && existsByRegistration(cmd.academicRegistration())) {
      LOG.warnf(
          "Update failed: Student Account ID %s tried to use existing registration %s",
          accountId, cmd.academicRegistration());
      throw ExceptionHelper.studentAlreadyExists();
    }

    repo.update(studentToUpdate);
    LOG.infof("Student updated successfully. Account ID: %s", accountId);

    auditPublisher.fireUpdate(Student.class.getName(), accountId, current, studentToUpdate);
    return getById(accountId);
  }

  /* --------------- INTERNAL HELPER METHODS --------------- */

  /**
   * Checks if a student with the given academic registration already exists.
   *
   * @param registration the academic registration string to check
   * @return {@code true} if a student with the given registration exists, {@code false} otherwise
   */
  private boolean existsByRegistration(String registration) {
    if (StringUtils.isEmpty(registration)) {
      return false;
    }
    return repo.existsByRegistration(registration);
  }
}
