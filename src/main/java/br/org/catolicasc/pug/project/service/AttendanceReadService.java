package br.org.catolicasc.pug.project.service;

import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.infra.read.dtos.AttendanceView;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;

/**
 * Application service interface dedicated exclusively to querying {@link
 * Attendance} data.
 *
 * <p>Following CQRS principles, this service handles the "Query" operations, retrieving lightweight
 * {@link AttendanceView} Data Transfer Objects directly from the read infrastructure.
 */
public interface AttendanceReadService {

  /**
   * Retrieves a read-only projection of an attendance record based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the attendance
   * @return the populated {@link AttendanceView} DTO
   * @throws ResourceNotFoundException if no attendance matches the
   *     provided ID
   */
  AttendanceView getViewById(UUID id);

  /**
   * Retrieves a list of attendance records associated with a specific project and student.
   *
   * @param projectId the unique identifier of the project
   * @param studentId the unique identifier of the student account
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
   * Retrieves a list of attendance records for a specific student.
   *
   * @param studentId the unique identifier (UUID) of the student account
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
