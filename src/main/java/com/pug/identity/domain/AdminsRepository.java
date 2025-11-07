package com.pug.identity.domain;

import com.pug.identity.infra.persistence.AdminsEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for managing AdminsEntity persistence operations. */
public interface AdminsRepository {
  /**
   * Persists the given AdminsEntity.
   *
   * @param entity the AdminsEntity to persist.
   */
  void persist(AdminsEntity entity);

  /**
   * Persists all given AdminsEntity instances.
   *
   * @param entities the iterable of AdminsEntity instances to persist.
   */
  void persistAll(Iterable<AdminsEntity> entities);

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
  Optional<AdminsEntity> findOptionalById(UUID userId);

  /**
   * Lists all AdminsEntity instances.
   *
   * @return a list of all AdminsEntity instances.
   */
  List<AdminsEntity> listAllAdmins();

  /**
   * Checks if an AdminsEntity exists for the given user ID.
   *
   * @param userId the user ID to check.
   * @return true if an AdminsEntity exists for the given user ID, false otherwise.
   */
  boolean existsByUserId(UUID userId);
}
