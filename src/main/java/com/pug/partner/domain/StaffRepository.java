package com.pug.partner.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for managing {@link Staff} aggregate roots.
 * <p>
 * This interface defines the contract for assigning and revoking staff privileges
 * that link an authentication account directly to a partner organization (Entity).
 * It abstracts the underlying data storage mechanism to maintain a pure domain model.
 */
public interface StaffRepository {

  /**
   * Persists a newly created {@link Staff} aggregate into the repository.
   *
   * @param staff the {@link Staff} aggregate to persist
   * @return the fully persisted {@link Staff} instance
   */
  Staff persist(Staff staff);

  /**
   * Removes a {@link Staff} privilege record from the repository based on its
   * linked account identifier.
   *
   * @param accountId the unique identifier of the account whose staff privileges should be revoked
   * @return {@code true} if the staff record was successfully deleted, {@code false} if it was not found
   */
  boolean deleteByAccountId(UUID accountId);

  /**
   * Removes all {@link Staff} records associated with a specific partner entity.
   * <p>
   * This is typically used during cascading deletes when a partner entity is removed
   * from the system, ensuring no orphaned staff privileges remain.
   *
   * @param entityId the unique identifier of the partner entity
   * @return the total number of staff records successfully deleted
   */
  long deleteByEntityId(UUID entityId);

  /**
   * Retrieves a {@link Staff} aggregate by its linked account identifier.
   * <p>
   * When a staff record is reconstituted from the persistence layer, it might contain
   * validation errors (verifiable via {@link Staff#hasFieldErrors()}) if the stored data
   * is inconsistent with current domain rules.
   *
   * @param accountId the unique identifier of the linked account
   * @return an {@link Optional} containing the {@link Staff} if found, or {@link Optional#empty()} if not
   */
  Optional<Staff> findOptionalByAccountId(UUID accountId);

  /**
   * Retrieves a list of all {@link Staff} aggregates currently linked to a specific
   * partner entity.
   * <p>
   * Note: The returned objects may contain validation errors (verifiable via
   * {@link Staff#hasFieldErrors()}) if the stored data violates current domain rules.
   *
   * @param entityId the unique identifier of the partner entity to filter by
   * @return a {@link List} of {@link Staff} entities associated with the given partner entity
   */
  List<Staff> listAllByEntityId(UUID entityId);

  /**
   * Checks whether a specific {@link Staff} assignment already exists linking
   * the given account to the given partner entity.
   * <p>
   * This is primarily used by domain services to enforce uniqueness constraints
   * before persisting a new staff assignment.
   *
   * @param accountId the unique identifier of the linked authentication account
   * @param entityId  the unique identifier of the partner entity
   * @return {@code true} if the staff assignment exists, {@code false} otherwise
   */
  boolean existsByAccountIdAndEntityId(UUID accountId, UUID entityId);
}