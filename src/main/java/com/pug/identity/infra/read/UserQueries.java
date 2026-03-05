package com.pug.identity.infra.read;

import com.pug.identity.infra.read.dtos.UserView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only interface for executing user queries.
 *
 * <p>This interface represents the "Query" side of a CQRS architecture. It defines operations for
 * retrieving user data directly into lightweight {@link UserView} projections, bypassing the
 * overhead of instantiating full JPA entities or domain aggregates.
 */
public interface UserQueries {

  /**
   * Retrieves a read-only view of a user based on their exact CPF.
   *
   * @param cpf the exact 11-digit numeric CPF string
   * @return an {@link Optional} containing the {@link UserView} if found, or {@link
   *     Optional#empty()} otherwise
   */
  Optional<UserView> findOptionalByCpf(String cpf);

  /**
   * Retrieves a read-only view of a user based on their unique identifier.
   *
   * @param id the unique identifier (UUID) of the user to retrieve
   * @return an {@link Optional} containing the {@link UserView} if found, or {@link
   *     Optional#empty()} otherwise
   */
  Optional<UserView> findOptionalById(UUID id);

  /**
   * Retrieves a comprehensive list of all users registered in the system.
   *
   * <p><i>Note:</i> Use with caution if the dataset grows significantly, as this method does not
   * implement pagination.
   *
   * @return a {@link List} of all {@link UserView} entries available in the database
   */
  List<UserView> listAllUsers();

  /**
   * Executes a robust full-text search against user names.
   *
   * <p>This method typically leverages underlying indexing engines (e.g., Elasticsearch via
   * Hibernate Search) to provide fuzzy matching, accent-insensitivity, and autocomplete
   * capabilities.
   *
   * @param key the raw search string or partial name provided by the client
   * @return a sorted {@link List} of {@link UserView} entries matching the search criteria, ordered
   *     by search relevance
   */
  List<UserView> searchByName(String key);
}
