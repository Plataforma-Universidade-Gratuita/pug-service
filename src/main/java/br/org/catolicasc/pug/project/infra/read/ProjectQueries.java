package br.org.catolicasc.pug.project.infra.read;

import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import br.org.catolicasc.pug.project.service.dtos.projects.ProjectComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only interface for executing project queries and paginated project searches.
 *
 * <p>This interface represents the "Query" side of the project module. It exposes lightweight
 * projection-based reads for direct lookups, collection filters, and the complex-search flow used
 * by the presenter contract.
 */
public interface ProjectQueries {

  /**
   * Retrieves a read-only view of a project based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the project to find
   * @return an {@link Optional} containing the found {@link ProjectView}, or {@link
   *     Optional#empty()} when no matching project exists
   */
  Optional<ProjectView> findOptionalById(UUID id);

  /**
   * Retrieves every project currently available in the read model.
   *
   * @return a sorted {@link List} containing every available {@link ProjectView}
   */
  List<ProjectView> listAll();

  /**
   * Retrieves every project created by the provided account identifier.
   *
   * @param accountId the unique identifier of the creator account
   * @return a sorted {@link List} of projects created by the provided account
   */
  List<ProjectView> listAllByCreatedBy(UUID accountId);

  /**
   * Retrieves every project associated with the provided partner-entity identifier.
   *
   * @param entityId the unique identifier of the partner entity
   * @return a sorted {@link List} of projects associated with the provided entity
   */
  List<ProjectView> listAllByEntityId(UUID entityId);

  /**
   * Retrieves every project whose identifier is contained in the provided collection.
   *
   * @param ids the project identifiers that should be resolved
   * @return a sorted {@link List} of projects matching the provided identifiers
   */
  List<ProjectView> listAllByIds(List<UUID> ids);

  /**
   * Executes the paginated complex-search flow for projects.
   *
   * @param criteria the optional search criteria supplied by the caller
   * @param pageQuery the requested pagination window
   * @return the paginated result containing the matching project projections
   */
  PageResult<ProjectView> search(ProjectComplexSearchCriteria criteria, PageQuery pageQuery);
}
