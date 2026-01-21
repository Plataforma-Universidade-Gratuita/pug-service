package com.pug.identity.domain;

import com.pug.shared.exceptions.AppValidationException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for managing User domain objects. */
public interface IUserRepository {

  /**
   * Persists a User domain object.
   *
   * @param entity the User to persist.
   * @return the persisted User.
   * @throws AppValidationException if the persisted entity cannot be converted back to a valid
   *     domain object (indicating a data integrity issue).
   */
  User persist(User entity) throws AppValidationException;

  /**
   * Persists multiple User domain objects.
   *
   * @param entities the User objects to persist.
   * @return a list of persisted User objects.
   * @throws AppValidationException if any persisted entity cannot be converted back to a valid
   *     domain object (indicating a data integrity issue).
   */
  List<User> persistAll(Iterable<User> entities) throws AppValidationException;

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
   * @throws AppValidationException if a UserEntity is found but its data is inconsistent with
   *     domain rules, preventing the creation of a valid domain object.
   */
  Optional<User> findOptionalById(UUID id) throws AppValidationException;

  /**
   * Finds a User by its CPF.
   *
   * @param cpf the CPF of the User to find.
   * @return an Optional containing the User if found, or empty if not found.
   * @throws AppValidationException if a UserEntity is found but its data is inconsistent with
   *     domain rules, preventing the creation of a valid domain object.
   */
  Optional<User> findOptionalByCpf(String cpf) throws AppValidationException;

  /**
   * Lists all User objects.
   *
   * @return a list of all User objects.
   * @throws AppValidationException if any UserEntity is found but its data is inconsistent with
   *     domain rules, preventing the creation of valid domain objects.
   */
  List<User> listAllUsers() throws AppValidationException;

  /**
   * Lists all User objects by their CPFs.
   *
   * @param cpfs an iterable of CPFs.
   * @return a list of User objects with the given CPFs.
   * @throws AppValidationException if any UserEntity is found but its data is inconsistent with
   *     domain rules, preventing the creation of valid domain objects.
   */
  List<User> listByCpfs(Iterable<String> cpfs) throws AppValidationException;

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
