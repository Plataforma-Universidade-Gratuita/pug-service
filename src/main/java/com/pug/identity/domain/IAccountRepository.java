package com.pug.identity.domain;

import com.pug.shared.exceptions.AppValidationException;

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
   * @throws AppValidationException if the persisted entity cannot be converted back to a valid
   *                                domain object (indicating a data integrity issue).
   */
  Account persist(Account entity) throws AppValidationException;

  /**
   * Persists multiple Accounts objects.
   *
   * @param entities the iterable collection of Account objects to persist.
   * @return a list of the persisted Account objects.
   * @throws AppValidationException if any persisted entity cannot be converted back to a valid
   *                                domain object (indicating a data integrity issue).
   */
  List<Account> persistAll(Iterable<Account> entities) throws AppValidationException;

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
   * @return an Optional containing the found Accounts, or empty if not found.
   * @throws AppValidationException if an AccountEntity is found but its data is inconsistent with
   *                                domain rules, preventing the creation of a valid domain object.
   */
  Optional<Account> findOptionalById(UUID id) throws AppValidationException;

  /**
   * Lists all Accounts objects.
   *
   * @return a list of all Accounts objects.
   * @throws AppValidationException if any AccountEntity is found but its data is inconsistent with
   *                                domain rules, preventing the creation of valid domain objects.
   */
  List<Account> listAllAccounts() throws AppValidationException;

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