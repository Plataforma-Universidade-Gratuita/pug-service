package br.org.catolicasc.pug.project.domain;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Domain repository interface for managing {@link ProjectAreaOfExpertise} aggregate roots.
 *
 * <p>This interface defines the contract for persisting project-to-areaOfExpertise association
 * aggregates, as well as removing existing associations either in bulk or for a specific
 * project–areaOfExpertise pair.
 */
public interface ProjectAreaOfExpertiseRepository {

  /**
   * Removes a specific association between a project and a areaOfExpertise.
   *
   * <p>The implementation is expected to use the {@code projectId} and {@code areaOfExpertiseId}
   * contained in the provided {@link ProjectAreaOfExpertise} aggregate to identify and delete a
   * single matching record.
   *
   * @param association the {@link ProjectAreaOfExpertise} aggregate containing both identifiers
   * @return {@code true} if an association was deleted, {@code false} otherwise
   */
  boolean delete(ProjectAreaOfExpertise association);

  /**
   * Removes all associations for the specified project.
   *
   * <p>The implementation is expected to use the provided {@code projectId} to identify and delete
   * all matching {@link ProjectAreaOfExpertise} records.
   *
   * @param projectId the unique identifier of the project
   * @return the number of associations deleted
   */
  long deleteAllByProjectId(UUID projectId);

  /**
   * Removes all associations for the specified areaOfExpertise.
   *
   * <p>The implementation is expected to use the provided {@code areaOfExpertiseId} to identify and
   * delete all matching {@link ProjectAreaOfExpertise} records.
   *
   * @param areaOfExpertiseId the unique identifier of the areaOfExpertise
   * @return the number of associations deleted
   */
  long deleteAllByAreaOfExpertiseId(UUID areaOfExpertiseId);

  /**
   * Retrieves the identifiers of all areaOfExpertises associated with the given project.
   *
   * <p>This method is intended for write-side orchestration (e.g., when updating or deleting a
   * project and needing to understand its current areaOfExpertise links). It does not instantiate
   * full association aggregates, returning only the foreign key identifiers.
   *
   * @param projectId the unique identifier of the project
   * @return a {@link Set} of areaOfExpertise UUIDs associated with the project; an empty set if
   *     {@code projectId} is {@code null} or if no associations exist
   */
  Set<UUID> findAllAreaOfExpertiseIdsByProjectId(UUID projectId);

  /**
   * Retrieves all fully reconstituted academic areas of expertise linked to the given project.
   *
   * @param projectId the unique identifier of the project
   * @return a {@link List} of linked {@link AreaOfExpertise} aggregates, or an empty list if none
   *     exist
   */
  List<AreaOfExpertise> findAllAreasOfExpertiseByProjectId(UUID projectId);

  /**
   * Retrieves the identifiers of all projects associated with the given areaOfExpertise.
   *
   * <p>This method is intended for write-side orchestration (e.g., when updating or deleting a
   * areaOfExpertise and needing to understand its current project links). It does not instantiate
   * full association aggregates, returning only the foreign key identifiers.
   *
   * @param areaOfExpertiseId the unique identifier of the areaOfExpertise
   * @return a {@link Set} of project UUIDs associated with the areaOfExpertise; an empty set if
   *     {@code areaOfExpertiseId} is {@code null} or if no associations exist
   */
  Set<UUID> findAllProjectIdsByAreaOfExpertiseId(UUID areaOfExpertiseId);

  /**
   * Persists a newly created {@link ProjectAreaOfExpertise} association into the repository.
   *
   * @param association the {@link ProjectAreaOfExpertise} aggregate to persist
   * @return the fully persisted {@link ProjectAreaOfExpertise} instance
   */
  ProjectAreaOfExpertise persist(ProjectAreaOfExpertise association);
}
