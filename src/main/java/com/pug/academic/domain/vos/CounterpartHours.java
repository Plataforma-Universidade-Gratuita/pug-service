package com.pug.academic.domain.vos;

import com.pug.academic.domain.enums.AcademicFieldErrorCodes;
import com.pug.shared.domain.DomainError;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Immutable Value Object (VO) representing the Counterpart Hours required for a student's project.
 *
 * <p>Extends {@link DomainError} to encapsulate validations relating to time requirements, ensuring
 * hours are properly quantified and avoiding negative/zero time constraints.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class CounterpartHours extends DomainError {

  /** The required amount of hours the student must complete. */
  BigDecimal requiredHours;

  /** A flag indicating whether the required hours have been fully completed. */
  Boolean concluded;

  /**
   * Constructs a {@code CounterpartHours} instance.
   *
   * @param requiredHours the quantified hours required
   * @param concluded the completion status
   */
  @Builder(toBuilder = true)
  private CounterpartHours(BigDecimal requiredHours, Boolean concluded) {
    this.requiredHours = requiredHours;
    this.concluded = concluded;
  }

  /**
   * Factory method to create a new {@code CounterpartHours} instance.
   *
   * <p>If the {@code concluded} flag is omitted (null), it defaults to {@code false}. The returned
   * instance is immediately self-validated.
   *
   * @param requiredHours the required hours
   * @param concluded the completion status (defaults to false if null)
   * @return a self-validated {@link CounterpartHours} instance
   */
  public static CounterpartHours factory(BigDecimal requiredHours, Boolean concluded) {
    CounterpartHours vo =
        CounterpartHours.builder()
            .requiredHours(requiredHours)
            .concluded(concluded != null ? concluded : Boolean.FALSE)
            .build();
    vo.collectValidationProblems();
    return vo;
  }

  /**
   * Evaluates internal constraints and accumulates validation problems.
   *
   * <p>Business rules applied:
   *
   * <ul>
   *   <li>The required hours must not be null (appends {@link
   *       AcademicFieldErrorCodes#INVALID_HOURS_BLANK})
   *   <li>The required hours cannot be negative (appends {@link
   *       AcademicFieldErrorCodes#INVALID_REQUIRED_HOURS_NEGATIVE})
   *   <li>The required hours cannot be exactly zero (appends {@link
   *       AcademicFieldErrorCodes#INVALID_REQUIRED_HOURS_ZERO})
   * </ul>
   */
  private void collectValidationProblems() {
    if (requiredHours == null) {
      addFieldError(AcademicFieldErrorCodes.INVALID_HOURS_BLANK);
      return;
    }
    if (requiredHours.compareTo(BigDecimal.ZERO) < 0) {
      addFieldError(AcademicFieldErrorCodes.INVALID_REQUIRED_HOURS_NEGATIVE);
    } else if (requiredHours.compareTo(BigDecimal.ZERO) == 0) {
      addFieldError(AcademicFieldErrorCodes.INVALID_REQUIRED_HOURS_ZERO);
    }
  }
}
