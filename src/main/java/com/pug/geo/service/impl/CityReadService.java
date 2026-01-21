package com.pug.geo.service.impl;

import com.pug.geo.domain.enums.GeoErrorCodes;
import com.pug.geo.infra.read.ICityQueries;
import com.pug.geo.infra.read.dtos.CityView;
import com.pug.geo.service.ICityReadService;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for reading city information.
 */
@ApplicationScoped
public class CityReadService implements ICityReadService {

  @Inject
  ICityQueries queries;

  @Override
  public CityView getViewById(UUID id) {
    return queries
            .findOptionalById(id)
            .orElseThrow(
                    () -> new ResourceNotFoundException(GeoErrorCodes.CITY_NOT_FOUND, Map.of("id", id)));
  }

  @Override
  public CityView getViewByIbgeCode(String ibgeCode) {
    if (StringUtils.isEmpty(ibgeCode)) {
      throw new ResourceNotFoundException(GeoErrorCodes.CITY_NOT_FOUND, Map.of("ibgeCode", ibgeCode));
    }
    return queries
            .findOptionalByIbgeCode(ibgeCode)
            .orElseThrow(
                    () ->
                            new ResourceNotFoundException(
                                    GeoErrorCodes.CITY_NOT_FOUND, Map.of("ibgeCode", ibgeCode)));
  }

  @Override
  public List<CityView> listViews() {
    return queries.listAllCities();
  }

  @Override
  public List<CityView> search(String q) {
    String key = StringUtils.fold(q);
    return queries.searchByName(key);
  }
}