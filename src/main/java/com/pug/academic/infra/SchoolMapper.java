package com.pug.academic.infra;

import com.pug.academic.domain.School;
import com.pug.academic.infra.persistence.SchoolEntity;

public final class SchoolMapper {
  private SchoolMapper() {}

  public static School toDomain(SchoolEntity e) {
    if (e == null) return null;
    return School.builder().id(e.getId()).name(e.getName()).build();
  }

  public static SchoolEntity toEntity(School d) {
    if (d == null) return null;
    return SchoolEntity.builder().id(d.getId()).name(d.getName()).build();
  }

  public static void copy(School d, SchoolEntity e) {
    e.setName(d.getName());
  }
}
