package com.pug.identity.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for managing {@link Admin} aggregate roots.
 *
 * <p>This interface defines the contract for persisting, retrieving, updating, and deleting
 * administrator privileges. It abstracts the underlying data storage mechanism to maintain a pure,
 * infrastructure-agnostic domain model.
 */
public interface AdminRepository {

  /**
   * Persists a newly created {@link Admin} aggregate into the repository.
   *
   * @param entity the {@link Admin} aggregate to persist
   * @return the fully persisted {@link Admin} instance
   */
  Admin persist(Admin entity);

  /**
   * Updates the state of an existing {@link Admin} aggregate in the repository.
   *
   * @param entity the {@link Admin} instance containing the updated state
   */
  void update(Admin entity);

  /**
   * Removes an {@link Admin} privilege record from the repository based on its linked account
   * identifier.
   *
   * @param accountId the unique identifier of the account whose admin privileges should be revoked
   * @return {@code true} if the admin record was successfully deleted, {@code false} if it was not
   *     found
   */
  boolean deleteByAccountId(UUID accountId);

  /**
   * Retrieves an {@link Admin} by its linked account identifier.
   *
   * <p>When an admin record is reconstituted from the persistence layer, it typically undergoes the
   * same domain validations as a newly created entity. Therefore, the returned {@link Admin} might
   * contain validation errors (verifiable via {@link Admin#hasFieldErrors()}) if the stored data
   * violates current domain rules.
   *
   * @param accountId the unique identifier of the linked account
   * @return an {@link Optional} containing the {@link Admin} if found, or {@link Optional#empty()}
   *     if not
   */
  Optional<Admin> findOptionalByAccountId(UUID accountId);
}
