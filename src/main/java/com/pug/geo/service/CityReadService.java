package com.pug.geo.service;

import com.pug.geo.infra.read.dtos.CityView;
import com.pug.shared.exceptions.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;

/** Interface for reading City data. */
public interface CityReadService {

  /**
   * Retrieves a CityView by its unique identifier.
   *
   * @param id the UUID of the city
   * @return the CityView corresponding to the given id
   * @throws ResourceNotFoundException if no city is found with the given id
   */
  CityView getViewById(UUID id);

  /**
   * Retrieves a CityView by its IBGE code.
   *
   * @param ibgeCode the IBGE code of the city
   * @return the CityView corresponding to the given IBGE code
   * @throws ResourceNotFoundException if no city is found with the given IBGE code
   */
  CityView getViewByIbgeCode(String ibgeCode);

  /**
   * Lists all CityView entries.
   *
   * @return a list of all CityView entries
   */
  List<CityView> listViews();

  /**
   * Searches for CityView entries by name.
   *
   * @param q the search query string
   * @return a list of CityView entries matching the search query
   */
  List<CityView> search(String q);
}
