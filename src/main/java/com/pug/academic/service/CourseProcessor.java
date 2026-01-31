package com.pug.academic.service;

import com.pug.academic.domain.Course;
import com.pug.shared.utils.StringUtils;
import java.util.UUID;

public class CourseProcessor {

  /**
   * Helper method to process DTO input and build a new Course domain object.
   *
   * @param name The name from DTO.
   * @param schoolId The school ID from DTO (already validated for existence).
   * @return The constructed Course domain object (may contain errors).
   */
  public static Course processCreateInput(String name, UUID schoolId) {
    return Course.factory(name, schoolId);
  }

  /**
   * Helper method to process DTO input and update an existing Course domain object.
   *
   * @param existingCourse The existing course to be updated.
   * @param name The name from DTO (can be null for no change).
   * @param schoolId The school ID from DTO (can be null for no change).
   * @return The updated Course domain object (may contain errors).
   */
  public static Course processUpdateInput(Course existingCourse, String name, UUID schoolId) {
    Course updatedCourse = existingCourse;

    if (!StringUtils.isEmpty(name)) {
      updatedCourse = updatedCourse.changeName(name);
    }

    if (schoolId != null) {
      updatedCourse = updatedCourse.moveToSchool(schoolId);
    }

    return updatedCourse;
  }
}
