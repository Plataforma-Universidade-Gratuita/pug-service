/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.service;

import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountUpdateCommand;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
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
public interface AccountsService {

  /**
   * Deactivates an existing {@link Account} without deleting it.
   *
   * <p>This disables Login.bru capabilities and system access, returning a functionally inactive
   * account while maintaining historical referential integrity.
   *
   * @param id the unique identifier (UUID) of the account to deactivate
   * @return the deactivated {@link Account} aggregate
   * @throws ResourceNotFoundException if the account does not exist
   */
  Account deactivate(UUID id);

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
   * Retrieves a full {@link Account} domain aggregate by its registered email address.
   *
   * <p><b>Note:</b> This method is intended strictly for internal domain orchestration (such as
   * authentication flows).
   *
   * @param email the email address of the account
   * @return the fully reconstituted {@link Account} aggregate
   * @throws ResourceNotFoundException if the account does not exist
   * @throws AppValidationException if the account exists but its stored state violates domain
   *     constraints
   */
  Account getByEmail(String email);

  /**
   * Retrieves a full {@link Account} domain aggregate by its unique identifier.
   *
   * <p><b>Note:</b> This method is intended strictly for internal domain orchestration. For API
   * responses, use {@link AccountsReadService#getViewById(UUID)} instead.
   *
   * @param id the unique identifier (UUID) of the account
   * @return the fully reconstituted {@link Account} aggregate
   * @throws ResourceNotFoundException if the account does not exist
   * @throws AppValidationException if the account exists but its stored state violates strict
   *     domain invariants (data corruption)
   */
  Account getById(UUID id);

  /**
   * Instantiates and persists a new {@link Account} aggregate based on the provided command.
   *
   * <p>This method performs a cascading save. It checks if a {@link User} already exists for the
   * provided CPF. If they do, the new account is linked to them. If not, a new user is
   * automatically provisioned before the account is created.
   *
   * @param cmd the structured command containing the data to create the new account and linked user
   * @return the fully instantiated and persisted {@link Account} aggregate
   * @throws DuplicateResourceException if an account with the given email already exists
   * @throws AppValidationException if input validation fails (e.g., blank email, invalid CPF)
   */
  Account save(AccountCreateCommand cmd);

  /**
   * Instantiates and persists multiple {@link Account} aggregates in a single batch transaction.
   *
   * <p>This method drastically optimizes cross-domain data cascades. It resolves underlying {@link
   * User} dependencies in bulk, identifies existing users to avoid duplication, and dispatches
   * unified bulk flushes to the database.
   *
   * @param cmds a {@link List} of structured commands for the batch accounts
   * @return a {@link List} of the fully instantiated and persisted {@link Account} aggregates
   * @throws DuplicateResourceException if any email already exists
   * @throws AppValidationException if input validation fails
   */
  List<Account> saveInBulk(List<AccountCreateCommand> cmds);

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
   * @throws ResourceNotFoundException if the account does not exist
   * @throws DuplicateResourceException if the updated email conflicts with an existing account
   * @throws AppValidationException if the updated input data violates domain constraints
   */
  Account update(UUID id, AccountUpdateCommand cmd);
}
