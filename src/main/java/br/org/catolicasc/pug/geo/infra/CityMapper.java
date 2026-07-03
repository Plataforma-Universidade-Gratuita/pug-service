/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.geo.infra;

import br.org.catolicasc.pug.geo.domain.City;
import br.org.catolicasc.pug.geo.domain.vos.IbgeCode;
import br.org.catolicasc.pug.geo.infra.persistence.CityEntity;
import br.org.catolicasc.pug.geo.infra.read.dtos.CityView;

/**
 * Stateless utility class responsible for mapping between Geographic boundary layers.
 *
 * <p>This mapper acts as an anti-corruption layer, ensuring that the pure Domain model ({@link
 * City}) does not leak into or depend upon the JPA Persistence model ({@link CityEntity}) or the
 * Read/Query model ({@link CityView}).
 */
public final class CityMapper {

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
   * <p>This method converts the domain aggregate, including its Value Objects, into a persistent
   * entity format. It maps the {@link IbgeCode} back into its primitive string representation for
   * database storage.
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
