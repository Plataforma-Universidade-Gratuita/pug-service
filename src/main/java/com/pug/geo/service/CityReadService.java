package com.pug.geo.service;

import com.pug.geo.domain.enums.GeoErrorCodes;
import com.pug.geo.infra.read.CityQueries;
import com.pug.geo.infra.read.dtos.CityView;
import com.pug.shared.exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CityReadService {

  @Inject CityQueries queries;

  public CityView getView(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(() -> new ResourceNotFoundException(GeoErrorCodes.CITY_NOT_FOUND));
  }

  public CityView getByIbgeCode(String ibgeCode) {
    return queries
        .findOptionalByIbgeCode(ibgeCode)
        .orElseThrow(() -> new ResourceNotFoundException(GeoErrorCodes.CITY_NOT_FOUND));
  }

  public List<CityView> listViews() {
    return queries.listAllCities();
  }

  public List<CityView> search(String q) {
    return queries.searchByName(q);
  }
}
