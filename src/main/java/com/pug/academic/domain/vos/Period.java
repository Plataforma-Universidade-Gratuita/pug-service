package com.pug.academic.domain.vos;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.exceptions.AppValidationException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Value Object representing a Period with start and due dates. Extends DomainError to allow
 * deferred validation.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class Period extends DomainError {

  LocalDate startDate;
  LocalDate dueDate;

  @Builder(toBuilder = true)
  private Period(LocalDate startDate, LocalDate dueDate) {
    this.startDate = startDate;
    this.dueDate = dueDate;
  }

  /**
   * Factory method to create a new Period.
   *
   * @param startDate the start date
   * @param dueDate the due date
   * @return The Period instance (which may contain errors)
   */
  public static Period factory(LocalDate startDate, LocalDate dueDate) {
    Period vo = Period.builder().startDate(startDate).dueDate(dueDate).build();
    vo.validate();
    return vo;
  }

  /** Validates the period dates. */
  private void validate() {
    if (startDate == null) {
      addError(new AppValidationException.Problem(AcademicErrorCodes.INVALID_PERIOD_BLANK));
    }
    if (dueDate == null) {
      addError(new AppValidationException.Problem(AcademicErrorCodes.INVALID_PERIOD_BLANK));
    }

    if (startDate != null && dueDate != null) {
      if (dueDate.isBefore(startDate)) {
        addError(new AppValidationException.Problem(AcademicErrorCodes.INVALID_PERIOD_RANGE));
      }
    }
  }

  /**
   * Calculates the number of remaining days until the due date.
   *
   * @param referenceDate the date from which to calculate remaining days.
   * @return the number of remaining days.
   */
  public long getRemainingDays(LocalDate referenceDate) {
    if (referenceDate == null) {
      throw new IllegalArgumentException(
          "Reference date cannot be null for remaining days calculation.");
    }
    return ChronoUnit.DAYS.between(referenceDate, dueDate);
  }
}
