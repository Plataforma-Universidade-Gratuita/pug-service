package com.pug.partner.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for managing Staff entities. */
public interface StaffRepository {

  /**
   * Persists a Staff entity.
   *
   * @param staff the Staff entity to persist.
   * @return the persisted Staff entity.
   */
  Staff persist(Staff staff);

  /**
   * Updates an existing Staff entity.
   *
   * @param entity the Staff entity to update.
   */
  void update(Staff entity);

  /**
   * Deletes a Staff entity by its account ID.
   *
   * @param accountId the account ID of the Staff entity to delete.
   * @return true if the entity was deleted, false otherwise.
   */
  boolean deleteByAccountId(UUID accountId);

  /**
   * Deletes all Staff entities associated with a specific entity ID.
   *
   * @param entityId the entity ID whose associated Staff entities should be deleted.
   * @return the number of Staff entities deleted.
   */
  long deleteByEntityId(UUID entityId);

  /**
   * Finds a Staff entity by its associated account ID.
   *
   * <p>Note: The returned Staff may contain validation errors (check {@code staff.hasErrors()}) if
   * the stored data is inconsistent with current domain rules.
   *
   * @param accountId the account ID to search for.
   * @return an Optional containing the found Staff entity, or empty if not found.
   */
  Optional<Staff> findOptionalByAccountId(UUID accountId);

  /**
   * Lists all Staff entities.
   *
   * <p>Note: The returned Staff objects may contain validation errors (check {@code
   * staff.hasErrors()}) if the stored data is inconsistent with current domain rules.
   *
   * @return a list of all Staff entities.
   */
  List<Staff> listAllStaff();

  /**
   * Lists all Staff entities associated with a specific entity ID.
   *
   * <p>Note: The returned Staff objects may contain validation errors (check {@code
   * staff.hasErrors()}) if the stored data is inconsistent with current domain rules.
   *
   * @param entityId the entity ID to filter by.
   * @return a list of Staff entities associated with the given entity ID.
   */
  List<Staff> listAllByEntityId(UUID entityId);

  /**
   * Checks if a Staff entity exists for a given account ID.
   *
   * @param accountId the account ID to check.
   * @return true if a Staff entity exists for the given account ID, false otherwise.
   */
  boolean existsByAccountId(UUID accountId);
}
