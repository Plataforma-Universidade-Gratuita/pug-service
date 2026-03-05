package com.pug.identity.infra.read;

import com.pug.identity.infra.read.dtos.AdminView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only interface for executing administrator profile queries.
 *
 * <p>This interface represents the "Query" side of a CQRS architecture. It defines operations for
 * retrieving consolidated administrator profiles directly into lightweight {@link AdminView}
 * projections. These views typically aggregate data across the Admin, Account, and User contexts
 * for optimized API delivery.
 */
public interface AdminQueries {

  /**
   * Retrieves a read-only view of an administrator profile based on their registered email address.
   *
   * @param email the exact email address of the administrator
   * @return an {@link Optional} containing the {@link AdminView} if found, or {@link
   *     Optional#empty()} otherwise
   */
  Optional<AdminView> findOptionalByEmail(String email);

  /**
   * Retrieves a read-only view of an administrator profile based on their linked account ID.
   *
   * @param accountId the unique identifier (UUID) of the admin's account
   * @return an {@link Optional} containing the {@link AdminView} if found, or {@link
   *     Optional#empty()} otherwise
   */
  Optional<AdminView> findOptionalById(UUID accountId);

  /**
   * Retrieves a comprehensive list of all administrators registered in the system.
   *
   * <p><i>Note:</i> Use with caution if the dataset grows significantly, as this method does not
   * implement pagination.
   *
   * @return a {@link List} of all {@link AdminView} entries available in the database
   */
  List<AdminView> listAllAdmins();

  /**
   * Retrieves a list of administrators filtered by their linked user's CPF.
   *
   * @param cpf the exact 11-digit numeric CPF string of the administrator
   * @return a {@link List} of {@link AdminView} entries matching the given CPF
   */
  List<AdminView> listByCpf(String cpf);

  /**
   * Executes a robust full-text search against the names of the associated administrators.
   *
   * <p>This method typically leverages underlying indexing engines (e.g., Elasticsearch via
   * Hibernate Search) to provide fuzzy matching and predictive autocomplete capabilities based on
   * the admin's name.
   *
   * @param key the raw search string or partial name provided by the client
   * @return a sorted {@link List} of matching {@link AdminView} entries, ordered by relevance
   */
  List<AdminView> searchByName(String key);
}
