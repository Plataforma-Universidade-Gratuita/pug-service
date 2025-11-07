package com.pug.geo.presenter.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pug.geo.domain.City;
import com.pug.helpers.domainGenerators.CityGenerator;
import org.junit.jupiter.api.Test;

class CityResponseTest {

  private final CityGenerator gen = new CityGenerator();

  @Test
  void from_maps_all_fields() {
    City domain = gen.randomCityWithId();
    CityResponse dto = CityResponse.from(domain);

    assertEquals(domain.getId(), dto.id());
    assertEquals(domain.getName(), dto.name());
    assertEquals(domain.getIbgeCode().toString(), dto.ibgeCode());
  }
}
