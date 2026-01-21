package com.pug.academic.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Course entity aggregate.
 */
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
   * @param name     the name of the course
   * @param schoolId the ID of the school
   * @return the created course
   * @throws AppValidationException if validation fails
   */
  public static Course createNew(String name, UUID schoolId) {
    String trimmedName = StringUtils.trim(name);
    Course course =
            Course.builder()
                    .id(UuidCreator.getTimeOrderedEpoch())
                    .name(trimmedName)
                    .schoolId(schoolId)
                    .build();

    List<AppValidationException.Problem> problems = course.collectValidationProblems();
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return course;
  }

  /**
   * Behavior: change the name of the course.
   *
   * @param newName the new name for the course
   * @return the updated course with the new name
   * @throws AppValidationException if validation fails
   */
  public Course changeName(String newName) {
    String trimmedName = StringUtils.trim(newName);
    Course updatedCourse = this.toBuilder().name(trimmedName).build();

    List<AppValidationException.Problem> problems = updatedCourse.collectValidationProblems();
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return updatedCourse;
  }

  /**
   * Behavior: move the course to another school.
   *
   * @param newSchoolId the ID of the new school
   * @return the updated course with the new school ID
   * @throws AppValidationException if validation fails
   */
  public Course moveToSchool(UUID newSchoolId) {
    Course updatedCourse = this.toBuilder().schoolId(newSchoolId).build();

    List<AppValidationException.Problem> problems = updatedCourse.collectValidationProblems();
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return updatedCourse;
  }

  /**
   * Collects all validation problems for the Course instance.
   *
   * @return A list of {@code AppValidationException.Problem}; an empty list otherwise.
   */
  private List<AppValidationException.Problem> collectValidationProblems() {
    List<AppValidationException.Problem> problems = new ArrayList<>();

    if (id == null) {
      problems.add(new AppValidationException.Problem(AcademicErrorCodes.INVALID_ID_BLANK, "id"));
    }
    if (StringUtils.isEmpty(name)) {
      problems.add(
              new AppValidationException.Problem(AcademicErrorCodes.INVALID_COURSE_NAME_BLANK, "name"));
    } else if (name.length() > 120) {
      problems.add(
              new AppValidationException.Problem(
                      AcademicErrorCodes.INVALID_COURSE_NAME_LENGTH, "name"));
    }
    if (schoolId == null) {
      problems.add(
              new AppValidationException.Problem(AcademicErrorCodes.INVALID_SCHOOL_BLANK, "schoolId"));
    }

    return problems;
  }
}