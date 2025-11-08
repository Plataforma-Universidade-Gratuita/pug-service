package com.pug.identity.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for managing Users objects. */
public interface UsersRepository {

  /**
   * Persists a User object.
   *
   * @param entity the User to persist.
   * @return the persisted User.
   */
  User persist(User entity);

  /**
   * Persists multiple Users objects.
   *
   * @param entities the iterable collection of User objects to persist.
   * @return a list of the persisted User objects.
   */
  List<User> persistAll(Iterable<User> entities);

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
  Optional<User> findOptionalById(UUID id);

  /**
   * Finds a Users by its email.
   *
   * @param email the email of the Users to find.
   * @return an Optional containing the found Users, or empty if not found.
   */
  Optional<User> findOptionalByEmail(String email);

  /**
   * Lists all Users objects.
   *
   * @return a list of all Users objects.
   */
  List<User> listAllUsers();

  /**
   * Lists Users objects by CPF.
   *
   * @param cpf the CPF to filter Users objects.
   * @return a list of Users objects matching the given CPF.
   */
  List<User> listByCpf(String cpf);

  /**
   * Searches for Users objects by name.
   *
   * @param key the name key to search for.
   * @return a list of Users objects matching the search key.
   */
  List<User> searchByName(String key);

  /**
   * Checks if a Users exists by email.
   *
   * @param email the email to check for existence.
   * @return true if a Users with the given email exists, false otherwise.
   */
  boolean existsByEmail(String email);

  /**
   * Checks if any Users exists with emails in the given collection.
   *
   * @param emails the collection of emails to check for existence.
   * @return true if any Users with emails in the collection exists, false otherwise.
   */
  boolean existsAnyByEmailIn(Collection<String> emails);

  /**
   * Deactivates a Users by its ID.
   *
   * @param id the UUID of the Users to deactivate.
   */
  void deactivateById(UUID id);

  /**
   * Updates a Users object.
   *
   * @param user the Users to update.
   */
  void update(User user);
}
