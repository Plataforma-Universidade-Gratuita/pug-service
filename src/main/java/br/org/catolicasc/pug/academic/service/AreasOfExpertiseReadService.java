package br.org.catolicasc.pug.academic.service;

import br.org.catolicasc.pug.academic.infra.read.dtos.AreaOfExpertiseView;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.UUID;

/**
 * Query-side service interface for academic areas of expertise.
 *
 * <p>This read-side service exposes lookup, listing, and complex-search operations for academic
 * areas of expertise. It returns immutable read-model projections and delegates
 * persistence-specific query behavior to the infrastructure layer, keeping query flows separated
 * from command operations.
 */
public interface AreasOfExpertiseReadService {

  /**
   * Retrieves an area-of-expertise read-model projection by its unique identifier.
   *
   * @param id the unique identifier of the area of expertise
   * @return the matching {@link AreaOfExpertiseView}
   */
  AreaOfExpertiseView getViewById(UUID id);

  /**
   * Retrieves all area-of-expertise read-model projections.
   *
   * @return a list containing all available {@link AreaOfExpertiseView} projections
   */
  List<AreaOfExpertiseView> listViews();

  /**
   * Retrieves area-of-expertise read-model projections restricted to the provided identifiers.
   *
   * @param ids the identifiers used to restrict the returned areas of expertise
   * @return a list containing the matching {@link AreaOfExpertiseView} projections
   */
  List<AreaOfExpertiseView> listViewsByIds(List<UUID> ids);

  /**
   * Executes paginated area-of-expertise search using the complex-search criteria.
   *
   * @param pageQuery the pagination request containing page, size, and fetch-all behavior
   * @param criteria the area-of-expertise search filters to apply
   * @return a paginated {@link PageResult} containing {@link AreaOfExpertiseView} projections
   */
  PageResult<AreaOfExpertiseView> search(
      PageQuery pageQuery, AreaOfExpertiseComplexSearchCriteria criteria);
}
