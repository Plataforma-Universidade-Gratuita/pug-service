package br.org.catolicasc.pug.project.infra.read;

import br.org.catolicasc.pug.project.infra.read.dtos.AttendanceView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only interface for executing attendance queries.
 *
 * <p>Represents the Query side of a CQRS architecture, retrieving lightweight {@link
 * AttendanceView} projections.
 */
public interface AttendanceQueries {

  /**
   * Retrieves a read-only view of an attendance record based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the attendance
   * @return an {@link Optional} containing the {@link AttendanceView} if found, or empty otherwise
   */
  Optional<AttendanceView> findOptionalById(UUID id);

  /**
   * Retrieves a list of attendance records associated with a specific project and formerStudent.
   *
   * @param projectId the unique identifier of the project
   * @param studentId the unique identifier of the formerStudent account
   * @return a {@link List} of matching {@link AttendanceView} entries
   */
  List<AttendanceView> listByEnrollmentId(UUID projectId, UUID studentId);

  /**
   * Retrieves a list of attendance records for a specific project.
   *
   * @param projectId the unique identifier (UUID) of the project
   * @return a {@link List} of matching {@link AttendanceView} entries
   */
  List<AttendanceView> listByProjectId(UUID projectId);

  /**
   * Retrieves a list of attendance records for a specific formerStudent.
   *
   * @param studentId the unique identifier (UUID) of the formerStudent account
   * @return a {@link List} of matching {@link AttendanceView} entries
   */
  List<AttendanceView> listByStudentId(UUID studentId);

  /**
   * Retrieves a comprehensive list of all attendance records registered in the system.
   *
   * @return a {@link List} containing all available {@link AttendanceView} entries
   */
  List<AttendanceView> listViews();
}
