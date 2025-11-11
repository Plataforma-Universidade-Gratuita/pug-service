package com.pug.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for managing Account objects. */
public interface AccountRepository {

  /**
   * Persists a Account object.
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
   * Updates a Accounts object.
   *
   * @param entity the Accounts to update.
   */
  void update(Account entity);

  /**
   * Deletes Accounts objects by their IDs.
   *
   * @param ids the iterable collection of UUIDs representing the IDs of the Accounts objects to
   *     delete.
   * @return the number of entities deleted.
   */
  long deleteByIds(Iterable<UUID> ids);

  /**
   * Finds a Accounts by its ID.
   *
   * @param id the UUID of the Accounts to find.
   * @return an Optional containing the found Accounts, or empty if not found.
   */
  Optional<Account> findOptionalById(UUID id);

  /**
   * Lists all Accounts objects.
   *
   * @return a list of all Accounts objects.
   */
  List<Account> listAllAccounts();

  /**
   * Checks if a Accounts exists by email.
   *
   * @param email the email to check for existence.
   * @return true if a Accounts with the given email exists, false otherwise.
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
