package com.pug.identity.service;

import com.pug.identity.domain.Admin;
import com.pug.identity.service.dtos.AdminCreateCommand;
import com.pug.identity.service.dtos.AdminUpdateCommand;
import java.util.UUID;

/** Interface for managing admins. */
public interface AdminService {

  /**
   * Creates and saves a new Admin.
   *
   * <p>This method also creates and saves the associated Account.
   *
   * @param cmd the command containing the data to create the new Admin.
   * @return the saved Admin.
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails for account
   *     or admin data.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if the account email already
   *     exists.
   */
  Admin save(AdminCreateCommand cmd);

  /**
   * Updates an existing Admin's underlying Account.
   *
   * @param accountId the ID of the Admin (which corresponds to the Account ID).
   * @param cmd the command containing the data to update the Account.
   * @return the updated Admin.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the Admin/Account does not
   *     exist.
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails.
   */
  Admin update(UUID accountId, AdminUpdateCommand cmd);

  /**
   * Deletes the Admin with the given ID (which corresponds to the Account ID).
   *
   * @param accountId the ID of the Admin to delete.
   * @return true if the Admin was deleted, false if no Admin with the given ID was found.
   */
  boolean delete(UUID accountId);

  /**
   * Retrieves an Admin by account ID.
   *
   * @param accountId the UUID of the account.
   * @return the Admin entity.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the Admin with the given account
   *     ID does not exist (or data is corrupted in DB).
   */
  Admin getByAccountId(UUID accountId);
}
