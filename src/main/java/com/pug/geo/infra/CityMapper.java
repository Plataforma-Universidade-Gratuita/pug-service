package com.pug.geo.infra;

import com.pug.geo.domain.City;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.geo.infra.persistence.CityEntity;

/** Maps between City domain and CityEntity persistence. */
public final class CityMapper {
  /** Private constructor. */
  private CityMapper() {}

  /**
   * Entity -> Domain (uses domain builder).
   *
   * @param e entity.
   * @return domain object or null if entity is null.
   */
  public static City toDomain(CityEntity e) {
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
   * Domain -> Entity (for persist).
   *
   * @param d domain object.
   * @return entity or null if domain is null.
   */
  public static CityEntity toEntity(City d) {
    if (d == null) {
      return null;
    }
    return CityEntity.builder().name(d.getName()).ibgeCode(d.getIbgeCode().toString()).build();
  }

  /**
   * Copy domain fields into an existing entity (for update).
   *
   * @param d domain object.
   * @param e entity to copy into.
   */
  public static void copy(City d, CityEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setName(d.getName());
    e.setIbgeCode(d.getIbgeCode().toString());
  }
}
