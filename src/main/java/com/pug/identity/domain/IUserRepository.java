package com.pug.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing User domain objects.
 */
public interface IUserRepository {

  /**
   * Persists a User domain object.
   *
   * @param entity the User to persist.
   * @return the persisted User.
   */
  User persist(User entity);

  /**
   * Persists multiple User domain objects.
   *
   * @param entities the User objects to persist.
   * @return a list of persisted User objects.
   */
  List<User> persistAll(Iterable<User> entities);

  /**
   * Updates a User domain object.
   *
   * @param entity the User to update.
   */
  void update(User entity);

  /**
   * Deletes User objects by their IDs.
   *
   * @param ids the IDs of the User objects to delete.
   * @return the number of entities deleted.
   */
  long deleteByIds(Iterable<UUID> ids);

  /**
   * Finds a User by its ID.
   *
   * @param id the ID of the User to find.
   * @return an Optional containing the User if found, or empty if not found.
   * <p>Note: The returned User may contain validation errors (check {@code user.hasErrors()})
   * if the stored data is inconsistent with current domain rules.
   */
  Optional<User> findOptionalById(UUID id);

  /**
   * Finds a User by its CPF.
   *
   * @param cpf the CPF of the User to find.
   * @return an Optional containing the User if found, or empty if not found.
   * <p>Note: The returned User may contain validation errors (check {@code user.hasErrors()})
   * if the stored data is inconsistent with current domain rules.
   */
  Optional<User> findOptionalByCpf(String cpf);

  /**
   * Lists all User objects.
   *
   * @return a list of all User objects.
   * <p>Note: The returned Users may contain validation errors (check {@code user.hasErrors()})
   * if the stored data is inconsistent with current domain rules.
   */
  List<User> listAllUsers();

  /**
   * Lists all User objects by their CPFs.
   *
   * @param cpfs an iterable of CPFs.
   * @return a list of User objects with the given CPFs.
   * <p>Note: The returned Users may contain validation errors (check {@code user.hasErrors()})
   * if the stored data is inconsistent with current domain rules.
   */
  List<User> listByCpfs(Iterable<String> cpfs);

  /**
   * Checks if a User exists with the given CPF.
   *
   * @param cpf the CPF to check.
   * @return true if a User exists with the given CPF, false otherwise.
   */
  boolean existsByCpf(String cpf);

  /**
   * Checks if any User exists with a CPF in the given collection.
   *
   * @param cpfs the collection of CPFs to check.
   * @return true if any User exists with a CPF in the collection, false otherwise.
   */
  boolean existsAnyByCpfIn(Iterable<String> cpfs);
}