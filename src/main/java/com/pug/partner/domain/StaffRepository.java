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
   * Persists multiple Staff entities.
   *
   * @param staff an iterable of Staff entities to persist.
   * @return a list of the persisted Staff entities.
   */
  List<Staff> persistAll(Iterable<Staff> staff);

  /**
   * Deletes Staff entities by their associated user IDs.
   *
   * @param userIds an iterable of user IDs whose Staff entities should be deleted.
   * @return the number of Staff entities deleted.
   */
  long deleteByUserIds(Iterable<UUID> userIds);

  /**
   * Finds a Staff entity by its associated user ID.
   *
   * @param userId the user ID to search for.
   * @return an Optional containing the found Staff entity, or empty if not found.
   */
  Optional<Staff> findOptionalByUserId(UUID userId);

  /**
   * Lists all Staff entities.
   *
   * @return a list of all Staff entities.
   */
  List<Staff> listAllStaff();

  /**
   * Lists all Staff entities associated with a specific entity ID.
   *
   * @param entityId the entity ID to filter by.
   * @return a list of Staff entities associated with the given entity ID.
   */
  List<Staff> listAllByEntityId(UUID entityId);

  /**
   * Checks if a Staff entity exists for a given user ID.
   *
   * @param userId the user ID to check.
   * @return true if a Staff entity exists for the given user ID, false otherwise.
   */
  boolean existsByUserId(UUID userId);

  /**
   * Checks if any Staff entities exist for the given user IDs.
   *
   * @param userIds an iterable of user IDs to check.
   * @return true if any Staff entities exist for the given user IDs, false otherwise.
   */
  boolean existsAnyByUserIdIn(Iterable<UUID> userIds);
}
