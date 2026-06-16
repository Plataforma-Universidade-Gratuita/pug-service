package br.org.catolicasc.pug.academic.domain;

import br.org.catolicasc.pug.academic.domain.enums.AcademicFieldErrorCodes;
import br.org.catolicasc.pug.academic.domain.enums.AcademicErrorCodes;
import br.org.catolicasc.pug.academic.domain.vos.AcademicRegistration;
import br.org.catolicasc.pug.academic.domain.vos.CounterpartHours;
import br.org.catolicasc.pug.academic.domain.vos.Period;
import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.domain.enums.SharedFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.vos.AuditInfo;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Immutable Domain Entity representing an enrolled FormerStudent.
 *
 * <p>This class maps a specific authentication account to academic records. It serves as an
 * aggregate for managing a formerStudent's enrollment status, including their academic
 * registration, campus, course, and required counterpart hours. It extends {@link DomainError} to
 * accumulate validation failures.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class FormerStudent extends DomainError {

  /** The unique identifier of the linked authentication account, which acts as the primary key. */
  UUID accountId;

  /** The validated Academic Registration Value Object associated with the formerStudent. */
  AcademicRegistration academicRegistration;

  /** The specific university campus where the formerStudent is enrolled. */
  Campi campus;

  /** The unique identifier of the {@link Course} the formerStudent is enrolled in. */
  UUID courseId;

  /**
   * The validated Value Object tracking the required counterpart hours the formerStudent must
   * fulfill.
   */
  CounterpartHours counterpartHours;

  /**
   * The validated Value Object representing the chronological validity of the formerStudent's
   * enrollment.
   */
  Period period;

  /** The audit tracking information (creation and update timestamps). */
  AuditInfo auditInfo;

  /**
   * Constructs a {@code FormerStudent} instance.
   *
   * @param accountId the unique identifier of the account
   * @param academicRegistration the academic registration VO
   * @param campus the campus enumeration
   * @param courseId the unique identifier of the course
   * @param counterpartHours the counterpart hours VO
   * @param period the academic period VO
   * @param auditInfo the audit tracking VO
   */
  @Builder(toBuilder = true)
  private FormerStudent(
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
   * Factory method to create a new {@code FormerStudent} aggregate.
   *
   * <p>Initializes standard audit tracking information and performs a full validation of the
   * aggregate and its nested Value Objects.
   *
   * @param accountId the unique identifier of the account
   * @param reg the {@link AcademicRegistration} value object
   * @param campus the {@link Campi} where the formerStudent is enrolled
   * @param courseId the unique identifier of the course
   * @param hours the {@link CounterpartHours} tracking requirements
   * @param period the valid {@link Period} of enrollment
   * @return a newly created and self-validated {@link FormerStudent} instance
   */
  public static FormerStudent factory(
      UUID accountId,
      AcademicRegistration reg,
      Campi campus,
      UUID courseId,
      CounterpartHours hours,
      Period period) {
    FormerStudent formerStudent =
        FormerStudent.builder()
            .accountId(accountId)
            .academicRegistration(reg)
            .campus(campus)
            .courseId(courseId)
            .counterpartHours(hours)
            .period(period)
            .auditInfo(AuditInfo.factory())
            .build();

    formerStudent.collectValidationProblems();
    return formerStudent;
  }

  /**
   * Updates the campus at which the formerStudent is enrolled.
   *
   * <p>Since this entity is immutable, this method returns a new {@code FormerStudent} instance
   * with the updated campus and a refreshed {@link AuditInfo} timestamp.
   *
   * @param newCampus the new {@link Campi} to set
   * @return a new, updated, and validated {@link FormerStudent} instance, or {@code this} if the
   *     campus is unchanged
   */
  public FormerStudent moveToCampus(Campi newCampus) {
    if (campus == newCampus) {
      return this;
    }
    FormerStudent updatedStudent =
        toBuilder().campus(newCampus).auditInfo(auditInfo.update()).build();
    updatedStudent.collectValidationProblems();
    return updatedStudent;
  }

  /**
   * Updates the academic registration identifier of the formerStudent.
   *
   * @param newReg the new {@link AcademicRegistration} value object
   * @return a new, updated, and validated {@link FormerStudent} instance, or {@code this} if the
   *     registration is unchanged
   */
  public FormerStudent changeAcademicRegistration(AcademicRegistration newReg) {
    if (academicRegistration.equals(newReg)) {
      return this;
    }
    FormerStudent updatedStudent =
        toBuilder().academicRegistration(newReg).auditInfo(auditInfo.update()).build();
    updatedStudent.collectValidationProblems();
    return updatedStudent;
  }

  /**
   * Updates the course the formerStudent is enrolled in.
   *
   * @param newCourseId the unique identifier of the new course
   * @return a new, updated, and validated {@link FormerStudent} instance, or {@code this} if the
   *     course is unchanged
   */
  public FormerStudent changeCourse(UUID newCourseId) {
    if (courseId.equals(newCourseId)) {
      return this;
    }
    FormerStudent updatedStudent =
        toBuilder().courseId(newCourseId).auditInfo(auditInfo.update()).build();
    updatedStudent.collectValidationProblems();
    return updatedStudent;
  }

  /**
   * Updates the required counterpart hours for the formerStudent.
   *
   * @param newHours the new {@link CounterpartHours} value object
   * @return a new, updated, and validated {@link FormerStudent} instance, or {@code this} if the
   *     hours are unchanged
   */
  public FormerStudent updateRequiredHours(CounterpartHours newHours) {
    if (counterpartHours.equals(newHours)) {
      return this;
    }
    FormerStudent updatedStudent =
        toBuilder().counterpartHours(newHours).auditInfo(auditInfo.update()).build();
    updatedStudent.collectValidationProblems();
    return updatedStudent;
  }

  /**
   * Updates the chronological period of the formerStudent's enrollment.
   *
   * @param newPeriod the new {@link Period} value object
   * @return a new, updated, and validated {@link FormerStudent} instance, or {@code this} if the
   *     period is unchanged
   */
  public FormerStudent updateDateWindow(Period newPeriod) {
    if (period.equals(newPeriod)) {
      return this;
    }
    FormerStudent updatedStudent =
        toBuilder().period(newPeriod).auditInfo(auditInfo.update()).build();
    updatedStudent.collectValidationProblems();
    return updatedStudent;
  }

  /**
   * Adds completed hours to the formerStudent's progress.
   *
   * <p>If the total completed hours reaches or exceeds the required hours, the formerStudent's
   * counterpart status is automatically updated.
   *
   * @param hours the amount of hours to add
   * @return a new instance of {@link FormerStudent} with updated hours and recalculated status
   */
  public FormerStudent addCompletedHours(BigDecimal hours) {
    BigDecimal newTotal = counterpartHours.getCompletedHours().add(hours);
    boolean isNowConcluded = newTotal.compareTo(counterpartHours.getRequiredHours()) >= 0;

    CounterpartHours updatedHours =
        CounterpartHours.factory(counterpartHours.getRequiredHours(), newTotal, isNowConcluded);

    FormerStudent updated =
        toBuilder().counterpartHours(updatedHours).auditInfo(auditInfo.update()).build();

    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Validates whether the former student can enroll in new projects.
   *
   * @throws BusinessRuleException if the counterpart-hour requirement is already concluded
   */
  public void validateCanEnroll() {
    if (counterpartHours != null && Boolean.TRUE.equals(counterpartHours.getConcluded())) {
      throw new BusinessRuleException(AcademicErrorCodes.FORMER_STUDENT_ENROLLMENT_CONCLUDED);
    }
  }

  /**
   * Evaluates constraints for the FormerStudent aggregate and accumulates any validation problems.
   *
   * <p>Rules applied:
   *
   * <ul>
   *   <li>Ensures the {@code accountId} is not null (appends {@link
   *       AcademicFieldErrorCodes#INVALID_ACCOUNT_ID_BLANK})
   *   <li>Ensures the {@code academicRegistration} is not null and bubbles up any internal errors
   *   <li>Ensures the {@code campus} is not null (appends {@link
   *       SharedFieldErrorCodes#INVALID_CAMPUS_BLANK})
   *   <li>Ensures the {@code courseId} is not null (appends {@link
   *       AcademicFieldErrorCodes#INVALID_COURSE_BLANK})
   *   <li>Ensures the {@code counterpartHours} is not null and bubbles up any internal errors
   *   <li>Ensures the {@code period} is not null and bubbles up any internal errors
   *   <li>Ensures the {@code auditInfo} is not null and bubbles up any internal errors
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
