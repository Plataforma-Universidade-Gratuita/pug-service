package com.pug.partner.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.helpers.domainGenerators.CityGenerator;
import com.pug.helpers.domainGenerators.EntityGenerator;
import com.pug.helpers.entityGenerators.CitiesEntityGenerator;
import com.pug.partner.domain.Entity;
import com.pug.partner.infra.persistence.EntitiesEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EntityMapperTest {

  private final EntityGenerator entityGen = new EntityGenerator();
  private final CitiesEntityGenerator cityGen = new CitiesEntityGenerator();

  @Test
  @DisplayName("toEntity maps all simple fields and city id")
  void toEntity_basic() {
    Entity d = entityGen.createRandomPersistedEntity();
    EntitiesEntity e = EntityMapper.toEntity(d);

    assertNotNull(e);
    assertEquals(d.getId(), e.getId());
    assertEquals(d.getCnpj().toString(), e.getCnpj());
    assertEquals(d.getName(), e.getName());
    assertEquals(d.getAddress(), e.getAddress());
    assertNotNull(e.getCity());
    assertEquals(d.getCity().getId(), e.getCity().getId());
  }

  @Test
  @DisplayName("copy updates target entity; city with null id clears relation")
  void copy_updates_and_clears_city() {
    Entity src = entityGen.createRandomPersistedEntity();

    var tgt = new EntitiesEntity();
    tgt.setId(src.getId());
    tgt.setCnpj("00000000000000");
    tgt.setName("OLD");
    tgt.setAddress("OLD-ADDR");

    EntityMapper.copy(src, tgt);
    assertEquals(src.getCnpj().toString(), tgt.getCnpj());
    assertEquals(src.getName(), tgt.getName());
    assertEquals(src.getAddress(), tgt.getAddress());
    assertNotNull(tgt.getCity());
    assertEquals(src.getCity().getId(), tgt.getCity().getId());

    var cityGen = new CityGenerator();
    var validCityNoId = cityGen.randomCity();
    var srcCityNoId = src.toBuilder().city(validCityNoId).build();

    EntityMapper.copy(srcCityNoId, tgt);
    assertNull(tgt.getCity());
  }

  @Test
  @DisplayName("toDomain maps with full CitiesEntity relation present")
  void toDomain_withCityRelation() {
    var id = UuidCreator.getTimeOrderedEpoch();
    Entity d = entityGen.createRandomPersistedEntity();

    var city = cityGen.createRandomCitiesEntity();
    city.setId(UuidCreator.getTimeOrderedEpoch());

    var e = new EntitiesEntity();
    e.setId(id);
    e.setCnpj(d.getCnpj().toString());
    e.setName(d.getName());
    e.setAddress(d.getAddress());
    e.setCity(city);
    e.setCityId(city.getId());

    Entity mapped = EntityMapper.toDomain(e);

    assertNotNull(mapped);
    assertEquals(id, mapped.getId());
    assertEquals(d.getCnpj(), mapped.getCnpj());
    assertEquals(d.getName(), mapped.getName());
    assertEquals(d.getAddress(), mapped.getAddress());
    assertNotNull(mapped.getCity());
    assertEquals(city.getId(), mapped.getCity().getId());
  }

  @Test
  @DisplayName("toDomain returns null on null input")
  void toDomain_null() {
    assertNull(EntityMapper.toDomain(null));
  }

  @Test
  @DisplayName("toEntity returns null on null input")
  void toEntity_null() {
    assertNull(EntityMapper.toEntity(null));
  }
}
