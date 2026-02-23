package com.pug.geo.infra;

import com.pug.geo.domain.City;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.geo.infra.persistence.CityEntity;
import com.pug.geo.infra.read.dtos.CityView;

/** Maps between City domain and CityEntity persistence. */
public final class CityMapper {
  /** Private constructor. */
  private CityMapper() {}

  /**
   * Entity -> Domain.
   *
   * @param e entityId.
   * @return domain object or null if entityId is null.
   */
  public static City toDomain(CityEntity e) {
    if (e == null) {
      return null;
    }
    return City.builder()
        .id(e.getId())
        .name(e.getName())
        .ibgeCode(IbgeCode.factory(e.getIbgeCode()))
        .build();
  }

  /**
   * Domain -> Entity (for persist).
   *
   * @param d domain object.
   * @return entityId or null if domain is null.
   */
  public static CityEntity toEntity(City d) {
    if (d == null) {
      return null;
    }
    return CityEntity.builder()
        .id(d.getId())
        .name(d.getName())
        .ibgeCode(d.getIbgeCode().toString())
        .build();
  }

  /**
   * Copy domain fields into an existing entityId (for update).
   *
   * @param d domain object.
   * @param e entityId to copy into.
   */
  public static void copy(City d, CityEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setName(d.getName());
    e.setIbgeCode(d.getIbgeCode().toString());
  }

  /**
   * Converts a CityEntity to a CityView.
   *
   * @param c the CityEntity
   * @return the CityView
   */
  public static CityView toView(CityEntity c) {
    if (c == null) {
      return null;
    }
    return new CityView(c.getId(), c.getName(), c.getIbgeCode());
  }
}
