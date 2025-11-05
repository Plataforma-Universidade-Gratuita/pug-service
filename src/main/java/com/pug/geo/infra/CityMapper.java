package com.pug.geo.infra;

import com.pug.geo.domain.City;
import com.pug.geo.domain.records.IBGECode;
import com.pug.geo.infra.persistence.CitiesEntity;

public final class CityMapper {

  private CityMapper() {}

  public static City toDomain(CitiesEntity e) {
    if (e == null) return null;
    return City.builder()
        .id(e.getId())
        .name(e.getName())
        .ibgeCode(new IBGECode(e.getIbgeCode()))
        .build();
  }

  public static CitiesEntity toEntity(City d) {
    if (d == null) return null;
    return CitiesEntity.builder().name(d.getName()).ibgeCode(d.getIbgeCode().toString()).build();
  }

  /**
   * Copies the data from domain to entity. <br>
   * Useful for update operations.
   *
   * @param d The domain object from which to copy data.
   * @param e The entity object to which data will be copied.
   */
  public static void copy(City d, CitiesEntity e) {
    e.setName(d.getName());
    e.setIbgeCode(d.getIbgeCode().toString());
  }
}
