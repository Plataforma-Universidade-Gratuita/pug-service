package com.pug.geo.presenter.rest.dto;

import com.pug.geo.domain.City;

public record CityResponse(String id, String name, String ibgeCode) {
  public static CityResponse from(City c) {
    return new CityResponse(c.getId().toString(), c.getName(), c.getIbgeCode());
  }
}
