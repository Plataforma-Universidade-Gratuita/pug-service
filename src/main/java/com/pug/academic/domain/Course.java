package com.pug.academic.domain;

import static com.pug.academic.domain.AcademicErrorCodes.ACADEMIC_COURSE_NAME_REQUIRED;
import static com.pug.academic.domain.AcademicErrorCodes.ACADEMIC_COURSE_NAME_TOO_LONG;
import static com.pug.academic.domain.AcademicErrorCodes.ACADEMIC_COURSE_SCHOOL_REQUIRED;

import com.pug.shared.domain.exceptions.AppValidationException;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public final class Course {
  private final UUID id;
  private final String name;
  private final UUID schoolId;

  private void validate() {
    if (schoolId == null) throw new AppValidationException(ACADEMIC_COURSE_SCHOOL_REQUIRED);
    if (name == null || name.isBlank())
      throw new AppValidationException(ACADEMIC_COURSE_NAME_REQUIRED);
    if (name.length() > 120) throw new AppValidationException(ACADEMIC_COURSE_NAME_TOO_LONG);
  }

  public static class CourseBuilder {
    public Course build() {
      Course c = new Course(id, name, schoolId);
      c.validate();
      return c;
    }
  }
}
