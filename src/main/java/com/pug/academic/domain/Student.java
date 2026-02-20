package com.pug.academic.domain;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.domain.enums.Campi;
import com.pug.academic.domain.vos.AcademicRegistration;
import com.pug.academic.domain.vos.CounterpartHours;
import com.pug.academic.domain.vos.Period;
import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.Problem;
import com.pug.shared.domain.vos.AuditInfo;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.util.UUID;

/**
 * Student entity aggregate.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Student extends DomainError {
  UUID accountId;
  AcademicRegistration academicRegistration;
  Campi campus;
  UUID courseId;
  CounterpartHours counterpartHours;
  Period period;
  AuditInfo auditInfo;

  @Builder(toBuilder = true)
  private Student(
          UUID accountId,
          AcademicRegistration academicRegistration,
          Campi campus,
          UUID courseId,
          CounterpartHours counterpartHours,
          Period period,
          AuditInfo auditInfo) {
    this.accountId = accountId;
    this.academicRegistration = academicRegistration;
    this.campus = campus;
    this.courseId = courseId;
    this.counterpartHours = counterpartHours;
    this.period = period;
    this.auditInfo = auditInfo;
  }

  /**
   * Factory for new students.
   *
   * @param accountId the unique identifier of the account
   * @param reg       the academic registration for the student
   * @param campus    the campus at which the student is enrolled
   * @param courseId  the course identifier the student is enrolled in
   * @param hours     the counterpart hours details
   * @param period    the academic period details
   * @return the created student (may contain errors)
   */
  public static Student factory(
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
                    .auditInfo(AuditInfo.factory())
                    .build();

    student.collectValidationProblems();
    return student;
  }

  /**
   * Behavior: Change the campus at which the student is enrolled.
   *
   * @param newCampus the new campus to set
   * @return a new student instance with the updated campus
   */
  public Student changeCampus(Campi newCampus) {
    if (campus == newCampus) {
      return this;
    }
    Student updatedStudent = toBuilder().campus(newCampus).auditInfo(auditInfo.update()).build();
    updatedStudent.collectValidationProblems();
    return updatedStudent;
  }

  /**
   * Behavior: Change the academic registration of the student.
   *
   * @param newReg the new academic registration to set
   * @return a new student instance with the updated academic registration
   */
  public Student changeAcademicRegistration(AcademicRegistration newReg) {
    if (academicRegistration.equals(newReg)) {
      return this;
    }
    Student updatedStudent = toBuilder().academicRegistration(newReg).auditInfo(auditInfo.update()).build();
    updatedStudent.collectValidationProblems();
    return updatedStudent;
  }

  /**
   * Behavior: Change the course the student is enrolled in.
   *
   * @param newCourseId the new course to set
   * @return a new student instance with the updated course
   */
  public Student changeCourse(UUID newCourseId) {
    if (courseId.equals(newCourseId)) {
      return this;
    }
    Student updatedStudent = toBuilder().courseId(newCourseId).auditInfo(auditInfo.update()).build();
    updatedStudent.collectValidationProblems();
    return updatedStudent;
  }

  /**
   * Behavior: Change the counterpart hours of the student.
   *
   * @param newHours the new counterpart hours to set
   * @return a new student instance with the updated counterpart hours
   */
  public Student changeCounterpartHours(CounterpartHours newHours) {
    if (counterpartHours.equals(newHours)) {
      return this;
    }
    Student updatedStudent = toBuilder().counterpartHours(newHours).auditInfo(auditInfo.update()).build();
    updatedStudent.collectValidationProblems();
    return updatedStudent;
  }

  /**
   * Behavior: Change the period of the student.
   *
   * @param newPeriod the new period to set
   * @return a new student instance with the updated period
   */
  public Student changePeriod(Period newPeriod) {
    if (period.equals(newPeriod)) {
      return this;
    }
    Student updatedStudent = toBuilder().period(newPeriod).auditInfo(auditInfo.update()).build();
    updatedStudent.collectValidationProblems();
    return updatedStudent;
  }

  /**
   * Collects all validation problems for the Student instance.
   */
  private void collectValidationProblems() {
    validateForeignKeyField(accountId, "accountId");
    validateForeignKeyField(courseId, "courseId");
    if (campus == null) {
      addError(new Problem(AcademicErrorCodes.INVALID_CAMPUS_BLANK));
    }
    if (academicRegistration == null) {
      addError(new Problem(AcademicErrorCodes.INVALID_REGISTRATION_BLANK));
    } else if (academicRegistration.hasErrors()) {
      addErrors(academicRegistration.getProblems());
    }
    if (counterpartHours == null) {
      addError(new Problem(AcademicErrorCodes.INVALID_HOURS_BLANK));
    } else if (counterpartHours.hasErrors()) {
      addErrors(counterpartHours.getProblems());
    }
    if (period == null) {
      addError(new Problem(AcademicErrorCodes.INVALID_PERIOD_BLANK));
    } else if (period.hasErrors()) {
      addErrors(period.getProblems());
    }
  }
}
