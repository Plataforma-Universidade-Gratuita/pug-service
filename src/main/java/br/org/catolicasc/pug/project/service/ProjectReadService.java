package br.org.catolicasc.pug.project.service;

import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import br.org.catolicasc.pug.project.service.dtos.ProjectComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.UUID;

/**
 * Application service interface dedicated exclusively to querying project data.
 *
 * <p>Following CQRS principles, this service handles read-only project operations, from direct
 * lookups to collection filters and paginated complex-search execution.
 */
public interface ProjectReadService {

  /**
   * Retrieves a read-only projection of a project based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the project
   * @return the populated {@link ProjectView} DTO
   */
  ProjectView getViewById(UUID id);

  /**
   * Retrieves every project currently available in the read model.
   *
   * @return a sorted {@link List} containing every available {@link ProjectView}
   */
  List<ProjectView> listViews();

  /**
   * Retrieves every project whose identifier is contained in the provided collection.
   *
   * @param ids the project identifiers that should be resolved
   * @return a sorted {@link List} of projects matching the provided identifiers
   */
  List<ProjectView> listViewsByIds(List<UUID> ids);

  /**
   * Retrieves every project created by the provided account identifier.
   *
   * @param accountId the unique identifier of the creator account
   * @return a sorted {@link List} of projects created by the provided account
   */
  List<ProjectView> listViewsByCreatedBy(UUID accountId);

  /**
   * Retrieves every project associated with the provided partner-entity identifier.
   *
   * @param entityId the unique identifier of the partner entity
   * @return a sorted {@link List} of projects associated with the provided entity
   */
  List<ProjectView> listViewsByEntityId(UUID entityId);

  /**
   * Executes the paginated complex-search flow for projects.
   *
   * @param criteria the optional search criteria supplied by the caller
   * @param pageQuery the requested pagination window
   * @return the paginated result containing the matching project projections
   */
  PageResult<ProjectView> search(ProjectComplexSearchCriteria criteria, PageQuery pageQuery);
}
