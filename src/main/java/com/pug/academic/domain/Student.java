package com.pug.academic.domain;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.domain.enums.Campi;
import com.pug.academic.domain.vos.AcademicRegistration;
import com.pug.academic.domain.vos.CounterpartHours;
import com.pug.academic.domain.vos.Period;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** Student entity aggregate. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Student {
  private final UUID accountId;
  private final AcademicRegistration academicRegistration;
  private final Campi campus;
  private final UUID courseId;
  private final CounterpartHours counterpartHours;
  private final Period period;

  /**
   * Factory for new students.
   *
   * @param accountId the unique identifier of the account
   * @param reg the academic registration for the student
   * @param campus the campus at which the student is enrolled
   * @param courseId the course identifier the student is enrolled in
   * @param hours the counterpart hours details
   * @param period the academic period details
   * @return the created student
   */
  public static Student createNew(
      UUID accountId,
      AcademicRegistration reg,
      Campi campus,
      UUID courseId,
      CounterpartHours hours,
      Period period) {
    Student s = new Student(accountId, reg, campus, courseId, hours, period);
    s.validate();
    return s;
  }

  /**
   * Behavior: Change the campus at which the student is enrolled.
   *
   * @param newCampus the new campus to set
   * @return a new student instance with the updated campus
   */
  public Student changeCampus(Campi newCampus) {
    Student s = this.toBuilder().campus(newCampus).build();
    s.validate();
    return s;
  }

  /**
   * Behavior: Change the academic registration of the student.
   *
   * @param newReg the new academic registration to set
   * @return a new student instance with the updated academic registration
   */
  public Student changeAcademicRegistration(AcademicRegistration newReg) {
    Student s = this.toBuilder().academicRegistration(newReg).build();
    s.validate();
    return s;
  }

  /**
   * Validates the Student aggregate
   *
   * <p>Checks that all required fields are not null.
   *
   * @throws AppValidationException if any field is invalid.
   */
  private void validate() {
    if (accountId == null) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_STUDENT_ACCOUNT_BLANK);
    }
    if (academicRegistration == null) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_REGISTRATION_BLANK);
    }
    if (campus == null) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_CAMPUS_BLANK);
    }
    if (courseId == null) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_COURSE_BLANK);
    }
    if (counterpartHours == null) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_HOURS_BLANK);
    }
    if (period == null) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_PERIOD_BLANK);
    }
  }

  /**
   * Builder class for Student.
   *
   * <p>Overrides the build method to include validation.
   */
  public static class StudentBuilder {
    /**
     * Builds and returns a validated Student instance.
     *
     * @return a validated Student instance.
     */
    public Student build() {
      return createNew(accountId, academicRegistration, campus, courseId, counterpartHours, period);
    }
  }
}
