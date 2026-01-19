package com.pug.academic.infra;

import com.pug.academic.domain.Course;
import com.pug.academic.infra.persistence.CourseEntity;

/** Mapper for Course and CourseEntity. */
public final class CourseMapper {
  /** Private constructor to prevent instantiation. */
  private CourseMapper() {}

  /**
   * Maps CourseEntity to Course domain object.
   *
   * @param e the CourseEntity.
   * @return the Course domain object.
   */
  public static Course toDomain(CourseEntity e) {
    if (e == null) {
      return null;
    }
    return Course.builder().id(e.getId()).name(e.getName()).schoolId(e.getSchoolId()).build();
  }

  /**
   * Maps Course domain object to CourseEntity.
   *
   * @param d the Course domain object.
   * @return the CourseEntity.
   */
  public static CourseEntity toEntity(Course d) {
    if (d == null) {
      return null;
    }
    var e = new CourseEntity();
    e.setId(d.getId());
    copy(d, e);
    return e;
  }

  /**
   * Copies attributes from Course domain object to CourseEntity.
   *
   * @param d the Course domain object.
   * @param e the CourseEntity.
   */
  public static void copy(Course d, CourseEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setName(d.getName());
    e.setSchoolId(d.getSchoolId());
  }
}
