package com.pug.academic.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/** Course entity aggregate. */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class Course extends DomainError {
  UUID id;
  String name;
  UUID schoolId;

  @Builder(toBuilder = true)
  private Course(UUID id, String name, UUID schoolId) {
    this.id = id;
    this.name = name;
    this.schoolId = schoolId;
  }

  /**
   * Factory for new courses.
   *
   * @param name the name of the course
   * @param schoolId the ID of the school
   * @return the created course (may contain errors)
   */
  public static Course factory(String name, UUID schoolId) {
    String trimmedName = StringUtils.trim(name);
    Course course =
        Course.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .name(trimmedName)
            .schoolId(schoolId)
            .build();

    course.collectValidationProblems();
    return course;
  }

  /**
   * Behavior: change the name of the course.
   *
   * @param newName the new name for the course
   * @return the updated course with the new name
   */
  public Course changeName(String newName) {
    String trimmedName = StringUtils.trim(newName);
    if (this.name.equals(trimmedName)) {
      return this;
    }
    Course updatedCourse = this.toBuilder().name(trimmedName).build();
    updatedCourse.collectValidationProblems();
    return updatedCourse;
  }

  /**
   * Behavior: move the course to another school.
   *
   * @param newSchoolId the ID of the new school
   * @return the updated course with the new school ID
   */
  public Course moveToSchool(UUID newSchoolId) {
    if (this.schoolId.equals(newSchoolId)) {
      return this;
    }
    Course updatedCourse = this.toBuilder().schoolId(newSchoolId).build();
    updatedCourse.collectValidationProblems();
    return updatedCourse;
  }

  /** Collects all validation problems for the Course instance. */
  private void collectValidationProblems() {
    if (id == null) {
      addError(new AppValidationException.Problem(AcademicErrorCodes.INVALID_ID_BLANK));
    }

    if (StringUtils.isEmpty(name)) {
      addError(new AppValidationException.Problem(AcademicErrorCodes.INVALID_COURSE_NAME_BLANK));
    } else if (name.length() > 120) {
      addError(new AppValidationException.Problem(AcademicErrorCodes.INVALID_COURSE_NAME_LENGTH));
    }

    if (schoolId == null) {
      addError(new AppValidationException.Problem(AcademicErrorCodes.INVALID_SCHOOL_BLANK));
    }
  }
}
