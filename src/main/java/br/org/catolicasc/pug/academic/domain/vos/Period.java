/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

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

  LocalDate startDate;

  LocalDate dueDate;

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
