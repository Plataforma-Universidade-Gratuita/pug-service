package br.org.catolicasc.pug.academic.service;

import br.org.catolicasc.pug.academic.infra.read.dtos.CourseView;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;

/**
 * Application service interface dedicated exclusively to querying Course data.
 *
 * <p>Following CQRS principles, this service handles the "Query" operations. It bypasses complex
 * domain logic and retrieves lightweight, fully resolved {@link CourseView} Data Transfer Objects
 * directly from the underlying data store or search indices.
 */
public interface CourseReadService {

  /**
   * Retrieves a read-only projection of a course based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the course
   * @return the populated {@link CourseView} DTO
   * @throws ResourceNotFoundException if no course matches the provided ID
   */
  CourseView getViewById(UUID id);

  /**
   * Retrieves a comprehensive list of all courses registered in the system.
   *
   * <p><i>Note:</i> This method returns the entire dataset. It should be used judiciously in
   * contexts where the dataset size is known to be safely bounded.
   *
   * @return a {@link List} containing all available {@link CourseView} entries
   */
  List<CourseView> listViews();

  /**
   * Retrieves a list of courses currently offered by a specific school.
   *
   * @param schoolId the unique identifier (UUID) of the academic school
   * @return a {@link List} of matching {@link CourseView} entries
   */
  List<CourseView> listViewsBySchoolId(UUID schoolId);

  /**
   * Executes a robust name-based search against the names of registered courses.
   *
   * <p>Leverages database-backed filtering (e.g., database-backed filtering) to provide fuzzy
   * matching, accent-insensitivity, and name matching.
   *
   * @param query the raw search string or partial name provided by the client
   * @return a sorted {@link List} of matching {@link CourseView} entries
   */
  List<CourseView> searchByName(String query);
}
