package br.org.catolicasc.pug.project.service;

import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.ProjectBySchool;
import br.org.catolicasc.pug.project.domain.ProjectBySchoolRepository;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import java.util.List;
import java.util.UUID;

/**
 * Application service interface for managing the association between {@link Project} and {@link
 * School} aggregates via {@link ProjectBySchool}.
 *
 * <p>Following CQRS principles, this service handles the "Command" operations for the
 * project–school link: creation and deletion of associations. It orchestrates validation and
 * delegates persistence concerns to the {@link ProjectBySchoolRepository}.
 */
public interface ProjectBySchoolService {

  /**
   * Creates new associations between a project and one or more schools.
   *
   * <p>This operation:
   *
   * <ul>
   *   <li>Constructs a {@link ProjectBySchool} aggregate for each provided school ID via its
   *       factory method,
   *   <li>validates structural constraints for every association (non-null IDs), and
   *   <li>persists all valid associations using the underlying repository.
   * </ul>
   *
   * @param projectId the unique identifier of the project to link
   * @param schoolIds the unique identifiers of the schools to link to the project
   * @return a {@link List} of fully instantiated and persisted {@link ProjectBySchool} aggregates
   *     corresponding to the provided school IDs
   * @throws AppValidationException if any created association violates domain constraints (e.g.,
   *     null IDs)
   */
  List<ProjectBySchool> save(UUID projectId, List<UUID> schoolIds);

  /**
   * Removes a specific association between a project and a school.
   *
   * <p>This operation is expected to be idempotent: if the association does not exist, the
   * implementation should simply return {@code false} without raising an exception.
   *
   * @param projectId the unique identifier of the project
   * @param schoolId the unique identifier of the school
   * @return {@code true} if an association was deleted, {@code false} otherwise
   */
  boolean delete(UUID projectId, UUID schoolId);

  /**
   * Removes all associations for the specified project.
   *
   * <p>This is typically used when deleting or restructuring a project and needing to clean up all
   * of its school links in a single operation.
   *
   * @param projectId the unique identifier of the project
   * @return the number of associations deleted (zero if {@code projectId} is {@code null} or no
   *     associations exist)
   */
  long deleteAllByProjectId(UUID projectId);

  /**
   * Removes all associations for the specified school.
   *
   * <p>This is typically used when deleting or restructuring a school and needing to clean up all
   * of its project links in a single operation.
   *
   * @param schoolId the unique identifier of the school
   * @return the number of associations deleted (zero if {@code schoolId} is {@code null} or no
   *     associations exist)
   */
  long deleteAllBySchoolId(UUID schoolId);
}
