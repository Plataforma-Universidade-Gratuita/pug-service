package com.pug.geo.presenter.dtos;

import com.pug.geo.domain.City;
import java.util.UUID;

/**
 * Response DTO for a city.
 *
 * @param id the unique identifier of the city
 * @param name the name of the city
 * @param ibgeCode the IBGE code of the city
 */
public record CityResponse(UUID id, String name, String ibgeCode) {
  /**
   * Factory method to create a CityResponse from a City domain object.
   *
   * @param c the City domain object.
   * @return the CityResponse DTO.
   */
  public static CityResponse from(City c) {
    return new CityResponse(c.getId(), c.getName(), c.getIbgeCode().toString());
  }
}
