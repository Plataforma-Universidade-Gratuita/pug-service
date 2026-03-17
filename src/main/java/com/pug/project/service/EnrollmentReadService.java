package com.pug.project.service;

import com.pug.project.infra.read.dtos.EnrollmentView;
import java.util.List;
import java.util.UUID;

/** Application service interface dedicated exclusively to querying Enrollment data. */
public interface EnrollmentReadService {

  /**
   * Retrieves a read-only projection of an enrollment based on its composite identifier.
   *
   * @param projectId the unique identifier (UUID) of the project
   * @param studentId the unique identifier (UUID) of the student
   * @return the populated {@link EnrollmentView} DTO
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if not found
   */
  EnrollmentView getViewByIds(UUID projectId, UUID studentId);

  /**
   * Retrieves a comprehensive list of all enrollments registered in the system.
   *
   * @return a {@link List} containing all available {@link EnrollmentView} entries
   */
  List<EnrollmentView> listViews();

  /**
   * Retrieves a list of enrollments for a specific project.
   *
   * @param projectId the unique identifier (UUID) of the project
   * @return a {@link List} of matching {@link EnrollmentView} entries
   */
  List<EnrollmentView> listViewsByProjectId(UUID projectId);

  /**
   * Retrieves a list of enrollments for a specific student.
   *
   * @param studentId the unique identifier (UUID) of the student
   * @return a {@link List} of matching {@link EnrollmentView} entries
   */
  List<EnrollmentView> listViewsByStudentId(UUID studentId);
}
