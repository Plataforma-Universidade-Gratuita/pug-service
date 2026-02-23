package com.pug.partner.service;

import com.pug.partner.domain.Staff;
import com.pug.partner.service.dtos.StaffCreateCommand;
import java.util.UUID;

/** Interface for managing staff assignments to partner entities. */
public interface StaffService {

  /**
   * Save a new staff member by creating an account and linking them to an entityId.
   *
   * @param cmd the command containing staff creation details.
   * @return the created Staff object.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if a staff member with the same
   *     account ID already exists.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the specified entityId does not
   *     exist.
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails.
   */
  Staff save(StaffCreateCommand cmd);

  /**
   * Deletes a staff member by their account ID, removing their association with any entityId.
   *
   * @param accountId the ID of the staff account (Account ID) to delete.
   * @return true if deletion was successful, false if the staff member was not found.
   */
  boolean delete(UUID accountId);

  /**
   * Deletes all staff members associated with a specific entityId ID.
   *
   * @param entityId the ID of the entityId whose staff members should be deleted.
   * @return the number of staff members that were deleted.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the specified entityId does not
   *     exist.
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
}
