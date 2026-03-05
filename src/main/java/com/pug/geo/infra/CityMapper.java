package com.pug.geo.infra;

import com.pug.geo.domain.City;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.geo.infra.persistence.CityEntity;
import com.pug.geo.infra.read.dtos.CityView;

/**
 * Stateless utility class responsible for mapping between Geographic boundary layers.
 *
 * <p>This mapper acts as an anti-corruption layer, ensuring that the pure Domain model ({@link
 * City}) does not leak into or depend upon the JPA Persistence model ({@link CityEntity}) or the
 * Read/Query model ({@link CityView}).
 */
public final class CityMapper {

  /** Private constructor to prevent instantiation of utility class. */
  private CityMapper() {}

  /**
   * Reconstitutes a pure Domain {@link City} aggregate from a JPA {@link CityEntity}.
   *
   * <p>This method translates primitive database columns back into their corresponding Domain Value
   * Objects (e.g., using {@link IbgeCode#factory(String)}).
   *
   * @param e the JPA persistence entity to convert
   * @return a fully constructed Domain {@link City}, or {@code null} if the input entity is null
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
   * Translates a pure Domain {@link City} aggregate into a newly instantiated JPA {@link
   * CityEntity}.
   *
   * <p>This is typically used when persisting a brand-new entity to the database. It flattens
   * Domain Value Objects back into primitive types suitable for JDBC insertion.
   *
   * @param d the Domain aggregate to convert
   * @return a newly constructed JPA {@link CityEntity}, or {@code null} if the input domain is null
   */
  public static CityEntity toEntity(City d) {
    if (d == null) {
      return null;
    }
    return CityEntity.builder()
        .id(d.getId())
        .name(d.getName())
        .ibgeCode(d.getIbgeCode().getCode())
        .build();
  }

  /**
   * Updates an existing, attached JPA {@link CityEntity} with the current state of a Domain {@link
   * City}.
   *
   * <p>This method is used during update operations. Modifying the attached entity allows the ORM
   * (Hibernate) to track changes and issue the appropriate SQL {@code UPDATE} statements upon
   * transaction commit. The primary key (ID) is intentionally excluded from the copy.
   *
   * @param d the Domain aggregate containing the updated state
   * @param e the existing, attached JPA entity to update in-place
   */
  public static void copy(City d, CityEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setName(d.getName());
    e.setIbgeCode(d.getIbgeCode().getCode());
  }

  /**
   * Projects a JPA {@link CityEntity} into a lightweight, read-only {@link CityView} DTO.
   *
   * <p>Used heavily by the query/read layer to provide flattened data structures ready for JSON
   * serialization in API responses.
   *
   * @param c the JPA persistence entity to project
   * @return a flattened {@link CityView} DTO, or {@code null} if the input entity is null
   */
  public static CityView toView(CityEntity c) {
    if (c == null) {
      return null;
    }
    return new CityView(c.getId(), c.getName(), c.getIbgeCode());
  }
}
