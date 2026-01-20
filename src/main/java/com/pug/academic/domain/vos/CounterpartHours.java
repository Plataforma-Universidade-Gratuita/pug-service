package com.pug.academic.domain.vos;

import com.pug.academic.domain.enums.AcademicErrorCodes;

import java.math.BigDecimal;

/**
 * Value Object representing Counterpart Hours with validation.
 *
 * <p>Also provides a method to calculate missing hours.
 *
 * @param requiredHours the required hours
 * @param completedHours the completed hours
 */
public record CounterpartHours(BigDecimal requiredHours, BigDecimal completedHours) {
  /**
   * Constructs CounterpartHours after validating the input hours.
   *
   * @param requiredHours the required hours
   * @param completedHours the completed hours
   * @throws AppValidationException if the hours are null, negative, or if completed hours exceed
   *     required hours
   */
  public CounterpartHours {
    if (requiredHours == null || completedHours == null) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_HOURS_BLANK);
    }
    if (requiredHours.signum() < 0 || completedHours.signum() < 0) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_HOURS_BLANK);
    }
    if (completedHours.compareTo(requiredHours) > 0) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_HOURS_COMPLETED_GT_REQUIRED);
    }
  }
}
