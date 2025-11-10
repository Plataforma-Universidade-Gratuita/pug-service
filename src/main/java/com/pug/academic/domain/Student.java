package com.pug.academic.domain;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.domain.enums.Campi;
import com.pug.academic.domain.vos.AcademicRegistration;
import com.pug.academic.domain.vos.CounterpartHours;
import com.pug.academic.domain.vos.Period;
import com.pug.shared.exceptions.AppValidationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/** Domain entity representing a Student with validation and modification methods. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Student {
  private final UUID userId;
  private final AcademicRegistration academicRegistration;
  private final Campi campus;
  private final UUID courseId;
  private final CounterpartHours counterpartHours;
  private final Period period;

  /**
   * Factory method to create a new Student with validation.
   * @param userId the unique identifier of the user.
   * @param reg the academic registration details.
   * @param campus the campus of the student.
   * @param courseId the course identifier.
   * @param hours the counterpart hours details.
   * @param period the academic period details.
   * @return a validated Student instance.
   */
  public static Student createNew(
          UUID userId,
          AcademicRegistration reg,
          Campi campus,
          UUID courseId,
          CounterpartHours hours,
          Period period) {
    Student s = new Student(userId, reg, campus, courseId, hours, period);
    s.validate();
    return s;
  }

  /**
   * Behavior: Change the campus of the student.
   * @param newCampus the new campus to set.
   * @return a new Student instance with the updated campus.
   */
  public Student changeCampus(Campi newCampus) {
    Student s = this.toBuilder().campus(newCampus).build();
    s.validate();
    return s;
  }

  /**
   * Behavior: Move the student to a new course.
   * @param newCourseId the new course identifier.
   * @return a new Student instance with the updated course.
   */
  public Student moveToCourse(UUID newCourseId) {
    Student s = this.toBuilder().courseId(newCourseId).build();
    s.validate();
    return s;
  }

  /**
   * Behavior: Update the counterpart hours of the student.
   * @param newHours the new counterpart hours to set.
   * @return a new Student instance with the updated hours.
   */
  public Student changeHours(CounterpartHours newHours) {
    Student s = this.toBuilder().counterpartHours(newHours).build();
    s.validate();
    return s;
  }

  /**
   * Behavior: Change the academic period of the student.
   * @param newPeriod the new period to set.
   * @return a new Student instance with the updated period.
   */
  public Student changePeriod(Period newPeriod) {
    Student s = this.toBuilder().period(newPeriod).build();
    s.validate();
    return s;
  }

  /**
   * Validates the Student entity to ensure all fields are properly set.
   * @throws AppValidationException if any field is invalid.
   */
  private void validate() {
    if (userId == null) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_STUDENT_USER);
    }
    if (academicRegistration == null) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_REGISTRATION);
    }
    if (campus == null) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_CAMPUS);
    }
    if (courseId == null) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_COURSE);
    }
    if (counterpartHours == null) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_HOURS);
    }
    if (period == null) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_PERIOD);
    }
  }

  /** Builder class for constructing Student instances. */
  public static class StudentBuilder {
    /**
     * Builds and returns a validated Student instance.
     * @return a validated Student instance.
     */
    public Student build() {
      return createNew(userId, academicRegistration, campus, courseId, counterpartHours, period);
    }
  }
}
