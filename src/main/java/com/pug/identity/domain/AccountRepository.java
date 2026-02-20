package com.pug.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing Account objects.
 */
public interface AccountRepository {

  /**
   * Persists an Account object.
   *
   * @param entity the Account to persist.
   * @return the persisted Account.
   */
  Account persist(Account entity);

  /**
   * Updates an Accounts object.
   *
   * @param entity the Accounts to update.
   */
  void update(Account entity);

  /**
   * Deletes an Accounts by its ID.
   *
   * @param id the UUID of the Accounts to delete.
   * @return true if the Accounts was deleted, false if it was not found.
   */
  boolean deleteById(UUID id);

  /**
   * Finds an Accounts by its ID.
   *
   * <p>Note: The returned Account may contain validation errors (check {@code account.hasErrors()})
   * if the stored data is inconsistent with current domain rules.
   *
   * @param id the UUID of the Accounts to find.
   * @return an Optional containing the found Account, or empty if not found.
   */
  Optional<Account> findOptionalById(UUID id);

  /**
   * Lists all Accounts objects.
   *
   * <p>Note: The returned Accounts may contain validation errors (check {@code
   * account.hasErrors()}) if the stored data is inconsistent with current domain rules.
   *
   * @return a list of all Accounts objects.
   */
  List<Account> listAllAccounts();

  /**
   * Counts the total number of Accounts associated with a given user ID.
   *
   * @param userId the UUID of the user whose accounts are to be counted.
   * @return the total number of Accounts associated with the specified user ID.
   */
  long countAllAccountsByUserId(UUID userId);

  /**
   * Checks if an Accounts exists by email.
   *
   * @param email the email to check for existence.
   * @return true if an Accounts with the given email exists, false otherwise.
   */
  boolean existsByEmail(String email);
}
