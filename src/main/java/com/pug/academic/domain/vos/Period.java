package com.pug.academic.domain.vos;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.exceptions.AppValidationException;

import java.time.LocalDate;

/**
 * Value Object representing a Period with start and due dates.
 *
 * <p>Also contains a method to calculate remaining days until the due date.
 *
 * @param startDate the start date of the period
 * @param dueDate   the due date of the period
 */
public record Period(LocalDate startDate, LocalDate dueDate) {
  /**
   * Constructs a Period after validating the start and due dates.
   *
   * @param startDate the start date
   * @param dueDate   the due date
   * @throws AppValidationException if the dates are null or if the due date is before the start
   *                                date
   */
  public Period {
    if (startDate == null || dueDate == null) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_PERIOD_BLANK);
    }
    if (dueDate.isBefore(startDate)) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_PERIOD_RANGE);
    }
  }
}
