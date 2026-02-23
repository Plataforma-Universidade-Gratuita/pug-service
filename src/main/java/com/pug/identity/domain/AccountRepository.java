package com.pug.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for managing Account objects. */
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
   * Deletes all Accounts with the given list of IDs.
   *
   * @param ids the list of UUIDs of the Accounts to delete.
   * @return the number of Accounts that were deleted.
   */
  long deleteAllByIds(List<UUID> ids);

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
   * Finds the account IDs associated with a list of Account IDs.
   *
   * @param ids the list of Account UUIDs to find account IDs for.
   * @return a list of account UUIDs associated with the given Account IDs.
   */
  List<UUID> findUserIdsByIds(List<UUID> ids);

  /**
   * Finds all account IDs that are considered "orphan" (i.e., have no associated Accounts) among a
   * given list of account IDs.
   *
   * @param userIds the list of account UUIDs to check for orphan status.
   * @return a list of account UUIDs that are orphaned (have no associated Accounts).
   */
  List<UUID> findAllOrphanUserIdsByUserIds(List<UUID> userIds);

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
   * Counts the total number of Accounts associated with a given account ID.
   *
   * @param userId the UUID of the account whose accounts are to be counted.
   * @return the total number of Accounts associated with the specified account ID.
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
