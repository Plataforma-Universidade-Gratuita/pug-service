/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.service.impl;

import br.org.catolicasc.pug.academic.infra.read.AreasOfExpertiseQueries;
import br.org.catolicasc.pug.academic.infra.read.dtos.AreaOfExpertiseView;
import br.org.catolicasc.pug.academic.service.AreasOfExpertiseReadService;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseComplexSearchCriteria;
import br.org.catolicasc.pug.academic.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/** Query-side service implementation for academic areas of expertise. */
@jakarta.enterprise.context.ApplicationScoped
public class AreasOfExpertiseReadServiceImpl implements AreasOfExpertiseReadService {

  private static final Logger LOG = Logger.getLogger(AreasOfExpertiseReadServiceImpl.class);

  @jakarta.inject.Inject AreasOfExpertiseQueries queries;

  /** {@inheritDoc} */
  @Override
  public AreaOfExpertiseView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () -> {
              LOG.debugf("Area of expertise lookup failed: ID %s not found", id);
              return ExceptionHelper.areaOfExpertiseNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public List<AreaOfExpertiseView> listViews() {
    return queries.listAllViews();
  }

  /** {@inheritDoc} */
  @Override
  public List<AreaOfExpertiseView> listViewsByIds(List<UUID> ids) {
    return queries.listAllByIds(ids);
  }

  /** {@inheritDoc} */
  @Override
  public PageResult<AreaOfExpertiseView> search(
      PageQuery pageQuery, AreaOfExpertiseComplexSearchCriteria criteria) {
    return queries.search(pageQuery, criteria);
  }
}
