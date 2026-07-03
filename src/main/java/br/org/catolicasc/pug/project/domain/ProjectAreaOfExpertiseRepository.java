/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.domain;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Domain repository interface for managing {@link ProjectAreaOfExpertise} aggregate roots.
 *
 * <p>This boundary persists project-to-area-of-expertise associations and exposes the identifier
 * lookups needed by write-side orchestration and read-side projections.
 */
public interface ProjectAreaOfExpertiseRepository {

  /**
   * Removes a specific association between a project and an area of expertise.
   *
   * @param association the association aggregate containing both identifiers
   * @return {@code true} if an association was deleted, or {@code false} otherwise
   */
  boolean delete(ProjectAreaOfExpertise association);

  /**
   * Removes all associations linked to the specified project.
   *
   * @param projectId the unique identifier of the project
   * @return the number of associations deleted
   */
  long deleteAllByProjectId(UUID projectId);

  /**
   * Removes all associations linked to the specified area of expertise.
   *
   * @param areaOfExpertiseId the unique identifier of the area of expertise
   * @return the number of associations deleted
   */
  long deleteAllByAreaOfExpertiseId(UUID areaOfExpertiseId);

  /**
   * Retrieves the identifiers of all areas of expertise associated with the given project.
   *
   * @param projectId the unique identifier of the project
   * @return a {@link Set} containing the linked area-of-expertise identifiers, or an empty set when
   *     none exist
   */
  Set<UUID> findAllAreaOfExpertiseIdsByProjectId(UUID projectId);

  /**
   * Retrieves all fully reconstituted academic areas of expertise linked to the given project.
   *
   * @param projectId the unique identifier of the project
   * @return the linked {@link AreaOfExpertise} aggregates, or an empty list when none exist
   */
  List<AreaOfExpertise> findAllAreasOfExpertiseByProjectId(UUID projectId);

  /**
   * Retrieves the identifiers of all projects associated with the given area of expertise.
   *
   * @param areaOfExpertiseId the unique identifier of the area of expertise
   * @return a {@link Set} containing the linked project identifiers, or an empty set when none
   *     exist
   */
  Set<UUID> findAllProjectIdsByAreaOfExpertiseId(UUID areaOfExpertiseId);

  /**
   * Persists a newly created project-to-area-of-expertise association.
   *
   * @param association the association aggregate to persist
   * @return the fully persisted {@link ProjectAreaOfExpertise} aggregate
   */
  ProjectAreaOfExpertise persist(ProjectAreaOfExpertise association);
}
