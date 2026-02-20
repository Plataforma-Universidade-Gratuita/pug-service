package com.pug.geo.service.impl;

import com.pug.geo.domain.enums.GeoErrorCodes;
import com.pug.geo.infra.read.CityQueries;
import com.pug.geo.infra.read.dtos.CityView;
import com.pug.geo.service.CityReadService;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

/**
 * Service for reading city information.
 */
@ApplicationScoped
public class CityReadServiceImpl implements CityReadService {

  private static final Logger LOG = Logger.getLogger(CityReadServiceImpl.class);

  @Inject
  CityQueries queries;

  @Override
  public CityView getViewById(UUID id) {
    return queries
            .findOptionalById(id)
            .orElseThrow(() -> {
              LOG.debugf("City lookup failed: ID %s not found", id);
              return new ResourceNotFoundException(
                      GeoErrorCodes.CITY_NOT_FOUND,
                      "id",
                      id.toString()
              );
            });
  }

  @Override
  public CityView getViewByIbgeCode(String ibgeCode) {
    if (StringUtils.isEmpty(ibgeCode)) {
      throw new ResourceNotFoundException(
              GeoErrorCodes.CITY_NOT_FOUND,
              "ibgeCode",
              "empty"
      );
    }

    return queries
            .findOptionalByIbgeCode(ibgeCode)
            .orElseThrow(() -> {
              LOG.debugf("City lookup failed: IBGE Code %s not found", ibgeCode);
              return new ResourceNotFoundException(
                      GeoErrorCodes.CITY_NOT_FOUND,
                      "ibgeCode",
                      ibgeCode
              );
            });
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