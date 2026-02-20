package com.pug.partner.service;

import com.pug.partner.domain.Staff;
import com.pug.partner.service.dtos.StaffCreateCommand;
import com.pug.partner.service.dtos.StaffUpdateCommand;
import java.util.List;
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
   * Updates an existing staff member's account details or entity link.
   *
   * @param accountId the ID of the staff user (Account ID) to update.
   * @param cmd the command containing updated staff details.
   * @return the updated Staff object.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the staff or entity does not
   *     exist.
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails.
   */
  Staff update(UUID accountId, StaffUpdateCommand cmd);

  /**
   * Deletes a staff member by their account ID, removing their association with any entity.
   *
   * @param accountId the ID of the staff user (Account ID) to delete.
   * @return true if deletion was successful, false if the staff member was not found.
   */
  boolean delete(UUID accountId);

  /**
   * Deletes all staff members associated with a specific entity ID.
   *
   * @param entityId the ID of the entity whose staff members should be deleted.
   * @return the number of staff members that were deleted.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the specified entity does not exist.
   */
  long deleteAllByEntityId(UUID entityId);

  /**
   * Retrieves a staff member by their account ID.
   *
   * @param accountId the ID of the staff account.
   * @return the Staff object.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if not found or corrupted.
   */
  Staff getByAccountId(UUID accountId);

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
  List<Staff> listByEntityId(UUID entityId);

  /**
   * Checks if a staff member exists by their account ID.
   *
   * @param accountId the ID of the staff account.
   * @return true if exists.
   */
  boolean existsByAccountId(UUID accountId);
}
