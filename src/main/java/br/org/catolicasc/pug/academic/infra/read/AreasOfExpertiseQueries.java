/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.infra.read;

import br.org.catolicasc.pug.academic.infra.read.dtos.AreaOfExpertiseView;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only query contract for academic areas-of-expertise projections.
 *
 * <p>This boundary centralizes the lookup, listing, and paginated complex-search operations used by
 * the academic read side. Implementations are expected to return lightweight projections rather
 * than managed entities so presenter flows can remain decoupled from persistence concerns.
 */
public interface AreasOfExpertiseQueries {
  /**
   * Resolves a single area-of-expertise projection by its unique identifier.
   *
   * @param id the unique identifier of the requested area of expertise
   * @return an {@link Optional} containing the matching {@link AreaOfExpertiseView}, or an empty
   *     optional when no row matches the provided identifier
   */
  Optional<AreaOfExpertiseView> findOptionalById(UUID id);

  /**
   * Retrieves all area-of-expertise projections whose identifiers are present in the provided
   * collection.
   *
   * @param ids the identifiers used to restrict the returned projections
   * @return a list containing the matching {@link AreaOfExpertiseView} projections
   */
  List<AreaOfExpertiseView> listAllByIds(List<UUID> ids);

  /**
   * Retrieves every area-of-expertise projection available to the academic read model.
   *
   * @return a list containing all {@link AreaOfExpertiseView} projections
   */
  List<AreaOfExpertiseView> listAllViews();

  /**
   * Executes paginated area-of-expertise search using the provided filtering criteria.
   *
   * @param pageQuery the pagination request containing page and size information
   * @param criteria the search filters to apply to the read model
   * @return a paginated result containing matching {@link AreaOfExpertiseView} projections
   */
  PageResult<AreaOfExpertiseView> search(
      PageQuery pageQuery, AreaOfExpertiseComplexSearchCriteria criteria);
}
