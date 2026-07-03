/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.partner.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for managing {@link Staff} aggregate roots.
 *
 * <p>This boundary encapsulates persistence operations for staff assignments between partner
 * accounts and partner entities, including existence checks used by reassignment and uniqueness
 * rules.
 */
public interface StaffRepository {

  /**
   * Removes a staff assignment based on its linked account identifier.
   *
   * @param accountId the unique identifier of the account linked to the staff member
   * @return {@code true} if the staff assignment was deleted, or {@code false} when the identifier
   *     is null or no assignment exists for it
   */
  boolean deleteByAccountId(UUID accountId);

  /**
   * Removes every staff assignment linked to the provided partner entity.
   *
   * @param entityId the unique identifier of the partner entity whose staff should be removed
   * @return the number of staff assignments deleted, or {@code 0} when the identifier is null or no
   *     assignments exist for it
   */
  long deleteByEntityId(UUID entityId);

  /**
   * Checks whether a staff assignment already exists for the provided account and partner entity.
   *
   * @param accountId the unique identifier of the account linked to the staff member
   * @param entityId the unique identifier of the partner entity
   * @return {@code true} if a matching staff assignment exists, or {@code false} otherwise
   */
  boolean existsByAccountIdAndEntityId(UUID accountId, UUID entityId);

  /**
   * Checks whether another staff assignment already exists in the target entity for the supplied
   * account email.
   *
   * <p>The {@code excludedAccountId} parameter allows update flows to ignore the current assignment
   * while still enforcing the entity-level uniqueness rule for account email.
   *
   * @param entityId the unique identifier of the partner entity whose staff roster is being checked
   * @param email the normalized account email that must remain unique within the entity
   * @param excludedAccountId the optional linked account identifier that should be ignored during
   *     the uniqueness check
   * @return {@code true} if another staff assignment already uses the supplied email inside the
   *     target entity, or {@code false} otherwise
   */
  boolean existsAnotherByEntityIdAndEmail(UUID entityId, String email, UUID excludedAccountId);

  /**
   * Retrieves a staff aggregate by its linked account identifier.
   *
   * @param accountId the unique identifier of the account linked to the staff member
   * @return an {@link Optional} containing the matching {@link Staff}, or {@link Optional#empty()}
   *     when the identifier is null or no staff assignment exists for it
   */
  Optional<Staff> findOptionalByAccountId(UUID accountId);

  /**
   * Lists every staff aggregate linked to the provided partner entity.
   *
   * @param entityId the unique identifier of the partner entity
   * @return a list containing the staff aggregates linked to the entity, or an empty list when the
   *     identifier is null or no assignments exist
   */
  List<Staff> listAllByEntityId(UUID entityId);

  /**
   * Persists a newly created staff aggregate into the repository.
   *
   * @param staff the {@link Staff} aggregate to persist
   * @return the fully persisted {@link Staff} instance
   */
  Staff persist(Staff staff);

  /**
   * Updates the mutable state of an existing persisted staff assignment.
   *
   * @param staff the {@link Staff} aggregate containing the updated state
   */
  void update(Staff staff);
}
