package com.pug.identity.service;

import com.pug.identity.infra.read.dtos.UserView;

import java.util.List;
import java.util.UUID;

/**
 * Application service interface dedicated exclusively to querying User data.
 * <p>
 * Following CQRS principles, this service handles the "Query" operations. It bypasses
 * complex domain logic and retrieves lightweight {@link UserView} Data Transfer Objects
 * directly from the underlying data store or search indices. This is heavily optimized
 * for fast, read-only API responses.
 */
public interface UserReadService {

  /**
   * Retrieves a read-only projection of a user based on their unique identifier.
   *
   * @param id the unique identifier (UUID) of the requested user
   * @return the populated {@link UserView} DTO
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no user matches the provided ID
   */
  UserView getViewById(UUID id);

  /**
   * Retrieves a read-only projection of a user based on their exact CPF string.
   *
   * @param cpf the raw 11-digit numeric CPF string of the requested user
   * @return the populated {@link UserView} DTO
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no user matches the provided CPF
   */
  UserView getViewByCpf(String cpf);

  /**
   * Retrieves a comprehensive list of all users registered in the system.
   * <p>
   * <i>Note:</i> This method returns the entire dataset. It should be used judiciously
   * in contexts where the dataset size is known to be safely bounded.
   *
   * @return a {@link List} containing all available {@link UserView} entries
   */
  List<UserView> listViews();

  /**
   * Executes a robust full-text search against the names of registered users.
   * <p>
   * Leverages advanced text analysis (e.g., Elasticsearch via Hibernate Search) to provide
   * fuzzy matching, accent-insensitivity, and predictive autocomplete capabilities.
   *
   * @param query the raw search string or partial name provided by the client
   * @return a sorted {@link List} of matching {@link UserView} entries
   */
  List<UserView> search(String query);
}