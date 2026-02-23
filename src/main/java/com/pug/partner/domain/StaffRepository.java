package com.pug.partner.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for managing Staff entities. */
public interface StaffRepository {

  /**
   * Persists a Staff entityId.
   *
   * @param staff the Staff entityId to persist.
   * @return the persisted Staff entityId.
   */
  Staff persist(Staff staff);

  /**
   * Deletes a Staff entityId by its account ID.
   *
   * @param accountId the account ID of the Staff entityId to delete.
   * @return true if the entityId was deleted, false otherwise.
   */
  boolean deleteByAccountId(UUID accountId);

  /**
   * Deletes all Staff entities associated with a specific entityId ID.
   *
   * @param entityId the entityId ID whose associated Staff entities should be deleted.
   * @return the number of Staff entities deleted.
   */
  long deleteByEntityId(UUID entityId);

  /**
   * Finds a Staff entityId by its associated account ID.
   *
   * <p>Note: The returned Staff may contain validation errors (check {@code staff.hasErrors()}) if
   * the stored data is inconsistent with current domain rules.
   *
   * @param accountId the account ID to search for.
   * @return an Optional containing the found Staff entityId, or empty if not found.
   */
  Optional<Staff> findOptionalByAccountId(UUID accountId);

  /**
   * Lists all Staff entities associated with a specific entityId ID.
   *
   * <p>Note: The returned Staff objects may contain validation errors (check {@code
   * staff.hasErrors()}) if the stored data is inconsistent with current domain rules.
   *
   * @param entityId the entityId ID to filter by.
   * @return a list of Staff entities associated with the given entityId ID.
   */
  List<Staff> listAllByEntityId(UUID entityId);
}
