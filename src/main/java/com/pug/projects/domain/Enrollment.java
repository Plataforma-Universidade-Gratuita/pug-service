package com.pug.projects.domain;

import com.pug.academic.domain.Student;
import com.pug.projects.domain.enums.EnrollmentStatus;
import com.pug.projects.domain.vos.EnrollmentInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** Domain entity representing a Course. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Enrollment {
  private final Student student;
  private final Project project;
  private final EnrollmentStatus status;
  private final EnrollmentInfo enrollmentInfo;

  private void validate() {}

  /** Builder class for constructing Enrollment instances with validation. */
  public static class EnrollmentBuilder {
    /**
     * Builds the Enrollment instance and performs validation.
     *
     * @return the validated Enrollment instance.
     */
    public Enrollment build() {
      Enrollment c = new Enrollment(student, project, status, enrollmentInfo);
      c.validate();
      return c;
    }
  }
}
