package com.pug.academic.infra;

import com.pug.academic.domain.School;
import com.pug.academic.infra.persistence.SchoolEntity;
import com.pug.academic.infra.read.dtos.SchoolView;

/**
 * Mapper for School and SchoolEntity.
 */
public final class SchoolMapper {
  /**
   * Private constructor to prevent instantiation.
   */
  private SchoolMapper() {
  }

  /**
   * Convert SchoolEntity to School domain object.
   *
   * @param e the SchoolEntity
   * @return the School domain object
   */
  public static School toDomain(SchoolEntity e) {
    if (e == null) {
      return null;
    }
    return School.builder().id(e.getId()).name(e.getName()).build();
  }

  /**
   * Convert School domain object to SchoolEntity.
   *
   * @param d the School domain object
   * @return the SchoolEntity
   */
  public static SchoolEntity toEntity(School d) {
    if (d == null) {
      return null;
    }
    return SchoolEntity.builder().id(d.getId()).name(d.getName()).build();
  }

  /**
   * Copy properties from School domain object to SchoolEntity.
   *
   * @param d the School domain object
   * @param e the SchoolEntity
   */
  public static void copy(School d, SchoolEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setName(d.getName());
  }

  /**
   * Convert SchoolEntity to SchoolView DTO.
   *
   * @param s the SchoolEntity
   * @return the SchoolView DTO
   */
  public static SchoolView toView(SchoolEntity s) {
    if (s == null) {
      return null;
    }
    return new SchoolView(s.getId(), s.getName());
  }
}