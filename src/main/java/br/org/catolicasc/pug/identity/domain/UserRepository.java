/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for managing {@link User} aggregate roots.
 *
 * <p>This interface defines the contract for persisting, retrieving, updating, and deleting user
 * entities. It abstracts the underlying data storage mechanism (e.g., database) to maintain a pure,
 * infrastructure-agnostic domain model.
 */
public interface UserRepository {

  /**
   * Removes multiple {@link User} entities from the repository based on their unique identifiers.
   *
   * @param ids a list of UUIDs representing the users to delete
   * @return the total number of users that were successfully deleted
   */
  long deleteAllByIds(List<UUID> ids);

  /**
   * Removes a {@link User} from the repository based on its unique identifier.
   *
   * @param id the unique identifier (UUIDv7) of the user to delete
   * @return {@code true} if the user was successfully deleted, {@code false} if the user was not
   *     found
   */
  boolean deleteById(UUID id);

  /**
   * Checks whether any of the specified CPFs already exist in the repository.
   *
   * <p>This bulk operation is heavily utilized during batch creations to validate uniqueness
   * payloads against the database in a single round-trip.
   *
   * @param cpfs a {@link List} of exact 11-digit numeric CPF strings
   * @return {@code true} if at least one matching CPF exists, {@code false} otherwise
   */
  boolean existsAnyByCpfs(List<String> cpfs);

  /**
   * Checks whether a {@link User} with the specified CPF already exists in the repository.
   *
   * @param cpf the numeric CPF string to check
   * @return {@code true} if a user with the given CPF exists, {@code false} otherwise
   */
  boolean existsByCpf(String cpf);

  /**
   * Retrieves a {@link User} by their unique Brazilian CPF.
   *
   * @param cpf the raw, 11-digit numeric CPF string of the user
   * @return an {@link Optional} containing the {@link User} if found, or {@link Optional#empty()}
   *     if not
   */
  Optional<User> findOptionalByCpf(String cpf);

  /**
   * Retrieves a {@link User} by its unique identifier.
   *
   * @param id the unique identifier (UUID) of the user
   * @return an {@link Optional} containing the {@link User} if found, or {@link Optional#empty()}
   *     if not
   */
  Optional<User> findOptionalById(UUID id);

  /**
   * Retrieves a collection of {@link User} domain aggregates based on a list of CPFs.
   *
   * @param cpfs a {@link List} of exact 11-digit numeric CPF strings
   * @return a {@link List} of the fully reconstituted {@link User} instances
   */
  List<User> listByCpfs(List<String> cpfs);

  /**
   * Persists a newly created {@link User} aggregate into the repository.
   *
   * @param entity the {@link User} aggregate to persist
   * @return the fully persisted {@link User} instance
   */
  User persist(User entity);

  /**
   * Persists a collection of newly created {@link User} aggregates in a single batch.
   *
   * @param users a {@link List} of {@link User} aggregates to persist
   * @return the fully persisted {@link List} of {@link User} instances
   */
  List<User> persistAll(List<User> users);

  /**
   * Updates the state of an existing {@link User} aggregate in the repository.
   *
   * @param entity the {@link User} instance containing the updated state
   */
  void update(User entity);
}
