package br.org.catolicasc.pug.project.infra.read;

import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.infra.read.dtos.EnrollmentView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only interface for executing queries against {@link Enrollment}
 * records.
 *
 * <p>This interface represents the "Query" side of a CQRS architecture. It defines operations for
 * retrieving enrollment data directly into lightweight, read-optimized {@link EnrollmentView}
 * projections, bypassing the overhead of instantiating full domain aggregates.
 */
public interface EnrollmentQueries {

  /**
   * Retrieves a read-only view of a specific enrollment based on its composite identifier.
   *
   * <p>The composite identifier is given by the pair ({@code projectId}, {@code studentId}), which
   * together uniquely identify an enrollment record.
   *
   * @param projectId the unique identifier (UUID) of the project
   * @param studentId the unique identifier (UUID) of the student account
   * @return an {@link Optional} containing the {@link EnrollmentView} if found, or {@link
   *     Optional#empty()} if no matching enrollment exists
   */
  Optional<EnrollmentView> findOptionalByIds(UUID projectId, UUID studentId);

  /**
   * Retrieves a comprehensive list of all enrollments registered in the system.
   *
   * <p><i>Note:</i> Use with caution if the dataset grows significantly, as this method does not
   * implement pagination and returns the entire result set in a single call.
   *
   * @return a {@link List} containing all available {@link EnrollmentView} entries
   */
  List<EnrollmentView> listAllEnrollments();

  /**
   * Retrieves a list of enrollments associated with a specific project.
   *
   * <p>Each {@link EnrollmentView} in the result set typically includes the fully resolved {@code
   * ProjectView} and {@code StudentView} data required by the presentation layer in a single query
   * round-trip.
   *
   * @param projectId the unique identifier (UUID) of the project
   * @return a {@link List} of {@link EnrollmentView} entries linked to the given project, or an
   *     empty list if none are found
   */
  List<EnrollmentView> listByProjectId(UUID projectId);

  /**
   * Retrieves a list of enrollments associated with a specific student.
   *
   * <p>Each {@link EnrollmentView} in the result set typically includes the fully resolved {@code
   * ProjectView} and {@code StudentView} data required by the presentation layer in a single query
   * round-trip.
   *
   * @param studentId the unique identifier (UUID) of the student account
   * @return a {@link List} of {@link EnrollmentView} entries linked to the given student, or an
   *     empty list if none are found
   */
  List<EnrollmentView> listByStudentId(UUID studentId);
}
