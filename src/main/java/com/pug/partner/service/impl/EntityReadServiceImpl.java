package com.pug.partner.service.impl;

import com.pug.geo.infra.read.dtos.CityView;
import com.pug.geo.service.CityReadService;
import com.pug.partner.infra.read.EntityQueries;
import com.pug.partner.infra.read.dtos.EntityView;
import com.pug.partner.service.EntityReadService;
import com.pug.partner.service.utils.ExceptionHelper;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link EntityReadService}.
 *
 * <p>This application-scoped bean delegates read-only operations to the underlying {@link
 * EntityQueries} infrastructure component. It handles basic input validation and translates "not
 * found" states into standardized domain exceptions.
 */
@ApplicationScoped
public class EntityReadServiceImpl implements EntityReadService {

  private static final Logger LOG = Logger.getLogger(EntityReadServiceImpl.class);

  @Inject EntityQueries queries;

  @Inject CityReadService cityReadService;

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
  public EntityView getViewByCnpj(String cnpj) {
    if (StringUtils.isEmpty(cnpj)) {
      throw ExceptionHelper.entityNotFound();
    }

    return queries
        .findOptionalByCnpj(cnpj)
        .orElseThrow(
            () -> {
              LOG.debugf("Entity lookup failed: CNPJ %s not found", cnpj);
              return ExceptionHelper.entityNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public List<CityView> listCityViews() {
    List<UUID> usedCityIds = queries.listAllCityIds();
    return cityReadService.listViewsByIds(usedCityIds);
  }

  /** {@inheritDoc} */
  @Override
  public List<EntityView> listViews() {
    return queries.listAllEntities();
  }

  /** {@inheritDoc} */
  @Override
  public List<EntityView> listViewsByCityId(UUID cityId) {
    if (cityId == null) {
      return List.of();
    }
    return queries.listAllByCityId(cityId);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Prior to execution, the input query is "folded" (lowercased and accents removed via {@link
   * StringUtils#fold(String)}) to ensure maximum compatibility with the underlying search indexing
   * rules.
   */
  @Override
  public List<EntityView> searchViews(String query) {
    String key = StringUtils.fold(query);
    return queries.searchByName(key);
  }
}
