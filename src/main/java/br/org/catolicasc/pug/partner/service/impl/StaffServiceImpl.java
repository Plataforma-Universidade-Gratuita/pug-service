package br.org.catolicasc.pug.partner.service.impl;

import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.vos.Email;
import br.org.catolicasc.pug.identity.service.AccountsService;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountUpdateCommand;
import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.partner.domain.StaffRepository;
import br.org.catolicasc.pug.partner.service.EntitiesService;
import br.org.catolicasc.pug.partner.service.StaffService;
import br.org.catolicasc.pug.partner.service.dtos.staff.StaffCreateCommand;
import br.org.catolicasc.pug.partner.service.dtos.staff.StaffUpdateCommand;
import br.org.catolicasc.pug.partner.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.partner.service.utils.StaffProcessor;
import br.org.catolicasc.pug.project.service.AttendanceService;
import br.org.catolicasc.pug.project.service.ProjectService;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/** Implementation of the {@link StaffService} command interface. */
@ApplicationScoped
public class StaffServiceImpl implements StaffService {

  private static final Logger LOG = Logger.getLogger(StaffServiceImpl.class);

  @Inject AuditPublisher auditPublisher;
  @Inject StaffRepository repo;
  @Inject AccountsService accountService;
  @Inject EntitiesService entityService;
  @Inject ProjectService projectService;
  @Inject AttendanceService attendanceService;

  @Transactional
  @Override
  public boolean delete(UUID accountId) {
    LOG.debugf("Attempting to hard-delete Staff Account ID: %s", accountId);
    if (accountId == null) {
      return false;
    }

    if (projectService.existsByCreatedBy(accountId)) {
      throw ExceptionHelper.staffHasProjects();
    }
    if (attendanceService.existsByValidatedBy(accountId)) {
      throw ExceptionHelper.staffHasAttendances();
    }

    boolean deleted = repo.deleteByAccountId(accountId);
    if (deleted) {
      auditPublisher.fireDelete(Staff.class.getName(), accountId);
      accountService.delete(accountId);
    }

    return deleted;
  }

  @Transactional
  @Override
  public long deleteAllByEntityId(UUID entityId) {
    if (entityId == null) {
      return 0;
    }

    List<Staff> staffList = repo.listAllByEntityId(entityId);
    if (staffList.isEmpty()) {
      return 0;
    }

    List<UUID> accountIds = staffList.stream().map(Staff::getAccountId).toList();
    long deletedCount = repo.deleteByEntityId(entityId);
    accountService.deleteAll(accountIds);
    return deletedCount;
  }

  @Override
  public Staff getByAccountId(UUID accountId) {
    Staff staff =
        repo.findOptionalByAccountId(accountId).orElseThrow(ExceptionHelper::staffNotFound);

    if (staff.hasFieldErrors()) {
      throw ExceptionHelper.staffNotFound();
    }

    return staff;
  }

  @Transactional
  @Override
  public Staff save(StaffCreateCommand cmd) {
    entityService.getById(cmd.entityId());
    Account account = accountService.save(cmd.accountCommand());

    repo.findOptionalByAccountId(account.getId())
        .ifPresent(
            existingStaff -> {
              if (existingStaff.getEntityId().equals(cmd.entityId())) {
                throw ExceptionHelper.staffAlreadyExists();
              }
              throw ExceptionHelper.staffAssignedToOtherEntity();
            });

    Staff staff = StaffProcessor.processCreateInput(account.getId(), cmd.entityId());
    if (staff.hasFieldErrors()) {
      throw new AppValidationException(staff.getFieldErrors());
    }

    Staff savedStaff = repo.persist(staff);
    auditPublisher.fireCreate(Staff.class.getName(), savedStaff.getAccountId());
    return savedStaff;
  }

  @Transactional
  @Override
  public Staff update(UUID accountId, StaffUpdateCommand cmd) {
    Staff current = getByAccountId(accountId);
    Account currentAccount = accountService.getById(accountId);

    if (cmd.entityId() != null && !cmd.entityId().equals(current.getEntityId())) {
      entityService.getById(cmd.entityId());
      String effectiveEmail = resolveEffectiveEmail(currentAccount, cmd);
      if (repo.existsAnotherByEntityIdAndEmail(cmd.entityId(), effectiveEmail, accountId)) {
        throw ExceptionHelper.staffEmailAlreadyExistsInEntity();
      }
    }

    if (cmd.accountCommand() != null) {
      accountService.update(accountId, cmd.accountCommand());
    }

    if (cmd.entityId() != null && !cmd.entityId().equals(current.getEntityId())) {
      Staff moved = current.moveToEntity(cmd.entityId());
      if (moved.hasFieldErrors()) {
        throw new AppValidationException(moved.getFieldErrors());
      }
      repo.update(moved);
    }

    Staff updated = getByAccountId(accountId);
    auditPublisher.fireUpdate(Staff.class.getName(), accountId, current, updated);
    return updated;
  }

  @Transactional
  @Override
  public Staff updateStatus(UUID accountId, boolean active) {
    Staff current = getByAccountId(accountId);
    accountService.update(accountId, new AccountUpdateCommand(null, null, active, null));
    Staff updated = getByAccountId(accountId);
    auditPublisher.fireUpdate(Staff.class.getName(), accountId, current, updated);
    return updated;
  }

  private String resolveEffectiveEmail(Account currentAccount, StaffUpdateCommand cmd) {
    if (cmd.accountCommand() == null || StringUtils.isEmpty(cmd.accountCommand().emailString())) {
      return currentAccount.getEmail().getValue();
    }
    return Email.factory(cmd.accountCommand().emailString()).getValue();
  }
}
