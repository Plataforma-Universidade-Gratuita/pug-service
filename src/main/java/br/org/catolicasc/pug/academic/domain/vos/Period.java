package br.org.catolicasc.pug.academic.domain.vos;

import br.org.catolicasc.pug.academic.domain.enums.AcademicFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.DomainError;
import java.time.LocalDate;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Immutable Value Object (VO) representing an Academic Period with a defined start and due date.
 *
 * <p>Extends {@link DomainError} to encapsulate validations relating to chronological integrity,
 * ensuring that end dates never precede start dates.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class Period extends DomainError {

  /** The start date of the period. */
  LocalDate startDate;

  /** The due date (end date) of the period. */
  LocalDate dueDate;

  /**
   * Constructs a {@code Period} instance.
   *
   * @param startDate the start date
   * @param dueDate the due date
   */
  @Builder(toBuilder = true)
  private Period(LocalDate startDate, LocalDate dueDate) {
    this.startDate = startDate;
    this.dueDate = dueDate;
  }

  /**
   * Factory method to create a new {@code Period} instance.
   *
   * <p>The instance is immediately self-validated. Any chronological or structural violations are
   * accumulated internally.
   *
   * @param startDate the start date
   * @param dueDate the due date
   * @return a self-validated {@link Period} instance
   */
  public static Period factory(LocalDate startDate, LocalDate dueDate) {
    Period vo = Period.builder().startDate(startDate).dueDate(dueDate).build();
    vo.collectValidationProblems();
    return vo;
  }

  /**
   * Evaluates internal constraints and accumulates validation problems.
   *
   * <p>Business rules applied:
   *
   * <ul>
   *   <li>The start date must not be null (appends {@link
   *       AcademicFieldErrorCodes#INVALID_START_DATE_BLANK})
   *   <li>The due date must not be null (appends {@link
   *       AcademicFieldErrorCodes#INVALID_DUE_DATE_BLANK})
   *   <li>The due date must not occur chronologically before the start date (appends {@link
   *       AcademicFieldErrorCodes#INVALID_PERIOD_RANGE})
   * </ul>
   */
  private void collectValidationProblems() {
    boolean hasNulls = false;
    if (startDate == null) {
      addFieldError(AcademicFieldErrorCodes.INVALID_START_DATE_BLANK);
      hasNulls = true;
    }
    if (dueDate == null) {
      addFieldError(AcademicFieldErrorCodes.INVALID_DUE_DATE_BLANK);
      hasNulls = true;
    }
    if (!hasNulls && dueDate.isBefore(startDate)) {
      addFieldError(AcademicFieldErrorCodes.INVALID_PERIOD_RANGE);
    }
  }
}
