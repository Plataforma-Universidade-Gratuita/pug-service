package com.pug.identity.service;

import com.pug.identity.domain.Account;
import com.pug.identity.domain.Admin;
import com.pug.identity.domain.AdminRepository;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.service.dtos.AdminCreateCommand;
import com.pug.identity.service.dtos.AdminUpdateCommand;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.ReferencedEntityException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.time.TimeProvider;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing admins.
 */
@ApplicationScoped
public class AdminService {

  private static final Logger LOG = Logger.getLogger(AdminService.class);

  @Inject
  AdminRepository adminsRepo;
  @Inject
  AccountService accountService;
  @Inject
  TimeProvider time;

  /**
   * Helper method to create an Admin domain object from an account ID,
   * collecting all validation problems.
   *
   * @param accountId The ID of the associated account.
   * @param problems  List to collect AppValidationException.Problem instances.
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

  /**
   * Creates and saves a new Admin.
   *
   * <p>This method also creates and saves the associated Account.
   *
   * @param cmd the command containing the data to create the new Admin.
   * @return the saved Admin.
   * @throws AppValidationException if input validation fails for account or admin data.
   */
  @Transactional
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

  /**
   * Creates and saves multiple new Admins.
   *
   * <p>This method also creates and saves the associated Accounts.
   *
   * @param cmds the commands containing the data to create the new Admins.
   * @return the list of saved Admins.
   * @throws AppValidationException if input validation fails for any admin or account in the bulk.
   */
  @Transactional
  public List<Admin> saveAll(Iterable<AdminCreateCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }

    List<AppValidationException.Problem> allCollectedProblems = new ArrayList<>();
    List<Admin> adminsToPersist = new ArrayList<>();

    List<Account> accounts = new ArrayList<>();
    try {
      var accountCmds = CollectionUtils.toStream(cmds).map(AdminCreateCommand::accountCommand).toList();
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

  /**
   * Updates an existing Admin.
   *
   * <p>This method also updates the associated Account.
   *
   * @param id  the ID of the Admin to update.
   * @param cmd the command containing the data to update the Admin.
   * @return the updated Admin.
   * @throws ResourceNotFoundException if the Admin with the given ID does not exist (or data is corrupted in DB).
   * @throws AppValidationException    if input validation fails for account data.
   */
  @Transactional
  public Admin update(UUID id, AdminUpdateCommand cmd) {
    Account updatedAccount = null;
    updatedAccount = accountService.update(id, cmd.accountCommand());

    return getById(updatedAccount.getId());
  }

  /**
   * Deletes all Admins with the given IDs.
   *
   * <p>This method also deletes the associated Accounts.
   *
   * @param ids the IDs of the Admins to delete.
   * @return a map containing the count of deleted Admins and Accounts.
   * @throws ReferencedEntityException if any account is still referenced by Admin, Staff, or
   *                                   Student entities (this check is done by AccountService).
   */
  @Transactional
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

  /**
   * Retrieves an Admin by account ID.
   *
   * @param accountId the UUID of the account.
   * @return the Admin entity.
   * @throws ResourceNotFoundException if the Admin with the given account ID does not exist (or data is corrupted in DB).
   */
  public Admin getById(UUID accountId) {
    try {
      return adminsRepo
              .findOptionalById(accountId)
              .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND, Map.of("accountId", accountId)));
    } catch (AppValidationException e) {
      LOG.errorf(e, "Data integrity error: Admin with Account ID %s in DB violates domain rules. Problems: %s",
              accountId, e.getProblems().stream().map(p -> p.code().getBundleKey() + (p.fieldName() != null ? "(" + p.fieldName() + ")" : "")).collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND, Map.of("accountId", accountId));
    }
  }

  /**
   * Lists all Admin entities.
   *
   * @return a list of all Admin entities.
   * @throws AppValidationException if any Admin entity found is corrupted in the database.
   */
  public List<Admin> listAll() {
    try {
      return adminsRepo.listAllAdmins();
    } catch (AppValidationException e) {
      LOG.errorf(e, "Data integrity error: Corrupted Admin entity found in DB. Problems: %s",
              e.getProblems().stream().map(p -> p.code().getBundleKey() + (p.fieldName() != null ? "(" + p.fieldName() + ")" : "")).collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND);
    }
  }

  /**
   * Checks if any Admin exists with account IDs in the provided iterable.
   *
   * @param ids the iterable of account IDs to check.
   * @return true if any Admin exists with the given account IDs, false otherwise.
   */
  public boolean existsAnyByAccountIdIn(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return false;
    }
    return adminsRepo.existsAnyByAccountIdIn(ids);
  }
}