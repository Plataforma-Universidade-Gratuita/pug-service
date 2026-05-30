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

  boolean deleteByAccountId(UUID accountId);

  long deleteByEntityId(UUID entityId);

  boolean existsByAccountIdAndEntityId(UUID accountId, UUID entityId);

  /**
   * Checks whether another staff assignment already exists in the target entity for the supplied
   * account email.
   *
   * <p>The {@code excludedAccountId} parameter allows update flows to ignore the current assignment
   * while still enforcing the entity-level uniqueness rule for account email.
   */
  boolean existsAnotherByEntityIdAndEmail(UUID entityId, String email, UUID excludedAccountId);

  Optional<Staff> findOptionalByAccountId(UUID accountId);

  List<Staff> listAllByEntityId(UUID entityId);

  Staff persist(Staff staff);

  /** Updates the mutable state of an existing persisted staff assignment. */
  void update(Staff staff);
}
