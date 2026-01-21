package com.pug.academic.infra;

import com.pug.academic.domain.Course;
import com.pug.academic.infra.persistence.CourseEntity;
import com.pug.shared.exceptions.AppValidationException;

/**
 * Mapper for Course and CourseEntity.
 */
public final class CourseMapper {
  /**
   * Private constructor to prevent instantiation.
   */
  private CourseMapper() {
  }

  /**
   * Maps CourseEntity to Course domain object.
   *
   * @param e the CourseEntity.
   * @return the Course domain object.
   * @throws AppValidationException if the data in the entity is invalid.
   */
  public static Course toDomain(CourseEntity e) throws AppValidationException {
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
    return CourseEntity.builder().id(d.getId()).name(d.getName()).schoolId(d.getSchoolId()).build();
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