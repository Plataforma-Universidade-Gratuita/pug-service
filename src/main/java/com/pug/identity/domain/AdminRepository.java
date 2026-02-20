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
   * Deletes the Admin with the given account ID.
   *
   * @param accountId the account ID of the Admin to delete.
   * @return true if the Admin was deleted, false if no Admin with the given ID was found.
   */
  boolean deleteByAccountId(UUID accountId);

  /**
   * Finds an Admin by its account ID.
   *
   * <p>Note: The returned Admin may contain validation errors (check {@code admin.hasErrors()}) if
   * the stored data is inconsistent with current domain rules.
   *
   * @param accountId the account ID of the Admin to find.
   * @return an Optional containing the found Admin, or empty if not found.
   */
  Optional<Admin> findOptionalByAccountId(UUID accountId);

  /**
   * Lists all Admin instances.
   *
   * <p>Note: The returned Admins may contain validation errors (check {@code admin.hasErrors()}) if
   * the stored data is inconsistent with current domain rules.
   *
   * @return a list of all Admin instances.
   */
  List<Admin> listAllAdmins();
}
