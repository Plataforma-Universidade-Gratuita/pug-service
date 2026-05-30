package br.org.catolicasc.pug.project.service;

import br.org.catolicasc.pug.academic.infra.read.dtos.AreaOfExpertiseView;
import br.org.catolicasc.pug.project.domain.ProjectAreaOfExpertiseRepository;
import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import java.util.Set;
import java.util.UUID;

/**
 * Application service interface dedicated exclusively to querying the association between projects
 * and areas of expertise.
 *
 * <p>Following CQRS principles, this service handles the query operations for the
 * project-to-area-of-expertise link. It leverages the underlying {@link ProjectAreaOfExpertiseRepository} to
 * resolve identifiers and then projects them into lightweight read models.
 */
public interface ProjectAreaOfExpertiseReadService {

  /**
   * Retrieves all areas of expertise associated with the specified project.
   *
   * <p>This method:
   *
   * <ul>
   *   <li>Resolves all area-of-expertise identifiers linked to the given {@code projectId}, and
   *   <li>Projects them into {@link AreaOfExpertiseView} DTOs.
   * </ul>
   *
   * @param projectId the unique identifier of the project
   * @return a {@link Set} of {@link AreaOfExpertiseView} associated with the project; an empty set
   *     if {@code projectId} is {@code null} or if no associations exist
   */
  Set<AreaOfExpertiseView> listAllAreasOfExpertiseByProjectId(UUID projectId);

  /**
   * Retrieves all projects associated with the specified area of expertise.
   *
   * <p>This method:
   *
   * <ul>
   *   <li>Resolves all project identifiers linked to the given {@code areaOfExpertiseId}, and
   *   <li>Projects them into {@link ProjectView} DTOs.
   * </ul>
   *
   * @param areaOfExpertiseId the unique identifier of the area of expertise
   * @return a {@link Set} of {@link ProjectView} associated with the area of expertise; an empty
   *     set if {@code areaOfExpertiseId} is {@code null} or if no associations exist
   */
  Set<ProjectView> listAllProjectsByAreaOfExpertiseId(UUID areaOfExpertiseId);
}
