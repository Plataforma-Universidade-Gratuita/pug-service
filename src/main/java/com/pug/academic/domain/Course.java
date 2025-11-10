package com.pug.academic.domain;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.text.StringUtils;
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
   * @param name the name.
   * @param schoolId the school ID.
   * @return the created course.
   */
  public static Course createNew(String name, UUID schoolId) {
    Course c = new Course(null, StringUtils.trim(name), schoolId);
    c.validate();
    return c;
  }

  /**
   * Behavior: change name.
   *
   * @param newName the new name.
   * @return the updated course.
   */
  public Course changeName(String newName) {
    Course c = this.toBuilder().name(StringUtils.trim(newName)).build();
    c.validate();
    return c;
  }

  /**
   * Behavior: move to another school.
   *
   * @param newSchoolId the new school ID.
   * @return the updated course.
   */
  public Course moveToSchool(UUID newSchoolId) {
    Course c = this.toBuilder().schoolId(newSchoolId).build();
    c.validate();
    return c;
  }

  /**
   * Validates the course attributes.
   *
   * @throws AppValidationException if any attribute is invalid.
   */
  private void validate() {
    if (name == null || name.isBlank()) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_COURSE_NAME_BLANK);
    }
    if (name.length() > 120) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_COURSE_NAME_TOOLONG);
    }
    if (schoolId == null) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_SCHOOL);
    }
  }

  /** Builder class for Course. */
  public static class CourseBuilder {
    /**
     * Builds the Course instance after validation.
     *
     * @return the built Course.
     */
    public Course build() {
      Course c = new Course(id, StringUtils.trim(name), schoolId);
      c.validate();
      return c;
    }
  }
}
