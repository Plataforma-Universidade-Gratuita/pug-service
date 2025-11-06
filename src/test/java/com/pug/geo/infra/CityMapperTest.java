package com.pug.geo.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.pug.geo.domain.City;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.geo.infra.persistence.CitiesEntity;
import com.pug.helpers.entityGenerators.CitiesEntityGenerator;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class CityMapperTest {

  private final CitiesEntityGenerator generator = new CitiesEntityGenerator();

  @Test
  public void testToDomain_withCitiesEntity() {
    CitiesEntity entity = generator.createRandomCitiesEntity();

    City city = CityMapper.toDomain(entity);

    assertEquals(entity.getName(), city.getName());
    assertEquals(new IbgeCode(entity.getIbgeCode()), city.getIbgeCode());
    assertEquals(entity.getId(), city.getId());
  }

  @Test
  public void testToEntity_withCity() {
    City city = City.builder().name("São Paulo").ibgeCode(new IbgeCode("3550308")).build();

    CitiesEntity entity = CityMapper.toEntity(city);

    assertNull(entity.getId());
    assertEquals(city.getName(), entity.getName());
    assertEquals(city.getIbgeCode().toString(), entity.getIbgeCode());
  }

  @Test
  public void testCopy_withCityAndEntity() {
    City city = City.builder().name("São Paulo").ibgeCode(new IbgeCode("3550308")).build();
    CitiesEntity entity = new CitiesEntity();
    entity.setId(UUID.randomUUID());

    CityMapper.copy(city, entity);

    assertEquals(city.getName(), entity.getName());
    assertEquals(city.getIbgeCode().toString(), entity.getIbgeCode());
  }
}
