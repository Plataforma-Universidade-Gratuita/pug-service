package com.pug.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for managing {@link User} aggregate roots.
 * <p>
 * This interface defines the contract for persisting, retrieving, updating, and deleting
 * user entities. It abstracts the underlying data storage mechanism (e.g., database)
 * to maintain a pure, infrastructure-agnostic domain model.
 */
public interface UserRepository {

  /**
   * Persists a newly created {@link User} aggregate into the repository.
   *
   * @param entity the {@link User} aggregate to persist
   * @return the fully persisted {@link User} instance
   */
  User persist(User entity);

  /**
   * Updates the state of an existing {@link User} aggregate in the repository.
   *
   * @param entity the {@link User} instance containing the updated state
   */
  void update(User entity);

  /**
   * Removes a {@link User} from the repository based on its unique identifier.
   *
   * @param id the unique identifier (UUIDv7) of the user to delete
   * @return {@code true} if the user was successfully deleted, {@code false} if the user was not found
   */
  boolean deleteById(UUID id);

  /**
   * Removes multiple {@link User} entities from the repository based on their unique identifiers.
   *
   * @param ids a list of UUIDs representing the users to delete
   * @return the total number of users that were successfully deleted
   */
  long deleteAllByIds(List<UUID> ids);

  /**
   * Retrieves a {@link User} by its unique identifier.
   * <p>
   * When a user is reconstituted from the persistence layer, it typically undergoes
   * the same domain validations as a newly created entity. Therefore, the returned {@link User}
   * might contain validation errors (verifiable via {@link User#hasFieldErrors()})
   * if the stored data violates current domain rules.
   *
   * @param id the unique identifier (UUID) of the user
   * @return an {@link Optional} containing the {@link User} if found, or {@link Optional#empty()} if not
   */
  Optional<User> findOptionalById(UUID id);

  /**
   * Retrieves a {@link User} by their unique Brazilian CPF.
   * <p>
   * Similar to ID retrieval, the reconstituted entity may contain validation errors
   * verifiable via {@link User#hasFieldErrors()} if the stored data is inconsistent.
   *
   * @param cpf the raw, 11-digit numeric CPF string of the user
   * @return an {@link Optional} containing the {@link User} if found, or {@link Optional#empty()} if not
   */
  Optional<User> findOptionalByCpf(String cpf);

  /**
   * Checks whether a {@link User} with the specified CPF already exists in the repository.
   * <p>
   * This is primarily used by domain services or use cases to enforce natural key
   * uniqueness constraints before persisting a new user or updating an existing one.
   *
   * @param cpf the numeric CPF string to check
   * @return {@code true} if a user with the given CPF exists, {@code false} otherwise
   */
  boolean existsByCpf(String cpf);
}