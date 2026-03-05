package com.pug.academic.domain;

import com.pug.academic.domain.enums.AcademicFieldErrorCodes;
import com.pug.academic.domain.vos.AcademicRegistration;
import com.pug.academic.domain.vos.CounterpartHours;
import com.pug.academic.domain.vos.Period;
import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.enums.Campi;
import com.pug.shared.domain.enums.SharedFieldErrorCodes;
import com.pug.shared.domain.vos.AuditInfo;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.util.UUID;

/**
 * Immutable Domain Entity representing an enrolled Student.
 * <p>
 * This class maps a specific authentication account to academic records.
 * It serves as an aggregate for managing a student's enrollment status, including
 * their academic registration, campus, course, and required counterpart hours.
 * It extends {@link DomainError} to accumulate validation failures.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Student extends DomainError {

  /**
   * The unique identifier of the linked authentication account, which acts as the primary key.
   */
  UUID accountId;

  /**
   * The validated Academic Registration Value Object associated with the student.
   */
  AcademicRegistration academicRegistration;

  /**
   * The specific university campus where the student is enrolled.
   */
  Campi campus;

  /**
   * The unique identifier of the {@link Course} the student is enrolled in.
   */
  UUID courseId;

  /**
   * The validated Value Object tracking the required counterpart hours the student must fulfill.
   */
  CounterpartHours counterpartHours;

  /**
   * The validated Value Object representing the chronological validity of the student's enrollment.
   */
  Period period;

  /**
   * The audit tracking information (creation and update timestamps).
   */
  AuditInfo auditInfo;

  /**
   * Constructs a {@code Student} instance.
   *
   * @param accountId            the unique identifier of the account
   * @param academicRegistration the academic registration VO
   * @param campus               the campus enumeration
   * @param courseId             the unique identifier of the course
   * @param counterpartHours     the counterpart hours VO
   * @param period               the academic period VO
   * @param auditInfo            the audit tracking VO
   */
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
   * Factory method to create a new {@code Student} aggregate.
   * <p>
   * Initializes standard audit tracking information and performs a full validation
   * of the aggregate and its nested Value Objects.
   *
   * @param accountId the unique identifier of the account
   * @param reg       the {@link AcademicRegistration} value object
   * @param campus    the {@link Campi} where the student is enrolled
   * @param courseId  the unique identifier of the course
   * @param hours     the {@link CounterpartHours} tracking requirements
   * @param period    the valid {@link Period} of enrollment
   * @return a newly created and self-validated {@link Student} instance
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
   * Updates the campus at which the student is enrolled.
   * <p>
   * Since this entity is immutable, this method returns a new {@code Student} instance
   * with the updated campus and a refreshed {@link AuditInfo} timestamp.
   *
   * @param newCampus the new {@link Campi} to set
   * @return a new, updated, and validated {@link Student} instance, or {@code this} if the campus is unchanged
   */
  public Student moveToCampus(Campi newCampus) {
    if (campus == newCampus) {
      return this;
    }
    Student updatedStudent = toBuilder().campus(newCampus).auditInfo(auditInfo.update()).build();
    updatedStudent.collectValidationProblems();
    return updatedStudent;
  }

  /**
   * Updates the academic registration identifier of the student.
   *
   * @param newReg the new {@link AcademicRegistration} value object
   * @return a new, updated, and validated {@link Student} instance, or {@code this} if the registration is unchanged
   */
  public Student changeAcademicRegistration(AcademicRegistration newReg) {
    if (academicRegistration.equals(newReg)) {
      return this;
    }
    Student updatedStudent =
            toBuilder().academicRegistration(newReg).auditInfo(auditInfo.update()).build();
    updatedStudent.collectValidationProblems();
    return updatedStudent;
  }

  /**
   * Updates the course the student is enrolled in.
   *
   * @param newCourseId the unique identifier of the new course
   * @return a new, updated, and validated {@link Student} instance, or {@code this} if the course is unchanged
   */
  public Student changeCourse(UUID newCourseId) {
    if (courseId.equals(newCourseId)) {
      return this;
    }
    Student updatedStudent =
            toBuilder().courseId(newCourseId).auditInfo(auditInfo.update()).build();
    updatedStudent.collectValidationProblems();
    return updatedStudent;
  }

  /**
   * Updates the required counterpart hours for the student.
   *
   * @param newHours the new {@link CounterpartHours} value object
   * @return a new, updated, and validated {@link Student} instance, or {@code this} if the hours are unchanged
   */
  public Student updateRequiredHours(CounterpartHours newHours) {
    if (counterpartHours.equals(newHours)) {
      return this;
    }
    Student updatedStudent =
            toBuilder().counterpartHours(newHours).auditInfo(auditInfo.update()).build();
    updatedStudent.collectValidationProblems();
    return updatedStudent;
  }

  /**
   * Updates the chronological period of the student's enrollment.
   *
   * @param newPeriod the new {@link Period} value object
   * @return a new, updated, and validated {@link Student} instance, or {@code this} if the period is unchanged
   */
  public Student updateDateWindow(Period newPeriod) {
    if (period.equals(newPeriod)) {
      return this;
    }
    Student updatedStudent = toBuilder().period(newPeriod).auditInfo(auditInfo.update()).build();
    updatedStudent.collectValidationProblems();
    return updatedStudent;
  }

  /**
   * Evaluates constraints for the Student aggregate and accumulates any validation problems.
   * <p>
   * Rules applied:
   * <ul>
   *   <li>Ensures the {@code accountId} is not null (appends {@link AcademicFieldErrorCodes#INVALID_ACCOUNT_ID_BLANK})</li>
   *   <li>Ensures the {@code academicRegistration} is not null and bubbles up any internal errors</li>
   *   <li>Ensures the {@code campus} is not null (appends {@link SharedFieldErrorCodes#INVALID_CAMPUS_BLANK})</li>
   *   <li>Ensures the {@code courseId} is not null (appends {@link AcademicFieldErrorCodes#INVALID_COURSE_BLANK})</li>
   *   <li>Ensures the {@code counterpartHours} is not null and bubbles up any internal errors</li>
   *   <li>Ensures the {@code period} is not null and bubbles up any internal errors</li>
   *   <li>Ensures the {@code auditInfo} is not null and bubbles up any internal errors</li>
   * </ul>
   */
  private void collectValidationProblems() {
    if (accountId == null) {
      addFieldError(AcademicFieldErrorCodes.INVALID_ACCOUNT_ID_BLANK);
    }
    if (academicRegistration == null) {
      addFieldError(AcademicFieldErrorCodes.INVALID_REGISTRATION_BLANK);
    } else if (academicRegistration.hasFieldErrors()) {
      addFieldErrors(academicRegistration.getFieldErrors());
    }
    if (campus == null) {
      addFieldError(SharedFieldErrorCodes.INVALID_CAMPUS_BLANK);
    }
    if (courseId == null) {
      addFieldError(AcademicFieldErrorCodes.INVALID_COURSE_BLANK);
    }
    if (counterpartHours == null) {
      addFieldError(AcademicFieldErrorCodes.INVALID_HOURS_BLANK);
    } else if (counterpartHours.hasFieldErrors()) {
      addFieldErrors(counterpartHours.getFieldErrors());
    }
    if (period == null) {
      addFieldError(AcademicFieldErrorCodes.INVALID_PERIOD_BLANK);
    } else if (period.hasFieldErrors()) {
      addFieldErrors(period.getFieldErrors());
    }
    if (auditInfo == null) {
      addFieldError(SharedFieldErrorCodes.INVALID_AUDIT_INFO_BLANK);
    } else if (auditInfo.hasFieldErrors()) {
      addFieldErrors(auditInfo.getFieldErrors());
    }
  }
}