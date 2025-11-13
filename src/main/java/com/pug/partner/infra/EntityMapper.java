package com.pug.partner.infra;

import com.pug.partner.domain.Entity;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.partner.infra.persistence.EntityEntity;

/**
 * Maps between Entity domain and EntityEntity persistence.
 */
public final class EntityMapper {
  /**
   * Private constructor to prevent instantiation.
   */
  private EntityMapper() {
  }

  /**
   * Persistence -> Domain.
   *
   * @param e the persistence entity.
   * @return the domain entity.
   */
  public static Entity toDomain(EntityEntity e) {
    if (e == null) {
      return null;
    }
    return Entity.builder()
            .id(e.getId())
            .cnpj(new Cnpj(e.getCnpj()))
            .name(e.getName())
            .cityId(e.getCityId())
            .address(e.getAddress())
            .build();
  }

  /**
   * Domain -> Persistence (new).
   *
   * @param d the domain entity.
   * @return the persistence entity.
   */
  public static EntityEntity toEntity(Entity d) {
    if (d == null) {
      return null;
    }
    var e = new EntityEntity();
    e.setId(d.getId());
    copy(d, e);
    return e;
  }

  /**
   * Domain -> Persistence (update).
   *
   * @param d the domain entity.
   * @param e the persistence entity.
   */
  public static void copy(Entity d, EntityEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setCnpj(d.getCnpj().toString());
    e.setName(d.getName());
    e.setAddress(d.getAddress());
    e.setCityId(d.getCityId());
  }
}
