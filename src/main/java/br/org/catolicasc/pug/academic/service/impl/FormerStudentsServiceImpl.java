package br.org.catolicasc.pug.academic.service.impl;

import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.domain.FormerStudentRepository;
import br.org.catolicasc.pug.academic.service.CoursesService;
import br.org.catolicasc.pug.academic.service.FormerStudentsService;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentUpdateCommand;
import br.org.catolicasc.pug.academic.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.academic.service.utils.FormerStudentProcessor;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.service.AccountsService;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountUpdateCommand;
import br.org.catolicasc.pug.project.service.EnrollmentsService;
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
 * Implementation of the {@link FormerStudentsService} command interface.
 *
 * <p>This application-scoped service orchestrates state mutations for formerStudent enrollments. Because
 * a formerStudent is inherently an extension of an authentication account tied to a specific course, this
 * service delegates identity concerns down to the {@link AccountsService} and structural course
 * validations to the {@link CoursesService}.
 */
@ApplicationScoped
public class FormerStudentsServiceImpl implements FormerStudentsService {

  private static final Logger LOG = Logger.getLogger(FormerStudentsServiceImpl.class);

  @Inject AuditPublisher auditPublisher;

  @Inject FormerStudentRepository repo;

  @Inject AccountsService accountService;

  @Inject CoursesService courseService;

  @Inject EnrollmentsService enrollmentsService;

  /** {@inheritDoc} */
  @Transactional
  @Override
  public FormerStudent addCompletedHours(UUID accountId, BigDecimal hours) {
    LOG.debugf("Adicionando %s horas completadas ao estudante: %s", hours, accountId);
    FormerStudent current = getById(accountId);

    FormerStudent updated = current.addCompletedHours(hours);

    if (updated.hasFieldErrors()) {
      throw new AppValidationException(updated.getFieldErrors());
    }

    repo.update(updated);
    if (Boolean.FALSE.equals(current.getCounterpartHours().getConcluded())
        && Boolean.TRUE.equals(updated.getCounterpartHours().getConcluded())) {
      enrollmentsService.completeAllByStudentId(accountId);
    }
    LOG.infof("Horas adicionadas com sucesso ao estudante %s", accountId);
    return updated;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean delete(UUID accountId) {
    LOG.debugf("Attempting to delete FormerStudent Account ID: %s", accountId);
    if (accountId == null) {
      return false;
    }

    if (enrollmentsService.existsAnyByStudentId(accountId)) {
      LOG.warnf("Delete failed: FormerStudent ID %s is enrolled in projects", accountId);
      throw ExceptionHelper.studentHasEnrollments();
    }

    boolean deleted = repo.deleteById(accountId);

    if (deleted) {
      LOG.infof("FormerStudent deleted successfully. Account ID: %s", accountId);
      accountService.delete(accountId);
      auditPublisher.fireDelete(FormerStudent.class.getName(), accountId);
    } else {
      LOG.debugf("Delete failed: FormerStudent Account ID %s not found (idempotent)", accountId);
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
  public FormerStudent getById(UUID accountId) {
    FormerStudent formerStudent =
        repo.findOptionalById(accountId)
            .orElseThrow(
                () -> {
                  LOG.debugf("FormerStudent lookup failed: Account ID %s not found", accountId);
                  return ExceptionHelper.studentNotFound();
                });

    if (formerStudent.hasFieldErrors()) {
      LOG.errorf(
          "DATA CORRUPTION DETECTED: FormerStudent %s violates domain rules: %s",
          accountId, formerStudent.getProblemsSummary());
      throw ExceptionHelper.studentNotFound();
    }

    return formerStudent;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public FormerStudent save(FormerStudentCreateCommand cmd) {
    LOG.debugf("Attempting to create FormerStudent with registration: %s", cmd.academicRegistration());
    courseService.getById(cmd.courseId());
    Account account = accountService.save(cmd.accountCreateCommand());

    FormerStudent studentToPersist =
        FormerStudentProcessor.processCreateInput(
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

    if (existsByRegistration(studentToPersist.getAcademicRegistration().getValue())) {
      LOG.warnf(
          "Creation failed: FormerStudent with registration %s already exists",
          studentToPersist.getAcademicRegistration());
      throw ExceptionHelper.studentAlreadyExists();
    }

    FormerStudent savedStudent = repo.persist(studentToPersist);
    LOG.infof("FormerStudent created successfully. Account ID: %s", savedStudent.getAccountId());

    auditPublisher.fireCreate(FormerStudent.class.getName(), savedStudent.getAccountId());
    return savedStudent;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public List<FormerStudent> saveInBulk(List<FormerStudentCreateCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }
    LOG.debugf("Attempting to bulk create %d Students", cmds.size());

    cmds.stream().map(FormerStudentCreateCommand::courseId).distinct().forEach(courseService::getById);

    List<String> registrations =
        cmds.stream().map(FormerStudentCreateCommand::academicRegistration).toList();
    long uniqueCount = registrations.stream().distinct().count();

    if (uniqueCount < cmds.size() || repo.existsAnyByRegistrations(registrations)) {
      LOG.warn(
          "Bulk creation failed: Duplicate academic registrations detected in payload or database");
      throw ExceptionHelper.studentAlreadyExists();
    }

    List<AccountCreateCommand> accountCmds =
        cmds.stream().map(FormerStudentCreateCommand::accountCreateCommand).toList();

    List<Account> createdAccounts = accountService.saveInBulk(accountCmds);
    List<UUID> accountIds = createdAccounts.stream().map(Account::getId).toList();

    List<FormerStudent> studentsToPersist = FormerStudentProcessor.processBulkCreateInput(cmds, accountIds);

    List<FormerStudent> savedStudents = repo.persistAll(studentsToPersist);
    LOG.infof("Successfully bulk created %d Students", savedStudents.size());

    return savedStudents;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public FormerStudent update(UUID accountId, FormerStudentUpdateCommand cmd) {
    LOG.debugf("Attempting to update FormerStudent Account ID: %s", accountId);
    FormerStudent current = getById(accountId);

    if (cmd.accountUpdateCommand() != null) {
      accountService.update(accountId, cmd.accountUpdateCommand());
    }
    if (cmd.courseId() != null && !cmd.courseId().equals(current.getCourseId())) {
      courseService.getById(cmd.courseId());
    }

    FormerStudent studentToUpdate =
        FormerStudentProcessor.processUpdateInput(
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
        && !cmd.academicRegistration().equals(current.getAcademicRegistration().getValue())
        && existsByRegistration(cmd.academicRegistration())) {
      LOG.warnf(
          "Update failed: FormerStudent Account ID %s tried to use existing registration %s",
          accountId, cmd.academicRegistration());
      throw ExceptionHelper.studentAlreadyExists();
    }

    repo.update(studentToUpdate);
    LOG.infof("FormerStudent updated successfully. Account ID: %s", accountId);

    auditPublisher.fireUpdate(FormerStudent.class.getName(), accountId, current, studentToUpdate);
    return getById(accountId);
  }

  @Transactional
  @Override
  public FormerStudent updateStatus(UUID accountId, boolean active) {
    FormerStudent current = getById(accountId);
    accountService.update(accountId, new AccountUpdateCommand(null, null, active, null));
    FormerStudent updated = getById(accountId);
    auditPublisher.fireUpdate(FormerStudent.class.getName(), accountId, current, updated);
    return updated;
  }

  /* --------------- INTERNAL HELPER METHODS --------------- */

  /**
   * Checks if a formerStudent with the given academic registration already exists.
   *
   * @param registration the academic registration string to check
   * @return {@code true} if a formerStudent with the given registration exists, {@code false} otherwise
   */
  private boolean existsByRegistration(String registration) {
    if (StringUtils.isEmpty(registration)) {
      return false;
    }
    return repo.existsByRegistration(registration);
  }
}

