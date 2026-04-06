package com.pug.partner.infra;

import com.pug.partner.domain.Entity;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.partner.infra.persistence.EntityEntity;
import com.pug.shared.domain.vos.AuditInfo;

/**
 * Stateless utility class responsible for mapping between Partner Entity boundary layers.
 *
 * <p>This mapper acts as an anti-corruption layer, ensuring that the pure Domain model ({@link
 * Entity}) does not leak into or depend upon the JPA Persistence model ({@link EntityEntity}).
 */
public final class EntityMapper {
  /** Private constructor to prevent instantiation. */
  private EntityMapper() {}

  /**
   * Reconstitutes a pure Domain {@link Entity} aggregate from a JPA {@link EntityEntity}.
   *
   * <p>This method translates primitive database columns back into their corresponding Domain Value
   * Objects (e.g., using {@link Cnpj#factory(String)} and {@link AuditInfo}).
   *
   * @param e the JPA persistence entity to convert
   * @return a fully constructed Domain {@link Entity}, or {@code null} if the input entity is null
   */
  public static Entity toDomain(EntityEntity e) {
    if (e == null) {
      return null;
    }
    return Entity.builder()
        .id(e.getId())
        .cnpj(Cnpj.factory(e.getCnpj()))
        .name(e.getName())
        .cityId(e.getCityId())
        .address(e.getAddress())
        .auditInfo(AuditInfo.factory(e.getCreatedAt(), e.getUpdatedAt()))
        .build();
  }

  /**
   * Translates a pure Domain {@link Entity} aggregate into a newly instantiated JPA {@link
   * EntityEntity}.
   *
   * <p>This is typically used when persisting a brand-new entity to the database. It flattens
   * Domain Value Objects back into primitive types suitable for JDBC insertion.
   *
   * @param d the Domain aggregate to convert
   * @return a newly constructed JPA {@link EntityEntity}, or {@code null} if the input domain is
   *     null
   */
  public static EntityEntity toEntity(Entity d) {
    if (d == null) {
      return null;
    }
    return EntityEntity.builder()
        .id(d.getId())
        .cnpj(d.getCnpj().getValue())
        .name(d.getName())
        .cityId(d.getCityId())
        .address(d.getAddress())
        .createdAt(d.getAuditInfo().getCreatedAt())
        .updatedAt(d.getAuditInfo().getUpdatedAt())
        .build();
  }

  /**
   * Updates an existing, attached JPA {@link EntityEntity} with the current state of a Domain
   * {@link Entity}.
   *
   * <p>This method is used during update operations. Modifying the attached entity allows the ORM
   * (Hibernate) to track changes and issue the appropriate SQL {@code UPDATE} statements upon
   * transaction commit. Primary keys and immutable audit fields are intentionally excluded.
   *
   * @param d the Domain aggregate containing the updated state
   * @param e the existing, attached JPA entity to update in-place
   */
  public static void copy(Entity d, EntityEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setCnpj(d.getCnpj().getValue());
    e.setName(d.getName());
    e.setAddress(d.getAddress());
    e.setCityId(d.getCityId());
    e.setCreatedAt(d.getAuditInfo().getCreatedAt());
    e.setUpdatedAt(d.getAuditInfo().getUpdatedAt());
  }
}
