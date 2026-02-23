package com.pug.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for managing User domain objects. */
public interface UserRepository {

  /**
   * Persists a User domain object.
   *
   * @param entity the User to persist.
   * @return the persisted User.
   */
  User persist(User entity);

  /**
   * Updates a User domain object.
   *
   * @param entity the User to update.
   */
  void update(User entity);

  /**
   * Deletes a User by its ID.
   *
   * @param id the ID of the User to delete.
   * @return true if the User was deleted, false if no User with the given ID was found.
   */
  boolean deleteById(UUID id);

  /**
   * Deletes multiple Users by their IDs.
   *
   * @param ids a list of IDs of the Users to delete.
   * @return the number of Users that were deleted.
   */
  long deleteAllByIds(List<UUID> ids);

  /**
   * Finds a User by its ID.
   *
   * <p>Note: The returned User may contain validation errors (check {@code account.hasErrors()}) if
   * the stored data is inconsistent with current domain rules.
   *
   * @param id the ID of the User to find.
   * @return an Optional containing the User if found, or empty if not found.
   */
  Optional<User> findOptionalById(UUID id);

  /**
   * Finds a User by its CPF.
   *
   * <p>Note: The returned User may contain validation errors (check {@code account.hasErrors()}) if
   * the stored data is inconsistent with current domain rules.
   *
   * @param cpf the CPF of the User to find.
   * @return an Optional containing the User if found, or empty if not found.
   */
  Optional<User> findOptionalByCpf(String cpf);

  /**
   * Lists all User objects.
   *
   * <p>Note: The returned Users may contain validation errors (check {@code account.hasErrors()}) if
   * the stored data is inconsistent with current domain rules.
   *
   * @return a list of all User objects.
   */
  List<User> listAllUsers();

  /**
   * Checks if a User exists with the given CPF.
   *
   * @param cpf the CPF to check.
   * @return true if a User exists with the given CPF, false otherwise.
   */
  boolean existsByCpf(String cpf);
}
