package com.pug.academic.infra;

import com.pug.academic.domain.Course;
import com.pug.academic.infra.persistence.CourseEntity;
import com.pug.academic.infra.persistence.SchoolEntity;
import com.pug.academic.infra.read.dtos.CourseView;
import com.pug.academic.infra.read.dtos.SchoolView;
import com.pug.shared.domain.vos.AuditInfo;

/** Mapper for Course and CourseEntity. */
public final class CourseMapper {
  /** Private constructor. */
  private CourseMapper() {}

  /**
   * Convert CourseEntity to Course domain object.
   *
   * @param e the CourseEntity
   * @return the Course domain object
   */
  public static Course toDomain(CourseEntity e) {
    if (e == null) {
      return null;
    }
    return Course.builder()
        .id(e.getId())
        .name(e.getName())
        .schoolId(e.getSchoolId())
        .auditInfo(AuditInfo.factory(e.getCreatedAt(), e.getUpdatedAt()))
        .build();
  }

  /**
   * Convert Course domain object to CourseEntity.
   *
   * @param d the Course domain object
   * @return the CourseEntity
   */
  public static CourseEntity toEntity(Course d) {
    if (d == null) {
      return null;
    }
    return CourseEntity.builder()
        .id(d.getId())
        .name(d.getName())
        .schoolId(d.getSchoolId())
        .createdAt(d.getAuditInfo().getCreatedAt())
        .updatedAt(d.getAuditInfo().getUpdatedAt())
        .build();
  }

  /**
   * Copy properties from Course domain object to CourseEntity.
   *
   * @param d the Course domain object
   * @param e the CourseEntity
   */
  public static void copy(Course d, CourseEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setName(d.getName());
    e.setSchoolId(d.getSchoolId());
  }

  /**
   * Convert CourseEntity and SchoolEntity to CourseView.
   *
   * @param c the CourseEntity
   * @param s the SchoolEntity
   * @return the CourseView
   */
  public static CourseView toView(CourseEntity c, SchoolEntity s) {
    if (c == null) {
      return null;
    }
    SchoolView schoolView =
        (s != null)
            ? new SchoolView(s.getId(), s.getName(), s.getCreatedAt(), s.getUpdatedAt())
            : null;
    return new CourseView(c.getId(), c.getName(), schoolView, c.getCreatedAt(), c.getUpdatedAt());
  }
}
