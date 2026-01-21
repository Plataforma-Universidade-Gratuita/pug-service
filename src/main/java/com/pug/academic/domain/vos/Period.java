package com.pug.academic.domain.vos;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.exceptions.AppValidationException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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
   *                                date. This exception may contain multiple validation problems.
   */
  public Period {
    List<AppValidationException.Problem> problems = new ArrayList<>();

    if (startDate == null) {
      problems.add(new AppValidationException.Problem(AcademicErrorCodes.INVALID_PERIOD_BLANK, "startDate"));
    }
    if (dueDate == null) {
      problems.add(new AppValidationException.Problem(AcademicErrorCodes.INVALID_PERIOD_BLANK, "dueDate"));
    }

    if (startDate != null && dueDate != null) {
      if (dueDate.isBefore(startDate)) {
        problems.add(new AppValidationException.Problem(AcademicErrorCodes.INVALID_PERIOD_RANGE, "dueDate"));
      }
    }

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
  }

  /**
   * Calculates the number of remaining days until the due date from a given reference date.
   *
   * @param referenceDate the date from which to calculate remaining days.
   * @return the number of remaining days.
   */
  public long getRemainingDays(LocalDate referenceDate) {
    if (referenceDate == null) {
      throw new IllegalArgumentException("Reference date cannot be null for remaining days calculation.");
    }
    return ChronoUnit.DAYS.between(referenceDate, dueDate);
  }
}