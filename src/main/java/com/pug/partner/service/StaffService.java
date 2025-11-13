package com.pug.partner.service;

import com.pug.identity.domain.Account;
import com.pug.identity.service.AccountService;
import com.pug.partner.domain.Staff;
import com.pug.partner.domain.StaffRepository;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.service.dtos.StaffCreateBulkCommand;
import com.pug.partner.service.dtos.StaffCreateCommand;
import com.pug.partner.service.dtos.StaffUpdateCommand;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing staff assignments to partner entities.
 */
@ApplicationScoped
public class StaffService {

  @Inject
  StaffRepository repo;
  @Inject
  AccountService accountService;
  @Inject
  EntityService entityService;

  /**
   * Save a new staff member by creating an account and linking them to an entity.
   *
   * @param cmd the command containing staff creation details.
   * @return the created Staff object.
   * @throws DuplicateResourceException if a staff member with the same user ID already exists.
   * @throws ResourceNotFoundException  if the specified entity does not exist.
   */
  @Transactional
  public Staff save(StaffCreateCommand cmd) {
    entityService.getById(cmd.entityId());
    var account =
            accountService.save(cmd.accountCommand());

    if (existsByAccountId(account.getId())) {
      throw new DuplicateResourceException(PartnerErrorCodes.STAFF_ALREADY_EXISTS);
    }

    return repo.persist(Staff.createNew(account.getId(), cmd.entityId()));
  }

  /**
   * Saves multiple staff members in bulk by creating accounts and linking them to entities.
   *
   * @param cmds an iterable of commands containing staff creation details.
   * @return a list of created Staff objects.
   * @throws DuplicateResourceException if any staff member with the same user ID already exists.
   * @throws ResourceNotFoundException  if any specified entity does not exist.
   */
  @Transactional
  public List<Staff> saveAll(Iterable<StaffCreateBulkCommand> cmds) {
    var entityIds = new LinkedHashSet<UUID>();
    for (StaffCreateBulkCommand cmd : cmds) {
      entityIds.add(cmd.entityId());
    }
    if (!entityService.existsAnyByIdIn(entityIds)) {
      throw new ResourceNotFoundException(PartnerErrorCodes.ENTITY_NOT_FOUND);
    }

    List<Staff> staffList = new ArrayList<>();
    for (StaffCreateBulkCommand cmd : cmds) {
      var accountsIds =
              accountService.saveAll(cmd.accountCommands()).stream().map(Account::getId).toList();

      if (existsAnyByAccountIdIn(accountsIds)) {
        throw new DuplicateResourceException(PartnerErrorCodes.STAFF_ALREADY_EXISTS);
      }

      for (UUID accountId : accountsIds) {
        staffList.add(Staff.createNew(accountId, cmd.entityId()));
      }
    }
    return repo.persistAll(staffList);
  }

  /**
   * Updates an existing staff member's account details.
   *
   * @param id  the ID of the staff user to update.
   * @param cmd the command containing updated staff details.
   * @return the updated Staff object.
   * @throws ResourceNotFoundException if the specified staff member does not exist.
   */
  @Transactional
  public Staff update(UUID id, StaffUpdateCommand cmd) {
    Account updated = accountService.update(id, cmd.accountCommand());
    return getById(updated.getId());
  }

  /**
   * Deletes all staff members with the specified IDs.
   *
   * @param ids an iterable of staff user IDs to delete.
   * @return a map containing the count of deleted staff, accounts, and users.
   */
  @Transactional
  public Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return Map.of(
              DeleteKeys.STAFF, 0L,
              DeleteKeys.ACCOUNTS, 0L,
              DeleteKeys.USERS, 0L
      );
    }

    var deletedStaff = repo.deleteByIds(ids);
    var deletedAccounts = accountService.deleteAll(ids);
    return Map.of(
            DeleteKeys.STAFF, deletedStaff,
            DeleteKeys.ACCOUNTS, deletedAccounts.getOrDefault(DeleteKeys.ACCOUNTS, 0L),
            DeleteKeys.USERS, deletedAccounts.getOrDefault(DeleteKeys.USERS, 0L)
    );
  }

  /**
   * Retrieves a staff member by their user ID.
   *
   * @param id the ID of the staff user.
   * @return the Staff object.
   * @throws ResourceNotFoundException if the specified staff member does not exist.
   */
  public Staff getById(UUID id) {
    return repo.findOptionalById(id)
            .orElseThrow(() -> new ResourceNotFoundException(PartnerErrorCodes.STAFF_NOT_FOUND));
  }

  /**
   * Lists all staff members.
   *
   * @return a list of all Staff objects.
   */
  public List<Staff> listAll() {
    return repo.listAllStaff();
  }

  /**
   * Lists all staff members associated with a specific entity.
   *
   * @param entityId the ID of the entity.
   * @return a list of Staff objects linked to the specified entity.
   */
  public List<Staff> listByEntity(UUID entityId) {
    if (entityId == null) {
      return List.of();
    }
    return repo.listAllByEntityId(entityId);
  }

  /**
   * Checks if a staff member exists by their account ID.
   *
   * @param accountId the ID of the staff account.
   * @return true if the staff member exists, false otherwise.
   */
  public boolean existsByAccountId(UUID accountId) {
    if (accountId == null) {
      return false;
    }
    return repo.existsByAccountId(accountId);
  }

  /**
   * Checks if any staff members exist for the given account IDs.
   *
   * @param accountIds an iterable of account IDs to check.
   * @return true if any staff members exist for the provided account IDs, false otherwise.
   */
  public boolean existsAnyByAccountIdIn(Iterable<UUID> accountIds) {
    if (CollectionUtils.isEmpty(accountIds)) {
      return false;
    }
    return repo.existsAnyByAccountIdIn(accountIds);
  }
}
