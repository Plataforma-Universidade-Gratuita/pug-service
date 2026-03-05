package com.pug.academic.service;

import com.pug.academic.infra.read.dtos.SchoolView;

import java.util.List;
import java.util.UUID;

/**
 * Application service interface dedicated exclusively to querying Academic School data.
 * <p>
 * Following CQRS principles, this service handles the "Query" operations. It bypasses
 * complex domain logic and retrieves lightweight {@link SchoolView} Data Transfer Objects
 * directly from the underlying data store or search indices.
 */
public interface SchoolReadService {

  /**
   * Retrieves a read-only projection of a school based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the school
   * @return the populated {@link SchoolView} DTO
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no school matches the provided ID
   */
  SchoolView getViewById(UUID id);

  /**
   * Retrieves a comprehensive list of all academic schools registered in the system.
   * <p>
   * <i>Note:</i> This method returns the entire dataset. It should be used judiciously
   * in contexts where the dataset size is known to be safely bounded.
   *
   * @return a {@link List} containing all available {@link SchoolView} entries
   */
  List<SchoolView> listAll();

  /**
   * Executes a robust full-text search against the names of registered schools.
   * <p>
   * Leverages advanced text analysis (e.g., Elasticsearch via Hibernate Search) to provide
   * fuzzy matching, accent-insensitivity, and predictive autocomplete capabilities.
   *
   * @param key the raw search string or partial name provided by the client
   * @return a sorted {@link List} of matching {@link SchoolView} entries
   */
  List<SchoolView> searchByName(String key);
}