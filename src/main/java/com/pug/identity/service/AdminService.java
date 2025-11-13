package com.pug.identity.service;

import com.pug.identity.domain.Admin;
import com.pug.identity.domain.AdminRepository;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.service.dtos.CreateAdminCommand;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.time.TimeProvider;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
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
  public Admin save(CreateAdminCommand cmd) {
    var account = accountService.save(cmd.accountCommand());
    var admin =
        Admin.builder()
            .accountId(account.getId())
            .grantedAt(OffsetDateTime.now(time.clock()))
            .build();

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
  public List<Admin> saveAll(Iterable<CreateAdminCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }

    var accountCmds =
        CollectionUtils.toStream(cmds).map(CreateAdminCommand::accountCommand).toList();
    var accounts = accountService.saveAll(accountCmds);

    var now = java.time.OffsetDateTime.now(time.clock());
    var admins =
        accounts.stream()
            .map(a -> Admin.builder().accountId(a.getId()).grantedAt(now).build())
            .toList();

    return adminsRepo.persistAll(admins);
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
   * Retrieves an Admin by user ID.
   *
   * @param userId the UUID of the user.
   * @return the Admin entity.
   * @throws ResourceNotFoundException if the Admin with the given user ID does not exist.
   */
  public Admin get(UUID userId) {
    return adminsRepo
        .findOptionalById(userId)
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
