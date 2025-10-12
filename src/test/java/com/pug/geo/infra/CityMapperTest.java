package com.pug.geo.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.pug.geo.domain.City;
import com.pug.geo.infra.persistence.CityEntity;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CityMapperTest {

  @Test
  void toDomainAndToEntityRoundTrip() {
    var id = UUID.randomUUID();
    var e = CityEntity.builder().id(id).name("Florianópolis").ibgeCode("4205407").build();

    var d = CityMapper.toDomain(e);
    assertNotNull(d);
    assertEquals(id, d.getId());
    assertEquals("Florianópolis", d.getName());
    assertEquals("4205407", d.getIbgeCode());

    var back = CityMapper.toEntity(d);
    assertNotNull(back);
    assertEquals(id, back.getId());
    assertEquals("Florianópolis", back.getName());
    assertEquals("4205407", back.getIbgeCode());
  }

  @Test
  void copyWritesIntoExistingEntity() {
    var src = City.builder().id(UUID.randomUUID()).name("São José").ibgeCode("4216602").build();

    var tgt = CityEntity.builder().id(src.getId()).name("Old").ibgeCode("1234567").build();

    CityMapper.copy(src, tgt);

    assertEquals("São José", tgt.getName());
    assertEquals("4216602", tgt.getIbgeCode());
  }

  @Test
  void nullSafe() {
    assertNull(CityMapper.toDomain(null));
    assertNull(CityMapper.toEntity(null));
  }
}
