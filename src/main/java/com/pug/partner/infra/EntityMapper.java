package com.pug.partner.infra;

import com.pug.partner.domain.Entity;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.partner.infra.persistence.EntityEntity;
import com.pug.shared.exceptions.AppValidationException;

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
   * Maps an EntityEntity to an Entity domain object.
   *
   * @param e the persistence entity.
   * @return the domain object, or null if entity is null.
   * @throws AppValidationException if the data in the entity (e.g., cnpj) is invalid
   *                                according to domain rules, indicating corrupted data in persistence.
   */
  public static Entity toDomain(EntityEntity e) throws AppValidationException {
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
   * Maps an Entity domain object to an EntityEntity for persistence.
   *
   * @param d the domain object.
   * @return the persistence entity, or null if domain is null.
   */
  public static EntityEntity toEntity(Entity d) {
    if (d == null) {
      return null;
    }
    return EntityEntity.builder()
            .id(d.getId())
            .cnpj(d.getCnpj().toString())
            .name(d.getName())
            .cityId(d.getCityId())
            .address(d.getAddress())
            .build();
  }

  /**
   * Copies domain fields into an existing EntityEntity (for update).
   *
   * @param d the domain object.
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