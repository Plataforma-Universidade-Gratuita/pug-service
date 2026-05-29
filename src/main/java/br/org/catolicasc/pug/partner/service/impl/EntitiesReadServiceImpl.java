package br.org.catolicasc.pug.partner.service.impl;

import br.org.catolicasc.pug.partner.infra.read.EntitiesQueries;
import br.org.catolicasc.pug.partner.infra.read.dtos.EntityComplexSearchView;
import br.org.catolicasc.pug.partner.infra.read.dtos.EntityView;
import br.org.catolicasc.pug.partner.service.EntitiesReadService;
import br.org.catolicasc.pug.partner.service.dtos.EntityComplexSearchCriteria;
import br.org.catolicasc.pug.partner.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link EntitiesReadService}.
 *
 * <p>This application-scoped bean delegates read-only operations to the underlying {@link
 * EntitiesQueries} infrastructure component and translates "not found" states into standardized
 * partner-domain exceptions.
 */
@ApplicationScoped
public class EntitiesReadServiceImpl implements EntitiesReadService {

  private static final Logger LOG = Logger.getLogger(EntitiesReadServiceImpl.class);

  @Inject EntitiesQueries queries;

  /** {@inheritDoc} */
  @Override
  public EntityView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () -> {
              LOG.debugf("Entity lookup failed: ID %s not found", id);
              return ExceptionHelper.entityNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public List<EntityView> listViews() {
    return queries.listAllEntities();
  }

  /** {@inheritDoc} */
  @Override
  public List<EntityView> listViewsByIds(List<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return List.of();
    }
    return queries.listAllByIds(ids);
  }

  /** {@inheritDoc} */
  @Override
  public PageResult<EntityComplexSearchView> search(
      PageQuery pageQuery, EntityComplexSearchCriteria criteria) {
    return queries.search(pageQuery, criteria);
  }
}
