package com.pug.partner.service;

import com.pug.partner.domain.Staff;
import com.pug.partner.service.dtos.StaffCreateCommand;
import com.pug.partner.service.dtos.StaffUpdateCommand;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.DataIntegrityException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Interface for managing staff assignments to partner entities. */
public interface StaffService {

  /**
   * Save a new staff member by creating an account and linking them to an entity.
   *
   * @param cmd the command containing staff creation details.
   * @return the created Staff object.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if a staff member with the same
   *     account ID already exists.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the specified entity does not
   *     exist.
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails.
   */
  Staff save(StaffCreateCommand cmd);

  /**
   * Saves multiple staff members in bulk.
   *
   * @param cmds an iterable of staff creation commands.
   * @return a list of created Staff objects.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if duplicates detected.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if any referenced entity is
   *     missing.
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails.
   */
  List<Staff> saveAll(Iterable<StaffCreateCommand> cmds);

  /**
   * Updates an existing staff member's account details or entity link.
   *
   * @param id the ID of the staff user (Account ID) to update.
   * @param cmd the command containing updated staff details.
   * @return the updated Staff object.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the staff or entity does not
   *     exist.
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails.
   */
  Staff update(UUID id, StaffUpdateCommand cmd);

  /**
   * Deletes all staff members with the specified IDs.
   *
   * @param ids an iterable of staff user IDs (Account IDs) to delete.
   * @return a map containing the count of deleted staff, accounts, and users.
   * @throws DataIntegrityException if references prevent deletion.
   */
  Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids);

  /**
   * Retrieves a staff member by their account ID.
   *
   * @param id the ID of the staff account.
   * @return the Staff object.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if not found or corrupted.
   */
  Staff getById(UUID id);

  /**
   * Lists all staff members.
   *
   * @return a list of all Staff objects.
   */
  List<Staff> listAll();

  /**
   * Lists all staff members associated with a specific entity.
   *
   * @param entityId the ID of the entity.
   * @return a list of Staff objects.
   */
  List<Staff> listByEntity(UUID entityId);

  /**
   * Checks if a staff member exists by their account ID.
   *
   * @param accountId the ID of the staff account.
   * @return true if exists.
   */
  boolean existsByAccountId(UUID accountId);

  /**
   * Checks if any staff members exist for the given account IDs.
   *
   * @param accountIds an iterable of account IDs.
   * @return true if any exist.
   */
  boolean existsAnyByAccountIdIn(Iterable<UUID> accountIds);
}
