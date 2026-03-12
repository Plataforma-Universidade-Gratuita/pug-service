package com.pug.geo.service.impl;

import com.pug.geo.infra.read.CityQueries;
import com.pug.geo.infra.read.dtos.CityView;
import com.pug.geo.service.CityReadService;
import com.pug.geo.service.utils.ExceptionHelper;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link CityReadService}.
 *
 * <p>This application-scoped bean delegates read-only operations to the underlying {@link
 * CityQueries} infrastructure component. It handles basic input validation and translates "not
 * found" states into standardized domain exceptions.
 */
@ApplicationScoped
public class CityReadServiceImpl implements CityReadService {

  private static final Logger LOG = Logger.getLogger(CityReadServiceImpl.class);

  @Inject CityQueries queries;

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
  public CityView getViewByIbgeCode(String ibgeCode) {
    if (StringUtils.isEmpty(ibgeCode)) {
      throw ExceptionHelper.cityNotFound();
    }

    return queries
        .findOptionalByIbgeCode(ibgeCode)
        .orElseThrow(
            () -> {
              LOG.debugf("City lookup failed: IBGE Code %s not found", ibgeCode);
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

  /**
   * {@inheritDoc}
   *
   * <p>Prior to execution, the input query is "folded" (lowercased and accents removed via {@link
   * StringUtils#fold(String)}) to ensure maximum compatibility with the underlying search indexing
   * rules.
   */
  @Override
  public List<CityView> search(String q) {
    String key = StringUtils.fold(q);
    return queries.searchByName(key);
  }
}
