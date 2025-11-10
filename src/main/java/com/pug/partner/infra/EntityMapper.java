package com.pug.partner.infra;

import com.pug.geo.domain.City;
import com.pug.geo.infra.CityMapper;
import com.pug.geo.infra.persistence.CityEntity;
import com.pug.partner.domain.Entity;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.partner.infra.persistence.EntitiesEntity;

/** Maps between Entity domain and EntitiesEntity persistence. */
public final class EntityMapper {
  /** Private constructor to prevent instantiation. */
  private EntityMapper() {}

  /**
   * Converts an EntitiesEntity to an Entity domain object.
   *
   * @param e the EntitiesEntity.
   * @return the corresponding Entity domain object.
   */
  public static Entity toDomain(EntitiesEntity e) {
    if (e == null) {
      return null;
    }
    City city =
        (e.getCity() != null)
            ? CityMapper.toDomain(e.getCity())
            : (e.getCityId() != null) ? City.builder().id(e.getCityId()).build() : null;

    return Entity.builder()
        .id(e.getId())
        .cnpj(new Cnpj(e.getCnpj()))
        .name(e.getName())
        .city(city)
        .address(e.getAddress())
        .build();
  }

  /**
   * Converts an Entity domain object to an EntitiesEntity for persistence.
   *
   * @param d the Entity domain object.
   * @return the corresponding EntitiesEntity.
   */
  public static EntitiesEntity toEntity(Entity d) {
    if (d == null) {
      return null;
    }
    var e = new EntitiesEntity();
    e.setId(d.getId());
    copy(d, e);
    return e;
  }

  /**
   * Copies the data from domain to entity. <br>
   * Useful for update operations.
   *
   * @param d The domain object from which to copy data.
   * @param e The entity object to which data will be copied.
   */
  public static void copy(Entity d, EntitiesEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setCnpj(d.getCnpj().toString());
    e.setName(d.getName());
    e.setAddress(d.getAddress());

    if (d.getCity() != null && d.getCity().getId() != null) {
      var cityRef = new CityEntity();
      cityRef.setId(d.getCity().getId());
      e.setCity(cityRef);
    } else {
      e.setCity(null);
    }
  }
}
