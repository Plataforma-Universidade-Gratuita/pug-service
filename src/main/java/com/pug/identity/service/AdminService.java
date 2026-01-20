package com.pug.identity.service;

import com.pug.identity.domain.Account;
import com.pug.identity.domain.Admin;
import com.pug.identity.domain.AdminRepository;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.service.dtos.AdminCreateCommand;
import com.pug.identity.service.dtos.AdminUpdateCommand;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.time.TimeProvider;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Service for managing admins. */
@ApplicationScoped
public class AdminService {

  @Inject AdminRepository adminsRepo;
  @Inject AccountService accountService;
  @Inject TimeProvider time;

  /**
   * Creates and saves a new Admin.
   *
   * <p>This method also creates and saves the associated Account.
   *
   * @param cmd the command containing the data to create the new Admin.
   * @return the saved Admin.
   */
  @Transactional
  public Admin save(AdminCreateCommand cmd) {
    var account = accountService.save(cmd.accountCommand());
    var admin = Admin.createNew(account.getId(), time);
    return adminsRepo.persist(admin);
  }

  /**
   * Creates and saves multiple new Admins.
   *
   * <p>This method also creates and saves the associated Accounts.
   *
   * @param cmds the commands containing the data to create the new Admins.
   * @return the list of saved Admins.
   */
  @Transactional
  public List<Admin> saveAll(Iterable<AdminCreateCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }

    var accountCmds =
        CollectionUtils.toStream(cmds).map(AdminCreateCommand::accountCommand).toList();
    var accounts = accountService.saveAll(accountCmds);

    var admins = accounts.stream().map(a -> Admin.createNew(a.getId(), time)).toList();

    return adminsRepo.persistAll(admins);
  }

  /**
   * Updates an existing Admin.
   *
   * <p>This method also updates the associated Account.
   *
   * @param id the ID of the Admin to update.
   * @param cmd the command containing the data to update the Admin.
   * @return the updated Admin.
   * @throws ResourceNotFoundException if the Admin with the given ID does not exist.
   */
  @Transactional
  public Admin update(UUID id, AdminUpdateCommand cmd) {
    Account updated = accountService.update(id, cmd.accountCommand());
    return getById(updated.getId());
  }

  /**
   * Deletes all Admins with the given IDs.
   *
   * <p>This method also deletes the associated Accounts.
   *
   * @param ids the IDs of the Admins to delete.
   * @return a map containing the count of deleted Admins and Accounts.
   */
  @Transactional
  public Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return Map.of(
          DeleteKeys.ADMINS, 0L,
          DeleteKeys.ACCOUNTS, 0L,
          DeleteKeys.USERS, 0L);
    }

    var admins = adminsRepo.deleteByIds(ids);
    var accounts = accountService.deleteAll(ids);

    return Map.of(
        DeleteKeys.ADMINS, admins,
        DeleteKeys.ACCOUNTS, accounts.getOrDefault(DeleteKeys.ACCOUNTS, 0L),
        DeleteKeys.USERS, accounts.getOrDefault(DeleteKeys.USERS, 0L));
  }

  /**
   * Retrieves an Admin by account ID.
   *
   * @param accountId the UUID of the account.
   * @return the Admin entity.
   * @throws ResourceNotFoundException if the Admin with the given account ID does not exist.
   */
  public Admin getById(UUID accountId) {
    return adminsRepo
        .findOptionalById(accountId)
        .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND));
  }

  /**
   * Lists all Admin entities.
   *
   * @return a list of all Admin entities.
   */
  public List<Admin> listAll() {
    return adminsRepo.listAllAdmins();
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
