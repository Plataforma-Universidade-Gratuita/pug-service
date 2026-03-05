package com.pug.academic.infra;

import com.pug.academic.domain.School;
import com.pug.academic.infra.persistence.SchoolEntity;
import com.pug.academic.infra.read.dtos.SchoolView;
import com.pug.shared.domain.vos.AuditInfo;

/**
 * Stateless utility class responsible for mapping between School boundary layers.
 *
 * <p>This mapper acts as an anti-corruption layer, ensuring that the pure Domain model ({@link
 * School}) does not leak into or depend upon the JPA Persistence model ({@link SchoolEntity}).
 */
public final class SchoolMapper {
  /** Private constructor to prevent instantiation. */
  private SchoolMapper() {}

  /**
   * Reconstitutes a pure Domain {@link School} aggregate from a JPA {@link SchoolEntity}.
   *
   * @param e the JPA persistence entity to convert
   * @return a fully constructed Domain {@link School}, or {@code null} if the input entity is null
   */
  public static School toDomain(SchoolEntity e) {
    if (e == null) {
      return null;
    }
    return School.builder()
        .id(e.getId())
        .name(e.getName())
        .auditInfo(AuditInfo.factory(e.getCreatedAt(), e.getUpdatedAt()))
        .build();
  }

  /**
   * Translates a pure Domain {@link School} aggregate into a newly instantiated JPA {@link
   * SchoolEntity}.
   *
   * @param d the Domain aggregate to convert
   * @return a newly constructed JPA {@link SchoolEntity}, or {@code null} if the input domain is
   *     null
   */
  public static SchoolEntity toEntity(School d) {
    if (d == null) {
      return null;
    }
    return SchoolEntity.builder()
        .id(d.getId())
        .name(d.getName())
        .createdAt(d.getAuditInfo().getCreatedAt())
        .updatedAt(d.getAuditInfo().getUpdatedAt())
        .build();
  }

  /**
   * Updates an existing, attached JPA {@link SchoolEntity} with the current state of a Domain
   * {@link School}.
   *
   * @param d the Domain aggregate containing the updated state
   * @param e the existing, attached JPA entity to update in-place
   */
  public static void copy(School d, SchoolEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setName(d.getName());
  }

  /**
   * Projects a JPA {@link SchoolEntity} into a lightweight, read-only {@link SchoolView} DTO.
   *
   * @param s the JPA persistence entity to project
   * @return a flattened {@link SchoolView} DTO, or {@code null} if the input entity is null
   */
  public static SchoolView toView(SchoolEntity s) {
    if (s == null) {
      return null;
    }
    return new SchoolView(s.getId(), s.getName(), s.getCreatedAt(), s.getUpdatedAt());
  }
}
