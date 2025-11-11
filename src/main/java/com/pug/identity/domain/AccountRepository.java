package com.pug.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for managing Users objects. */
public interface AccountRepository {

  /**
   * Persists a User object.
   *
   * @param entity the User to persist.
   * @return the persisted User.
   */
  Account persist(Account entity);

  /**
   * Persists multiple Users objects.
   *
   * @param entities the iterable collection of User objects to persist.
   * @return a list of the persisted User objects.
   */
  List<Account> persistAll(Iterable<Account> entities);

  /**
   * Updates a Users object.
   *
   * @param account the Users to update.
   */
  void update(Account account);

  /**
   * Deletes Users objects by their IDs.
   *
   * @param ids the iterable collection of UUIDs representing the IDs of the Users objects to
   *     delete.
   * @return the number of entities deleted.
   */
  long deleteByIds(Iterable<UUID> ids);

  /**
   * Finds a Users by its ID.
   *
   * @param id the UUID of the Users to find.
   * @return an Optional containing the found Users, or empty if not found.
   */
  Optional<Account> findOptionalById(UUID id);

  /**
   * Lists all Users objects.
   *
   * @return a list of all Users objects.
   */
  List<Account> listAllUsers();

  /**
   * Checks if a Users exists by email.
   *
   * @param email the email to check for existence.
   * @return true if a Users with the given email exists, false otherwise.
   */
  boolean existsByEmail(String email);

  /**
   * Checks if any Users exists with emails in the given iterable.
   *
   * @param emails the iterable of emails to check for existence.
   * @return true if any Users with emails in the iterable exists, false otherwise.
   */
  boolean existsAnyByEmailIn(Iterable<String> emails);
}
