package com.pug.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing Account objects.
 */
public interface IAccountRepository {

  /**
   * Persists an Account object.
   *
   * @param entity the Account to persist.
   * @return the persisted Account.
   */
  Account persist(Account entity);

  /**
   * Persists multiple Accounts objects.
   *
   * @param entities the iterable collection of Account objects to persist.
   * @return a list of the persisted Account objects.
   */
  List<Account> persistAll(Iterable<Account> entities);

  /**
   * Updates an Accounts object.
   *
   * @param entity the Accounts to update.
   */
  void update(Account entity);

  /**
   * Deletes Accounts objects by their IDs.
   *
   * @param ids the iterable collection of UUIDs representing the IDs of the Accounts objects to
   *            delete.
   * @return the number of entities deleted.
   */
  long deleteByIds(Iterable<UUID> ids);

  /**
   * Finds an Accounts by its ID.
   *
   * @param id the UUID of the Accounts to find.
   * @return an Optional containing the found Account, or empty if not found.
   * <p>Note: The returned Account may contain validation errors (check {@code account.hasErrors()})
   * if the stored data is inconsistent with current domain rules.
   */
  Optional<Account> findOptionalById(UUID id);

  /**
   * Lists all Accounts objects.
   *
   * @return a list of all Accounts objects.
   * <p>Note: The returned Accounts may contain validation errors (check {@code account.hasErrors()})
   * if the stored data is inconsistent with current domain rules.
   */
  List<Account> listAllAccounts();

  /**
   * Lists all Account user IDs by their Account IDs.
   *
   * @param ids the iterable collection of UUIDs representing the Account IDs.
   * @return a list of UUIDs representing the user IDs associated with the given Account IDs.
   */
  List<UUID> listAllAccountUserIdsByIds(Iterable<UUID> ids);

  /**
   * Finds user IDs that have Accounts, excluding those associated with the given Account IDs.
   *
   * @param excludedAccountIds the iterable collection of UUIDs representing the Account IDs to
   *                           exclude.
   * @param candidateUserIds   the iterable collection of UUIDs representing the candidate user IDs to
   *                           check.
   * @return a list of UUIDs representing the user IDs that have Accounts, excluding those
   * associated with the excluded Account IDs.
   */
  List<UUID> findUserIdsWithAccountsExcluding(
          Iterable<UUID> excludedAccountIds, Iterable<UUID> candidateUserIds);

  /**
   * Checks if an Accounts exists by email.
   *
   * @param email the email to check for existence.
   * @return true if an Accounts with the given email exists, false otherwise.
   */
  boolean existsByEmail(String email);

  /**
   * Checks if any Accounts exists with emails in the given iterable.
   *
   * @param emails the iterable of emails to check for existence.
   * @return true if any Accounts with emails in the iterable exists, false otherwise.
   */
  boolean existsAnyByEmailIn(Iterable<String> emails);

  /**
   * Checks if any Accounts exists with user IDs in the given iterable.
   *
   * @param userIds the iterable of user IDs to check for existence.
   * @return true if any Accounts with user IDs in the iterable exists, false otherwise.
   */
  boolean existsAnyByUserIdIn(Iterable<UUID> userIds);
}