package com.pug.academic.domain;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.domain.enums.Campi;
import com.pug.academic.domain.vos.AcademicRegistration;
import com.pug.academic.domain.vos.CounterpartHours;
import com.pug.academic.domain.vos.Period;
import com.pug.shared.exceptions.AppValidationException;
import java.util.ArrayList;
import java.util.List;
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
   * @throws AppValidationException if validation fails
   */
  public static Student createNew(
      UUID accountId,
      AcademicRegistration reg,
      Campi campus,
      UUID courseId,
      CounterpartHours hours,
      Period period) {
    Student student =
        Student.builder()
            .accountId(accountId)
            .academicRegistration(reg)
            .campus(campus)
            .courseId(courseId)
            .counterpartHours(hours)
            .period(period)
            .build();

    List<AppValidationException.Problem> problems = student.collectValidationProblems();
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return student;
  }

  /**
   * Behavior: Change the campus at which the student is enrolled.
   *
   * @param newCampus the new campus to set
   * @return a new student instance with the updated campus
   * @throws AppValidationException if validation fails
   */
  public Student changeCampus(Campi newCampus) {
    Student updatedStudent = this.toBuilder().campus(newCampus).build();
    List<AppValidationException.Problem> problems = updatedStudent.collectValidationProblems();
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return updatedStudent;
  }

  /**
   * Behavior: Change the academic registration of the student.
   *
   * @param newReg the new academic registration to set
   * @return a new student instance with the updated academic registration
   * @throws AppValidationException if validation fails
   */
  public Student changeAcademicRegistration(AcademicRegistration newReg) {
    Student updatedStudent = this.toBuilder().academicRegistration(newReg).build();
    List<AppValidationException.Problem> problems = updatedStudent.collectValidationProblems();
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return updatedStudent;
  }

  /**
   * Behavior: Change the course the student is enrolled in.
   *
   * @param newCourseId the new course to set
   * @return a new student instance with the updated course
   * @throws AppValidationException if validation fails
   */
  public Student changeCourse(UUID newCourseId) {
    Student updatedStudent = this.toBuilder().courseId(newCourseId).build();
    List<AppValidationException.Problem> problems = updatedStudent.collectValidationProblems();
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return updatedStudent;
  }

  /**
   * Behavior: Change the counterpart hours of the student.
   *
   * @param newHours the new counterpart hours to set
   * @return a new student instance with the updated counterpart hours
   * @throws AppValidationException if validation fails
   */
  public Student changeCounterpartHours(CounterpartHours newHours) {
    Student updatedStudent = this.toBuilder().counterpartHours(newHours).build();
    List<AppValidationException.Problem> problems = updatedStudent.collectValidationProblems();
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return updatedStudent;
  }

  /**
   * Behavior: Change the period of the student.
   *
   * @param newPeriod the new period to set
   * @return a new student instance with the updated period
   * @throws AppValidationException if validation fails
   */
  public Student changePeriod(Period newPeriod) {
    Student updatedStudent = this.toBuilder().period(newPeriod).build();
    List<AppValidationException.Problem> problems = updatedStudent.collectValidationProblems();
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return updatedStudent;
  }

  /**
   * Collects all validation problems for the Student instance.
   *
   * @return A list of {@code AppValidationException.Problem}; an empty list otherwise.
   */
  private List<AppValidationException.Problem> collectValidationProblems() {
    List<AppValidationException.Problem> problems = new ArrayList<>();

    if (accountId == null) {
      problems.add(
          new AppValidationException.Problem(
              AcademicErrorCodes.INVALID_STUDENT_ACCOUNT_BLANK, "accountId"));
    }
    if (academicRegistration == null) {
      problems.add(
          new AppValidationException.Problem(
              AcademicErrorCodes.INVALID_REGISTRATION_BLANK, "academicRegistration"));
    }
    if (campus == null) {
      problems.add(
          new AppValidationException.Problem(AcademicErrorCodes.INVALID_CAMPUS_BLANK, "campus"));
    }
    if (courseId == null) {
      problems.add(
          new AppValidationException.Problem(AcademicErrorCodes.INVALID_COURSE_BLANK, "courseId"));
    }
    if (counterpartHours == null) {
      problems.add(
          new AppValidationException.Problem(
              AcademicErrorCodes.INVALID_HOURS_BLANK, "counterpartHours"));
    }
    if (period == null) {
      problems.add(
          new AppValidationException.Problem(AcademicErrorCodes.INVALID_PERIOD_BLANK, "period"));
    }
    return problems;
  }
}
