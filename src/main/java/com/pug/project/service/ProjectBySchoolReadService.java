package com.pug.project.service;

import com.pug.academic.infra.read.dtos.SchoolView;
import com.pug.project.infra.read.dtos.ProjectView;
import java.util.Set;
import java.util.UUID;

/**
 * Application service interface dedicated exclusively to querying the association between projects
 * and schools.
 *
 * <p>Following CQRS principles, this service handles the "Query" operations for the project–school
 * link. It leverages the underlying {@link com.pug.project.domain.ProjectBySchoolRepository} to
 * resolve identifiers and then projects them into lightweight read models.
 */
public interface ProjectBySchoolReadService {

  /**
   * Retrieves all schools associated with the specified project.
   *
   * <p>This method:
   *
   * <ul>
   *   <li>Resolves all school identifiers linked to the given {@code projectId}, and
   *   <li>Projects them into {@link SchoolView} DTOs.
   * </ul>
   *
   * @param projectId the unique identifier of the project
   * @return a {@link Set} of {@link SchoolView} associated with the project; an empty set if {@code
   *     projectId} is {@code null} or if no associations exist
   */
  Set<SchoolView> listAllSchoolsByProjectId(UUID projectId);

  /**
   * Retrieves all projects associated with the specified school.
   *
   * <p>This method:
   *
   * <ul>
   *   <li>Resolves all project identifiers linked to the given {@code schoolId}, and
   *   <li>Projects them into {@link ProjectView} DTOs.
   * </ul>
   *
   * @param schoolId the unique identifier of the school
   * @return a {@link Set} of {@link ProjectView} associated with the school; an empty set if {@code
   *     schoolId} is {@code null} or if no associations exist
   */
  Set<ProjectView> listAllProjectsBySchoolId(UUID schoolId);
}
