package br.org.catolicasc.pug.academic.infra.read;

import br.org.catolicasc.pug.academic.infra.read.dtos.CourseView;
import br.org.catolicasc.pug.academic.service.dtos.courses.CourseComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only interface for executing queries against academic courses.
 *
 * <p>This interface represents the query side of the course CQRS flow. It defines operations for
 * retrieving course data directly into lightweight {@link CourseView} projections.
 */
public interface CoursesQueries {

  /**
   * Retrieves a read-only view of a course based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the course to find
   * @return an {@link Optional} containing the found {@link CourseView}, or {@link
   *     Optional#empty()} if not found
   */
  Optional<CourseView> findOptionalById(UUID id);

  /**
   * Retrieves courses restricted to the provided identifiers.
   *
   * @param ids the course identifiers to resolve
   * @return a sorted {@link List} of matching {@link CourseView} objects
   */
  List<CourseView> listAllByIds(List<UUID> ids);

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
   * Executes paginated course search using the academic complex-search contract.
   *
   * @param pageQuery the requested pagination information
   * @param criteria the optional search criteria
   * @return a paginated result containing matching {@link CourseView} projections
   */
  PageResult<CourseView> search(PageQuery pageQuery, CourseComplexSearchCriteria criteria);
}
