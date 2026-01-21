package com.pug.identity.domain;

import com.pug.shared.exceptions.AppValidationException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing Admin domain objects.
 */
public interface IAdminRepository {
  /**
   * Persists the given Admin domain object.
   *
   * @param entity the Admin to persist.
   * @return the persisted Admin.
   * @throws AppValidationException if the persisted entity cannot be converted back to a valid
   *                                domain object (indicating a data integrity issue).
   */
  Admin persist(Admin entity) throws AppValidationException;

  /**
   * Persists all given Admin domain objects.
   *
   * @param entities the iterable of Admin instances to persist.
   * @return a list of persisted Admin instances.
   * @throws AppValidationException if any persisted entity cannot be converted back to a valid
   *                                domain object (indicating a data integrity issue).
   */
  List<Admin> persistAll(Iterable<Admin> entities) throws AppValidationException;

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
   * @param accountId the account ID of the Admin to find.
   * @return an Optional containing the found Admin, or empty if not found.
   * @throws AppValidationException if an AdminEntity is found but its data is inconsistent with
   *                                domain rules, preventing the creation of a valid domain object.
   */
  Optional<Admin> findOptionalById(UUID accountId) throws AppValidationException;

  /**
   * Lists all Admin instances.
   *
   * @return a list of all Admin instances.
   * @throws AppValidationException if any AdminEntity is found but its data is inconsistent with
   *                                domain rules, preventing the creation of valid domain objects.
   */
  List<Admin> listAllAdmins() throws AppValidationException;

  /**
   * Checks if an Admin exists for the given iterable of account IDs.
   *
   * @param accountIds the iterable of account IDs to check.
   * @return true if an Admin exists for any of the given account IDs, false otherwise.
   */
  boolean existsAnyByAccountIdIn(Iterable<UUID> accountIds);
}