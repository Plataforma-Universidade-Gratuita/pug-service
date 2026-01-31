package com.pug.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for managing Admin domain objects. */
public interface AdminRepository {
  /**
   * Persists the given Admin domain object.
   *
   * @param entity the Admin to persist.
   * @return the persisted Admin.
   */
  Admin persist(Admin entity);

  /**
   * Persists all given Admin domain objects.
   *
   * @param entities the iterable of Admin instances to persist.
   * @return a list of persisted Admin instances.
   */
  List<Admin> persistAll(Iterable<Admin> entities);

  /**
   * Deletes Admin instances by their account IDs.
   *
   * @param ids the iterable of account IDs whose Admin instances should be deleted.
   * @return the number of entities deleted.
   */
  long deleteByIds(Iterable<UUID> ids);

  /**
   * Finds an Admin by its account ID.
   *
   * <p>Note: The returned Admin may contain validation errors (check {@code admin.hasErrors()}) if
   * the stored data is inconsistent with current domain rules.
   *
   * @param accountId the account ID of the Admin to find.
   * @return an Optional containing the found Admin, or empty if not found.
   */
  Optional<Admin> findOptionalById(UUID accountId);

  /**
   * Lists all Admin instances.
   *
   * <p>Note: The returned Admins may contain validation errors (check {@code admin.hasErrors()}) if
   * the stored data is inconsistent with current domain rules.
   *
   * @return a list of all Admin instances.
   */
  List<Admin> listAllAdmins();

  /**
   * Checks if an Admin exists for the given iterable of account IDs.
   *
   * @param accountIds the iterable of account IDs to check.
   * @return true if an Admin exists for any of the given account IDs, false otherwise.
   */
  boolean existsAnyByAccountIdIn(Iterable<UUID> accountIds);
}
