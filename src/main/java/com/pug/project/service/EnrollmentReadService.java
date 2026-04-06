package com.pug.project.service;

import com.pug.project.infra.read.dtos.EnrollmentView;
import java.util.List;
import java.util.UUID;

/**
 * Application service interface dedicated exclusively to querying {@link
 * com.pug.project.domain.Enrollment} data.
 *
 * <p>Following CQRS principles, this service handles the "Query" operations. It bypasses complex
 * domain logic and retrieves lightweight {@link EnrollmentView} Data Transfer Objects directly from
 * the underlying read infrastructure, optimized for list and filter operations.
 */
public interface EnrollmentReadService {

  /**
   * Retrieves a read-only projection of an enrollment based on its composite identifier.
   *
   * <p>The composite key is given by the pair ({@code projectId}, {@code studentId}), which
   * together uniquely identify an enrollment record.
   *
   * @param projectId the unique identifier (UUID) of the project
   * @param studentId the unique identifier (UUID) of the student account
   * @return the populated {@link EnrollmentView} DTO
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no enrollment matches the
   *     provided identifiers
   */
  EnrollmentView getViewByIds(UUID projectId, UUID studentId);

  /**
   * Retrieves a comprehensive list of all enrollments registered in the system.
   *
   * <p><i>Note:</i> This method returns the entire dataset. It should be used judiciously in
   * contexts where the dataset size is known to be safely bounded or where additional filtering is
   * applied at a higher layer.
   *
   * @return a {@link List} containing all available {@link EnrollmentView} entries
   */
  List<EnrollmentView> listViews();

  /**
   * Retrieves a list of enrollments for a specific project.
   *
   * <p>The returned {@link EnrollmentView} items expose only identifiers and lifecycle metadata,
   * allowing callers to resolve detailed project and student information on demand via dedicated
   * endpoints.
   *
   * @param projectId the unique identifier (UUID) of the project
   * @return a {@link List} of matching {@link EnrollmentView} entries, or an empty list if none are
   *     found or if {@code projectId} is {@code null}
   */
  List<EnrollmentView> listViewsByProjectId(UUID projectId);

  /**
   * Retrieves a list of enrollments for a specific student.
   *
   * <p>The returned {@link EnrollmentView} items expose only identifiers and lifecycle metadata,
   * allowing callers to resolve detailed project and student information on demand via dedicated
   * endpoints.
   *
   * @param studentId the unique identifier (UUID) of the student account
   * @return a {@link List} of matching {@link EnrollmentView} entries, or an empty list if none are
   *     found or if {@code studentId} is {@code null}
   */
  List<EnrollmentView> listViewsByStudentId(UUID studentId);
}
