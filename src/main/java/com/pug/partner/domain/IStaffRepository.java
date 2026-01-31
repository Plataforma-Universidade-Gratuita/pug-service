package com.pug.partner.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for managing Staff entities. */
public interface IStaffRepository {

  /**
   * Persists a Staff entity.
   *
   * @param staff the Staff entity to persist.
   * @return the persisted Staff entity.
   */
  Staff persist(Staff staff);

  /**
   * Persists multiple Staff entities.
   *
   * @param staff an iterable of Staff entities to persist.
   * @return a list of the persisted Staff entities.
   */
  List<Staff> persistAll(Iterable<Staff> staff);

  /**
   * Updates an existing Staff entity.
   *
   * @param entity the Staff entity to update.
   */
  void update(Staff entity);

  /**
   * Deletes Staff entities by their associated account IDs.
   *
   * @param ids an iterable of account IDs whose Staff entities should be deleted.
   * @return the number of Staff entities deleted.
   */
  long deleteByIds(Iterable<UUID> ids);

  /**
   * Finds a Staff entity by its associated account ID.
   *
   * @param id the account ID to search for.
   * @return an Optional containing the found Staff entity, or empty if not found.
   *     <p>Note: The returned Staff may contain validation errors (check {@code staff.hasErrors()})
   *     if the stored data is inconsistent with current domain rules.
   */
  Optional<Staff> findOptionalById(UUID id);

  /**
   * Lists all Staff entities.
   *
   * @return a list of all Staff entities.
   *     <p>Note: The returned Staff objects may contain validation errors (check {@code
   *     staff.hasErrors()}) if the stored data is inconsistent with current domain rules.
   */
  List<Staff> listAllStaff();

  /**
   * Lists all Staff entities associated with a specific entity ID.
   *
   * @param entityId the entity ID to filter by.
   * @return a list of Staff entities associated with the given entity ID.
   *     <p>Note: The returned Staff objects may contain validation errors (check {@code
   *     staff.hasErrors()}) if the stored data is inconsistent with current domain rules.
   */
  List<Staff> listAllByEntityId(UUID entityId);

  /**
   * Checks if a Staff entity exists for a given account ID.
   *
   * @param id the account ID to check.
   * @return true if a Staff entity exists for the given account ID, false otherwise.
   */
  boolean existsByAccountId(UUID id);

  /**
   * Checks if any Staff entities exist for the given account IDs.
   *
   * @param ids an iterable of account IDs to check.
   * @return true if any Staff entities exist for the provided account IDs, false otherwise.
   */
  boolean existsAnyByAccountIdIn(Iterable<UUID> ids);
}
