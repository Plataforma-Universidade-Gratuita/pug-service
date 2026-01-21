package com.pug.academic.domain.vos;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.exceptions.AppValidationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Value Object representing Counterpart Hours with validation.
 *
 * <p>Also provides a method to calculate missing hours.
 *
 * @param requiredHours  the required hours
 * @param completedHours the completed hours
 */
public record CounterpartHours(BigDecimal requiredHours, BigDecimal completedHours) {
  /**
   * Constructs CounterpartHours after validating the input hours.
   *
   * @param requiredHours  the required hours
   * @param completedHours the completed hours
   * @throws AppValidationException if the hours are null, negative, or if completed hours exceed
   *                                required hours. This exception may contain multiple validation problems.
   */
  public CounterpartHours {
    List<AppValidationException.Problem> problems = new ArrayList<>();

    if (requiredHours == null) {
      problems.add(new AppValidationException.Problem(AcademicErrorCodes.INVALID_HOURS_BLANK, "requiredHours"));
    }
    if (completedHours == null) {
      problems.add(new AppValidationException.Problem(AcademicErrorCodes.INVALID_HOURS_BLANK, "completedHours"));
    }
    if (requiredHours != null && completedHours != null) {
      if (requiredHours.signum() < 0) {
        problems.add(new AppValidationException.Problem(AcademicErrorCodes.INVALID_HOURS_BLANK, "requiredHours"));
      }
      if (completedHours.signum() < 0) {
        problems.add(new AppValidationException.Problem(AcademicErrorCodes.INVALID_HOURS_BLANK, "completedHours"));
      }
      if (completedHours.compareTo(requiredHours) > 0) {
        problems.add(new AppValidationException.Problem(AcademicErrorCodes.INVALID_HOURS_COMPLETED_GT_REQUIRED, "completedHours"));
      }
    }

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
  }

  /**
   * Calculates the remaining hours until the required hours are met.
   *
   * @return the number of hours still needed.
   */
  public BigDecimal getRemainingHours() {
    return requiredHours.subtract(completedHours);
  }
}