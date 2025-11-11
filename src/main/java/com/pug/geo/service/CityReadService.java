package com.pug.geo.service;

import com.pug.geo.domain.enums.GeoErrorCodes;
import com.pug.geo.infra.read.CityQueries;
import com.pug.geo.infra.read.dtos.CityView;
import com.pug.shared.exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;

/** Service for reading city information. */
@ApplicationScoped
public class CityReadService {

  @Inject CityQueries queries;

  /**
   * Retrieves a CityView by its unique identifier.
   *
   * @param id the UUID of the city
   * @return the CityView corresponding to the given id
   * @throws ResourceNotFoundException if no city is found with the given id
   */
  public CityView getView(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(() -> new ResourceNotFoundException(GeoErrorCodes.CITY_NOT_FOUND));
  }

  /**
   * Retrieves a CityView by its IBGE code.
   *
   * @param ibgeCode the IBGE code of the city
   * @return the CityView corresponding to the given IBGE code
   * @throws ResourceNotFoundException if no city is found with the given IBGE code
   */
  public CityView getViewByIbgeCode(String ibgeCode) {
    return queries
        .findOptionalByIbgeCode(ibgeCode)
        .orElseThrow(() -> new ResourceNotFoundException(GeoErrorCodes.CITY_NOT_FOUND));
  }

  /**
   * Lists all CityView entries.
   *
   * @return a list of all CityView entries
   */
  public List<CityView> listViews() {
    return queries.listAllCities();
  }

  /**
   * Searches for CityView entries by name.
   *
   * @param q the search query string
   * @return a list of CityView entries matching the search query
   */
  public List<CityView> search(String q) {
    return queries.searchByName(q);
  }
}
