package com.pug.academic.domain.vos;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.exceptions.AppValidationException;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Value Object representing Counterpart Hours. Extends DomainError to allow deferred validation.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class CounterpartHours extends DomainError {

  BigDecimal requiredHours;
  BigDecimal completedHours;

  @Builder(toBuilder = true)
  private CounterpartHours(BigDecimal requiredHours, BigDecimal completedHours) {
    this.requiredHours = requiredHours;
    this.completedHours = completedHours;
  }

  /**
   * Factory method to create a new CounterpartHours.
   *
   * @param requiredHours the required hours
   * @param completedHours the completed hours
   * @return The CounterpartHours instance (which may contain errors)
   */
  public static CounterpartHours factory(BigDecimal requiredHours, BigDecimal completedHours) {
    CounterpartHours vo =
        CounterpartHours.builder()
            .requiredHours(requiredHours)
            .completedHours(completedHours)
            .build();
    vo.collectValidationProblems();
    return vo;
  }

  /** Validates the hours. */
  private void collectValidationProblems() {
    if (requiredHours == null) {
      addError(new Problem(AcademicErrorCodes.INVALID_HOURS_BLANK));
    }
    if (completedHours == null) {
      addError(new Problem(AcademicErrorCodes.INVALID_HOURS_BLANK));
    }

    if (requiredHours != null && completedHours != null) {
      if (requiredHours.signum() < 0) {
        addError(new Problem(AcademicErrorCodes.INVALID_HOURS_NEGATIVE));
      }
      if (completedHours.signum() < 0) {
        addError(new Problem(AcademicErrorCodes.INVALID_HOURS_NEGATIVE));
      }
      if (completedHours.compareTo(requiredHours) > 0) {
        addError(
            new Problem(
                AcademicErrorCodes.INVALID_HOURS_COMPLETED_GT_REQUIRED));
      }
    }
  }

  /**
   * Calculates the remaining hours until the required hours are met.
   *
   * @return the number of hours still needed.
   */
  public BigDecimal getRemainingHours() {
    if (requiredHours != null && completedHours != null) {
      return requiredHours.subtract(completedHours);
    }
    return BigDecimal.ZERO;
  }
}
