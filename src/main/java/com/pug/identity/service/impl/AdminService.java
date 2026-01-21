package com.pug.identity.service.impl;

import com.pug.identity.domain.Account;
import com.pug.identity.domain.Admin;
import com.pug.identity.domain.IAdminRepository;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.service.IAccountService;
import com.pug.identity.service.IAdminService;
import com.pug.identity.service.dtos.AdminCreateCommand;
import com.pug.identity.service.dtos.AdminUpdateCommand;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.time.TimeProvider;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;

/** Service for managing admins. */
@ApplicationScoped
public class AdminService implements IAdminService {

  private static final Logger LOG = Logger.getLogger(AdminService.class);

  @Inject IAdminRepository adminsRepo;
  @Inject IAccountService accountService;
  @Inject TimeProvider time;

  /**
   * Helper method to create an Admin domain object from an account ID, collecting all validation
   * problems.
   *
   * @param accountId The ID of the associated account.
   * @param problems List to collect AppValidationException.Problem instances.
   * @return The constructed Admin domain object if no problems, or null if problems occurred.
   */
  private Admin processAdminInput(UUID accountId, List<AppValidationException.Problem> problems) {
    Admin admin = null;
    try {
      admin = Admin.createNew(accountId, time);
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    }
    return admin;
  }

  @Transactional
  @Override
  public Admin save(AdminCreateCommand cmd) {
    List<AppValidationException.Problem> problems = new ArrayList<>();
    Account account = null;

    try {
      account = accountService.save(cmd.accountCommand());
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    }

    Admin adminToPersist = null;
    if (problems.isEmpty() && account != null) {
      adminToPersist = processAdminInput(account.getId(), problems);
    } else {
      adminToPersist = processAdminInput(null, problems);
    }

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }

    return adminsRepo.persist(adminToPersist);
  }

  @Transactional
  @Override
  public List<Admin> saveAll(Iterable<AdminCreateCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }

    List<AppValidationException.Problem> allCollectedProblems = new ArrayList<>();
    List<Admin> adminsToPersist = new ArrayList<>();

    List<Account> accounts = new ArrayList<>();
    try {
      var accountCmds =
          CollectionUtils.toStream(cmds).map(AdminCreateCommand::accountCommand).toList();
      accounts = accountService.saveAll(accountCmds);
    } catch (AppValidationException e) {
      allCollectedProblems.addAll(e.getProblems());
    }

    if (allCollectedProblems.isEmpty()) {
      for (Account account : accounts) {
        Admin admin = processAdminInput(account.getId(), allCollectedProblems);
        if (admin != null) {
          adminsToPersist.add(admin);
        }
      }
    }

    if (!allCollectedProblems.isEmpty()) {
      throw new AppValidationException(allCollectedProblems);
    }

    return adminsRepo.persistAll(adminsToPersist);
  }

  @Transactional
  @Override
  public Admin update(UUID id, AdminUpdateCommand cmd) {
    accountService.update(id, cmd.accountCommand());

    return getById(id);
  }

  @Transactional
  @Override
  public Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return Map.of(
          DeleteKeys.ADMINS, 0L,
          DeleteKeys.ACCOUNTS, 0L,
          DeleteKeys.USERS, 0L);
    }

    long adminsDeleted = adminsRepo.deleteByIds(ids);

    Map<DeleteKeys, Long> accountsDeleted = accountService.deleteAll(ids);

    return Map.of(
        DeleteKeys.ADMINS, adminsDeleted,
        DeleteKeys.ACCOUNTS, accountsDeleted.getOrDefault(DeleteKeys.ACCOUNTS, 0L),
        DeleteKeys.USERS, accountsDeleted.getOrDefault(DeleteKeys.USERS, 0L));
  }

  @Override
  public Admin getById(UUID accountId) {
    try {
      return adminsRepo
          .findOptionalById(accountId)
          .orElseThrow(
              () ->
                  new ResourceNotFoundException(
                      IdentityErrorCodes.ADMIN_NOT_FOUND, Map.of("accountId", accountId)));
    } catch (AppValidationException e) {
      LOG.errorf(
          e,
          "Data integrity error: Admin with Account ID %s in DB violates domain rules. Problems: %s",
          accountId,
          e.getProblems().stream()
              .map(
                  p ->
                      p.code().getBundleKey()
                          + (p.fieldName() != null ? "(" + p.fieldName() + ")" : ""))
              .collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(
          IdentityErrorCodes.ADMIN_NOT_FOUND, Map.of("accountId", accountId));
    }
  }

  @Override
  public List<Admin> listAll() {
    try {
      return adminsRepo.listAllAdmins();
    } catch (AppValidationException e) {
      LOG.errorf(
          e,
          "Data integrity error: Corrupted Admin entity found in DB. Problems: %s",
          e.getProblems().stream()
              .map(
                  p ->
                      p.code().getBundleKey()
                          + (p.fieldName() != null ? "(" + p.fieldName() + ")" : ""))
              .collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND);
    }
  }

  @Override
  public boolean existsAnyByAccountIdIn(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return false;
    }
    return adminsRepo.existsAnyByAccountIdIn(ids);
  }
}
