package com.pug.project.service;

import com.pug.project.infra.read.dtos.AttendanceView;
import java.util.List;
import java.util.UUID;

/** Application service interface dedicated exclusively to querying Attendance data. */
public interface AttendanceReadService {

  /**
   * Retrieves a read-only projection of an attendance record based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the attendance
   * @return the populated {@link AttendanceView} DTO
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if not found
   */
  AttendanceView getViewById(UUID id);

  /**
   * Retrieves a comprehensive list of all attendance records registered in the system.
   *
   * @return a {@link List} containing all available {@link AttendanceView} entries
   */
  List<AttendanceView> listViews();

  /**
   * Retrieves a list of attendance records for a specific project.
   *
   * @param projectId the unique identifier (UUID) of the project
   * @return a {@link List} of matching {@link AttendanceView} entries
   */
  List<AttendanceView> listViewsByProjectId(UUID projectId);

  /**
   * Retrieves a list of attendance records for a specific student.
   *
   * @param studentId the unique identifier (UUID) of the student's account
   * @return a {@link List} of matching {@link AttendanceView} entries
   */
  List<AttendanceView> listViewsByStudentId(UUID studentId);
}
