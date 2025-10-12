package com.pug.geo.infra;

import com.pug.geo.domain.City;
import com.pug.geo.infra.persistence.CityEntity;

public final class CityMapper {
  private CityMapper() {}

  public static City toDomain(CityEntity e) {
    if (e == null) return null;
    return City.builder().id(e.getId()).name(e.getName()).ibgeCode(e.getIbgeCode()).build();
  }

  public static CityEntity toEntity(City d) {
    if (d == null) return null;
    return CityEntity.builder().id(d.getId()).name(d.getName()).ibgeCode(d.getIbgeCode()).build();
  }

  public static void copy(City d, CityEntity e) {
    e.setName(d.getName());
    e.setIbgeCode(d.getIbgeCode());
  }
}
