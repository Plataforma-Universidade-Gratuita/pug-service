package br.org.catolicasc.pug.geo.service.impl;

import br.org.catolicasc.pug.geo.infra.read.CitiesQueries;
import br.org.catolicasc.pug.geo.infra.read.dtos.CityView;
import br.org.catolicasc.pug.geo.service.CitiesReadService;
import br.org.catolicasc.pug.geo.service.dtos.CityComplexSearchCriteria;
import br.org.catolicasc.pug.geo.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link CitiesReadService}.
 *
 * <p>This application-scoped bean delegates read-only operations to the underlying {@link
 * CitiesQueries} infrastructure component. It handles basic input validation and translates "not
 * found" states into standardized domain exceptions.
 */
@ApplicationScoped
public class CitiesReadServiceImpl implements CitiesReadService {

  private static final Logger LOG = Logger.getLogger(CitiesReadServiceImpl.class);

  @Inject CitiesQueries queries;

  /** {@inheritDoc} */
  @Override
  public CityView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () -> {
              LOG.debugf("City lookup failed: ID %s not found", id);
              return ExceptionHelper.cityNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public List<CityView> listViews() {
    return queries.listAllCities();
  }

  /** {@inheritDoc} */
  @Override
  public List<CityView> listViewsByIds(List<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }
    return queries.listAllByIds(ids);
  }

  @Override
  public PageResult<CityView> search(PageQuery pageQuery, CityComplexSearchCriteria criteria) {
    return queries.search(pageQuery, criteria);
  }
}
