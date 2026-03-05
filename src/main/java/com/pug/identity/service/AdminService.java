package com.pug.identity.service;

import com.pug.identity.domain.Admin;
import com.pug.identity.service.dtos.AdminCreateCommand;
import com.pug.identity.service.dtos.AdminUpdateCommand;
import java.util.UUID;

/**
 * Application service interface for managing the state of {@link Admin} domain aggregates.
 *
 * <p>Following CQRS principles, this service handles the "Command" operations (Create, Update,
 * Delete) for administrative privileges. It orchestrates the complex lifecycle relationship between
 * an {@link Admin}, its underlying {@link com.pug.identity.domain.Account}, and the associated
 * user, ensuring that granting or revoking admin rights cascades correctly through the system.
 */
public interface AdminService {

  /**
   * Instantiates and persists a new {@link Admin} aggregate based on the provided command.
   *
   * <p>This method performs a cascading save. It delegates the creation of the underlying
   * authentication account (and potentially the user) to the {@link AccountService} before
   * appending the administrative privileges to that account.
   *
   * @param cmd the structured command containing the data to create the admin and linked account
   * @return the fully instantiated and persisted {@link Admin} aggregate
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails for account
   *     or admin data
   * @throws com.pug.shared.exceptions.DuplicateResourceException if the underlying account email
   *     already exists
   */
  Admin save(AdminCreateCommand cmd);

  /**
   * Updates an existing {@link Admin} and optionally its underlying account using the provided
   * data.
   *
   * <p>This method applies partial updates. If account data is provided in the command, the update
   * is cascaded down to the underlying account aggregate.
   *
   * @param accountId the unique identifier of the Admin (which corresponds directly to the Account
   *     ID)
   * @param cmd the structured command containing the data to update the admin and/or account
   * @return the mutated and persisted {@link Admin} aggregate
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the Admin or linked Account does
   *     not exist
   * @throws com.pug.shared.exceptions.AppValidationException if the updated input data violates
   *     domain constraints
   */
  Admin update(UUID accountId, AdminUpdateCommand cmd);

  /**
   * Revokes administrative privileges by deleting the {@link Admin} record.
   *
   * <p>This operation enforces data hygiene. After the admin privileges are successfully revoked,
   * the service automatically triggers the deletion of the underlying {@link
   * com.pug.identity.domain.Account} to ensure credentials tied strictly to admin roles are wiped
   * out.
   *
   * @param accountId the unique identifier of the Admin to delete (Account ID)
   * @return {@code true} if the Admin was successfully deleted, {@code false} if it was not found
   */
  boolean delete(UUID accountId);

  /**
   * Retrieves a full {@link Admin} domain aggregate by its linked account identifier.
   *
   * <p><b>Note:</b> This method is intended strictly for internal domain orchestration. For API
   * responses, use {@link AdminReadService#getViewByAccountId(UUID)} instead.
   *
   * @param accountId the unique identifier (UUID) of the linked account
   * @return the fully reconstituted {@link Admin} aggregate
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the Admin does not exist
   * @throws com.pug.shared.exceptions.AppValidationException if the Admin exists but its stored
   *     state violates strict domain invariants (data corruption)
   */
  Admin getByAccountId(UUID accountId);
}
