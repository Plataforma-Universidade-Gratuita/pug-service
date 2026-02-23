package com.pug.identity.service;

import com.pug.identity.domain.Account;
import com.pug.identity.domain.User;
import com.pug.identity.service.dtos.AccountCreateCommand;
import com.pug.identity.service.dtos.AccountUpdateCommand;
import java.util.List;
import java.util.UUID;

/** Interface for managing accounts. */
public interface AccountService {

  /**
   * Creates and saves a new Account.
   *
   * <p>If the associated {@link User} does not exist, it will be created.
   *
   * @param cmd the command containing the data to create the new Account.
   * @return the saved Account.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if an account with the given email
   *     already exists.
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails (e.g., blank
   *     email, invalid CPF).
   */
  Account save(AccountCreateCommand cmd);

  /**
   * Updates an existing Account with the given ID using the provided data.
   *
   * @param id the UUID of the Account to be updated.
   * @param cmd the command containing the data to update the Account.
   * @return the updated Account entity
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the account with the given ID
   *     does not exist (or data is corrupted in DB).
   * @throws com.pug.shared.exceptions.DuplicateResourceException if an account with the updated
   *     email already exists.
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails for account
   *     or account data.
   */
  Account update(UUID id, AccountUpdateCommand cmd);

  /**
   * Deletes an Account by its ID.
   *
   * @param id the UUID of the Account to be deleted.
   * @return true if the Account was successfully deleted, false if the Account was not found.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the account with the given ID
   *     does not exist (or data is corrupted in DB).
   */
  boolean delete(UUID id);

  /**
   * Deletes all accounts with the given list of IDs.
   *
   * @param ids the list of UUIDs of the Accounts to be deleted.
   * @return the number of Accounts that were successfully deleted.
   */
  long deleteAll(List<UUID> ids);

  /**
   * Retrieves an Account by its ID.
   *
   * @param id the UUID of the Account.
   * @return the Account entity.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the account with the given ID
   *     does not exist (or data is corrupted in DB).
   * @throws com.pug.shared.exceptions.AppValidationException if the account is found but its data
   *     is corrupted in the database.
   */
  Account getById(UUID id);
}
