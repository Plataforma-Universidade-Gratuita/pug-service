package com.pug.academic.domain.vos;

import com.pug.shared.domain.DomainError;
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
    vo.collectValidationProblems();
    return vo;
  }

  /** Validates the period dates. */
  private void collectValidationProblems() {
    validateDateFields(startDate, dueDate);
  }

  /**
   * Calculates the total number of days in the period.
   *
   * @return the number of days between startDate and dueDate
   * @throws IllegalStateException if startDate or dueDate is null
   */
  public long getPeriodInDays() {
    if (startDate == null || dueDate == null) {
      throw new IllegalStateException(
          "Cannot calculate period in days when start date or due date is null.");
    }
    return ChronoUnit.DAYS.between(startDate, dueDate);
  }

  /**
   * Calculates the number of remaining days until the due date.
   *
   * @param referenceDate the date from which to calculate remaining days.
   * @return the number of remaining days.
   * @throws IllegalArgumentException if referenceDate is null
   */
  public long getRemainingDays(LocalDate referenceDate) {
    if (referenceDate == null) {
      throw new IllegalArgumentException(
          "Reference date cannot be null for remaining days calculation.");
    }
    return ChronoUnit.DAYS.between(referenceDate, dueDate);
  }
}
