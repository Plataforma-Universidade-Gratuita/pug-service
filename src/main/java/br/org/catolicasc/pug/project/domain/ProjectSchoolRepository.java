package br.org.catolicasc.pug.project.domain;

import java.util.Set;
import java.util.UUID;

/**
 * Domain repository interface for managing {@link ProjectSchool} aggregate roots.
 *
 * <p>This interface defines the contract for persisting project-to-school association aggregates,
 * as well as removing existing associations either in bulk or for a specific project–school pair.
 */
public interface ProjectSchoolRepository {

  /**
   * Removes a specific association between a project and a school.
   *
   * <p>The implementation is expected to use the {@code projectId} and {@code schoolId} contained
   * in the provided {@link ProjectSchool} aggregate to identify and delete a single matching
   * record.
   *
   * @param association the {@link ProjectSchool} aggregate containing both identifiers
   * @return {@code true} if an association was deleted, {@code false} otherwise
   */
  boolean delete(ProjectSchool association);

  /**
   * Removes all associations for the specified project.
   *
   * <p>The implementation is expected to use the provided {@code projectId} to identify and delete
   * all matching {@link ProjectSchool} records.
   *
   * @param projectId the unique identifier of the project
   * @return the number of associations deleted
   */
  long deleteAllByProjectId(UUID projectId);

  /**
   * Removes all associations for the specified school.
   *
   * <p>The implementation is expected to use the provided {@code schoolId} to identify and delete
   * all matching {@link ProjectSchool} records.
   *
   * @param schoolId the unique identifier of the school
   * @return the number of associations deleted
   */
  long deleteAllBySchoolId(UUID schoolId);

  /**
   * Retrieves the identifiers of all schools associated with the given project.
   *
   * <p>This method is intended for write-side orchestration (e.g., when updating or deleting a
   * project and needing to understand its current school links). It does not instantiate full
   * association aggregates, returning only the foreign key identifiers.
   *
   * @param projectId the unique identifier of the project
   * @return a {@link Set} of school UUIDs associated with the project; an empty set if {@code
   *     projectId} is {@code null} or if no associations exist
   */
  Set<UUID> findAllSchoolIdsByProjectId(UUID projectId);

  /**
   * Retrieves the identifiers of all projects associated with the given school.
   *
   * <p>This method is intended for write-side orchestration (e.g., when updating or deleting a
   * school and needing to understand its current project links). It does not instantiate full
   * association aggregates, returning only the foreign key identifiers.
   *
   * @param schoolId the unique identifier of the school
   * @return a {@link Set} of project UUIDs associated with the school; an empty set if {@code
   *     schoolId} is {@code null} or if no associations exist
   */
  Set<java.util.UUID> findAllProjectIdsBySchoolId(UUID schoolId);

  /**
   * Persists a newly created {@link ProjectSchool} association into the repository.
   *
   * @param association the {@link ProjectSchool} aggregate to persist
   * @return the fully persisted {@link ProjectSchool} instance
   */
  ProjectSchool persist(ProjectSchool association);
}
