package br.org.catolicasc.pug.partner.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Domain repository interface for managing {@link Staff} aggregate roots. */
public interface StaffRepository {

  boolean deleteByAccountId(UUID accountId);

  long deleteByEntityId(UUID entityId);

  boolean existsByAccountIdAndEntityId(UUID accountId, UUID entityId);

  /**
   * Checks whether another staff assignment already exists in the given entity for the supplied
   * account email.
   */
  boolean existsAnotherByEntityIdAndEmail(UUID entityId, String email, UUID excludedAccountId);

  Optional<Staff> findOptionalByAccountId(UUID accountId);

  List<Staff> listAllByEntityId(UUID entityId);

  Staff persist(Staff staff);

  /** Updates an existing persisted staff assignment. */
  void update(Staff staff);
}
