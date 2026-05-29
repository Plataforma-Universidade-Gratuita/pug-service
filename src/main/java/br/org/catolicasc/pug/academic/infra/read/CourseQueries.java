package br.org.catolicasc.pug.academic.infra.read;

import br.org.catolicasc.pug.academic.infra.read.dtos.CourseView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only interface for executing queries against Academic Courses.
 *
 * <p>This interface represents the "Query" side of a CQRS architecture. It defines operations for
 * retrieving course data directly into lightweight {@link CourseView} projections, bypassing the
 * overhead of instantiating full JPA entities or domain aggregates.
 */
public interface CourseQueries {

  /**
   * Retrieves a read-only view of a course based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the course to find
   * @return an {@link Optional} containing the found {@link CourseView}, or {@link
   *     Optional#empty()} if not found
   */
  Optional<CourseView> findOptionalById(UUID id);

  /**
   * Retrieves a list of courses offered by a specific school.
   *
   * @param schoolId the unique identifier (UUID) of the school
   * @return a {@link List} of {@link CourseView} objects linked to the specified school
   */
  List<CourseView> listAllBySchoolId(UUID schoolId);

  /**
   * Retrieves a comprehensive list of all academic courses registered in the system.
   *
   * <p><i>Note:</i> Use with caution if the dataset grows significantly, as this method does not
   * implement pagination.
   *
   * @return a {@link List} of all {@link CourseView} objects
   */
  List<CourseView> listAllCourses();

  /**
   * Executes name-based search against course names.
   *
   * <p>This method applies folded, database-backed filtering on the mapped name field.
   *
   * @param query the raw search string or partial name provided by the client
   * @return a sorted {@link List} of {@link CourseView} entries matching the search criteria
   */
  List<CourseView> searchByName(String query);
}
