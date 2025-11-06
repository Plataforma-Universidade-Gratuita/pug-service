package com.pug.geo.infra;

import com.pug.geo.domain.City;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.geo.infra.persistence.CitiesEntity;

/**
 * Mapper class for converting between City domain objects and CitiesEntity persistence entities.
 */
public final class CityMapper {

  private CityMapper() {}

  /**
   * Converts a CitiesEntity to a City domain object.
   *
   * @param e The CitiesEntity to convert.
   * @return The corresponding City domain object.
   */
  public static City toDomain(CitiesEntity e) {
    if (e == null) {
      return null;
    }
    return City.builder()
        .id(e.getId())
        .name(e.getName())
        .ibgeCode(new IbgeCode(e.getIbgeCode()))
        .build();
  }

  /**
   * Converts a City domain object to a CitiesEntity.
   *
   * @param d The City domain object to convert.
   * @return The corresponding CitiesEntity.
   */
  public static CitiesEntity toEntity(City d) {
    if (d == null) {
      return null;
    }
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
