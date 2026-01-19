package com.pug.academic.domain;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** Course entity aggregate. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Course {
  private final UUID id;
  private final String name;
  private final UUID schoolId;

  /**
   * Factory for new courses.
   *
   * <p>Behavior: create a new course and validate its attributes
   *
   * @param name the name of the course
   * @param schoolId the ID of the school
   * @return the created course
   */
  public static Course createNew(String name, UUID schoolId) {
    Course c = new Course(null, StringUtils.trim(name), schoolId);
    c.validate();
    return c;
  }

  /**
   * Behavior: change the name of the course.
   *
   * @param newName the new name for the course
   * @return the updated course with the new name
   */
  public Course changeName(String newName) {
    Course c = this.toBuilder().name(StringUtils.trim(newName)).build();
    c.validate();
    return c;
  }

  /**
   * Behavior: move the course to another school.
   *
   * @param newSchoolId the ID of the new school
   * @return the updated course with the new school ID
   */
  public Course moveToSchool(UUID newSchoolId) {
    Course c = this.toBuilder().schoolId(newSchoolId).build();
    c.validate();
    return c;
  }

  /**
   * Validates the course aggregate.
   *
   * <p>Checks that the name is not blank and does not exceed 120 characters, and that the school ID
   * is not null.
   *
   * @throws AppValidationException if any attribute is invalid
   */
  private void validate() {
    if (StringUtils.isEmpty(name)) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_COURSE_NAME_BLANK);
    }
    if (name.length() > 120) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_COURSE_NAME_LENGTH);
    }
    if (schoolId == null) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_SCHOOL_BLANK);
    }
  }

  /**
   * Builder class for Course.
   *
   * <p>Overrides the build method to include validation.
   */
  public static class CourseBuilder {
    /**
     * Builds the Course instance after validation.
     *
     * @return the built Course instance
     */
    public Course build() {
      Course c = new Course(id, StringUtils.trim(name), schoolId);
      c.validate();
      return c;
    }
  }
}
