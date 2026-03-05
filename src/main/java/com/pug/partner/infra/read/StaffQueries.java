package com.pug.partner.infra.read;

import com.pug.partner.infra.read.dtos.StaffView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only interface for executing Staff profile queries.
 *
 * <p>This interface represents the "Query" side of a CQRS architecture. It defines operations for
 * retrieving consolidated staff profiles directly into lightweight {@link StaffView} projections.
 * These views aggregate data across the Identity, Geo, and Partner contexts for optimized API
 * delivery.
 */
public interface StaffQueries {

  /**
   * Retrieves a read-only view of a staff member based on their registered email address.
   *
   * @param email the exact email address of the staff member
   * @return an {@link Optional} containing the {@link StaffView} if found, otherwise empty
   */
  Optional<StaffView> findOptionalByEmail(String email);

  /**
   * Retrieves a read-only view of a staff member based on their linked account ID.
   *
   * @param id the unique identifier (UUID) of the staff member's account
   * @return an {@link Optional} containing the {@link StaffView} if found, otherwise empty
   */
  Optional<StaffView> findOptionalById(UUID id);

  /**
   * Retrieves a list of all staff members linked to a specific partner organization.
   *
   * @param entityId the unique identifier (UUID) of the partner entity
   * @return a {@link List} of {@link StaffView} records associated with the given entity
   */
  List<StaffView> listAllByEntityId(UUID entityId);

  /**
   * Retrieves a comprehensive list of all staff members registered in the system.
   *
   * <p><i>Note:</i> Use with caution if the dataset grows significantly, as this method does not
   * implement pagination.
   *
   * @return a {@link List} of all {@link StaffView} records
   */
  List<StaffView> listAllStaff();

  /**
   * Retrieves a list of staff members filtered by their linked user's CPF.
   *
   * @param cpf the exact 11-digit numeric CPF string
   * @return a {@link List} of {@link StaffView} records matching the given CPF
   */
  List<StaffView> listByCpf(String cpf);

  /**
   * Executes a robust full-text search against the names of the associated staff users.
   *
   * <p>This method typically leverages underlying indexing engines (e.g., Elasticsearch via
   * Hibernate Search).
   *
   * @param key the raw search string or partial name of the staff member
   * @return a sorted {@link List} of matching {@link StaffView} records
   */
  List<StaffView> searchByName(String key);
}
