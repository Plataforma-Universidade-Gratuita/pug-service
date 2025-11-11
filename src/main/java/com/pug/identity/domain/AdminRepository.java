package com.pug.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for managing AdminsEntity persistence operations. */
public interface AdminRepository {
  /**
   * Persists the given AdminsEntity.
   *
   * @param entity the AdminsEntity to persist.
   * @return the persisted AdminsEntity.
   */
  Admin persist(Admin entity);

  /**
   * Persists all given AdminsEntity instances.
   *
   * @param entities the iterable of AdminsEntity instances to persist.
   * @return a list of persisted AdminsEntity instances.
   */
  List<Admin> persistAll(Iterable<Admin> entities);

  /**
   * Deletes AdminsEntity instances by their user IDs.
   *
   * @param ids the iterable of user IDs whose AdminsEntity instances should be deleted.
   * @return the number of entities deleted.
   */
  long deleteByIds(Iterable<UUID> ids);

  /**
   * Finds an AdminsEntity by its user ID.
   *
   * @param userId the user ID of the AdminsEntity to find.
   * @return an Optional containing the found AdminsEntity, or empty if not found.
   */
  Optional<Admin> findOptionalById(UUID userId);

  /**
   * Lists all AdminsEntity instances.
   *
   * @return a list of all AdminsEntity instances.
   */
  List<Admin> listAllAdmins();

  /**
   * Checks if an AdminsEntity exists for the given iterable of user IDs.
   *
   * @param userIds the iterable of user IDs to check.
   * @return true if an AdminsEntity exists for any of the given user IDs, false otherwise.
   */
  boolean existsAnyByUserIdIn(Iterable<UUID> userIds);
}
