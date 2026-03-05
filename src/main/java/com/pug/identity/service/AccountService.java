package com.pug.identity.service;

import com.pug.identity.domain.Account;
import com.pug.identity.domain.User;
import com.pug.identity.service.dtos.AccountCreateCommand;
import com.pug.identity.service.dtos.AccountUpdateCommand;
import java.util.List;
import java.util.UUID;

/**
 * Application service interface for managing the state of {@link Account} domain aggregates.
 *
 * <p>Following CQRS principles, this service handles the "Command" operations (Create, Update,
 * Delete). It enforces cross-cutting business rules (e.g., email uniqueness) and manages the
 * complex lifecycle relationship between an {@link Account} and its underlying {@link User},
 * automatically provisioning or pruning user records to prevent orphans.
 */
public interface AccountService {

  /**
   * Instantiates and persists a new {@link Account} aggregate based on the provided command.
   *
   * <p>This method performs a cascading save. It checks if a {@link User} already exists for the
   * provided CPF. If they do, the new account is linked to them. If not, a new user is
   * automatically provisioned before the account is created.
   *
   * @param cmd the structured command containing the data to create the new account and linked user
   * @return the fully instantiated and persisted {@link Account} aggregate
   * @throws com.pug.shared.exceptions.DuplicateResourceException if an account with the given email
   *     already exists
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails (e.g., blank
   *     email, invalid CPF)
   */
  Account save(AccountCreateCommand cmd);

  /**
   * Updates an existing {@link Account} (and optionally its linked {@link User}) using the provided
   * data.
   *
   * <p>This method applies partial updates. If user data is provided in the command, the update is
   * cascaded down to the underlying user aggregate.
   *
   * @param id the unique identifier (UUIDv7) of the account to be updated
   * @param cmd the structured command containing the data to update the account
   * @return the mutated and persisted {@link Account} aggregate
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the account does not exist
   * @throws com.pug.shared.exceptions.DuplicateResourceException if the updated email conflicts
   *     with an existing account
   * @throws com.pug.shared.exceptions.AppValidationException if the updated input data violates
   *     domain constraints
   */
  Account update(UUID id, AccountUpdateCommand cmd);

  /**
   * Removes an {@link Account} from the system by its unique identifier.
   *
   * <p>This operation enforces data hygiene. After the account is deleted, the service checks if
   * the underlying {@link User} has any other active accounts. If they do not, the user is
   * automatically deleted to prevent orphaned records.
   *
   * @param id the unique identifier (UUID) of the account to delete
   * @return {@code true} if the account was successfully deleted, {@code false} if it was not found
   */
  boolean delete(UUID id);

  /**
   * Removes multiple {@link Account} entities from the system based on their unique identifiers.
   *
   * <p>Similar to single deletion, this batch operation evaluates the underlying users
   * post-deletion and purges any users that have been orphaned by the removal of these accounts.
   *
   * @param ids a list of UUIDs representing the accounts to delete
   * @return the total number of accounts that were successfully deleted
   */
  long deleteAll(List<UUID> ids);

  /**
   * Retrieves a full {@link Account} domain aggregate by its unique identifier.
   *
   * <p><b>Note:</b> This method is intended strictly for internal domain orchestration. For API
   * responses, use {@link AccountReadService#getViewById(UUID)} instead.
   *
   * @param id the unique identifier (UUID) of the account
   * @return the fully reconstituted {@link Account} aggregate
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the account does not exist
   * @throws com.pug.shared.exceptions.AppValidationException if the account exists but its stored
   *     state violates strict domain invariants (data corruption)
   */
  Account getById(UUID id);

  Account deactivate(UUID id);
}
