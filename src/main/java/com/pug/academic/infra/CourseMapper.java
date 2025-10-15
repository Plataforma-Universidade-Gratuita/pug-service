package com.pug.academic.infra;

import com.pug.academic.domain.Course;
import com.pug.academic.infra.persistence.CourseEntity;
import com.pug.academic.infra.persistence.SchoolEntity;

public final class CourseMapper {
  private CourseMapper() {}

  public static Course toDomain(CourseEntity e) {
    if (e == null) return null;
    return Course.builder().id(e.getId()).name(e.getName()).schoolId(e.getSchool().getId()).build();
  }

  public static CourseEntity toEntity(Course d, SchoolEntity school) {
    if (d == null) return null;
    return CourseEntity.builder().id(d.getId()).name(d.getName()).school(school).build();
  }

  public static void copy(Course d, CourseEntity e, SchoolEntity school) {
    e.setName(d.getName());
    e.setSchool(school);
  }
}
