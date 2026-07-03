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
import java.math.BigDecimal;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Immutable Value Object (VO) representing the Counterpart Hours required for a formerStudent's
 * project and the progress made toward completion.
 *
 * <p>Extends {@link DomainError} to encapsulate validations relating to time requirements, ensuring
 * hours remain within logically valid bounds (non-negative and not exceeding requirements).
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class CounterpartHours extends DomainError {

  BigDecimal requiredHours;

  BigDecimal completedHours;

  Boolean concluded;

  @Builder(toBuilder = true)
  private CounterpartHours(BigDecimal requiredHours, BigDecimal completedHours, Boolean concluded) {
    this.requiredHours = requiredHours;
    this.completedHours = completedHours;
    this.concluded = concluded;
  }

  /**
   * Factory method to create a new {@code CounterpartHours} instance.
   *
   * @param requiredHours the required hours
   * @param completedHours the completed hours
   * @param concluded the completion status
   * @return a self-validated {@link CounterpartHours} instance
   */
  public static CounterpartHours factory(
      BigDecimal requiredHours, BigDecimal completedHours, Boolean concluded) {
    CounterpartHours vo =
        CounterpartHours.builder()
            .requiredHours(requiredHours != null ? requiredHours : BigDecimal.ZERO)
            .completedHours(completedHours != null ? completedHours : BigDecimal.ZERO)
            .concluded(concluded != null ? concluded : Boolean.FALSE)
            .build();
    vo.collectValidationProblems();
    return vo;
  }

  private void collectValidationProblems() {
    if (requiredHours == null || requiredHours.compareTo(BigDecimal.ZERO) <= 0) {
      addFieldError(AcademicFieldErrorCodes.INVALID_HOURS_BLANK);
    }
    if (completedHours == null || completedHours.compareTo(BigDecimal.ZERO) < 0) {
      addFieldError(AcademicFieldErrorCodes.INVALID_COMPLETED_HOURS_NEGATIVE);
    }
    if (requiredHours != null
        && completedHours != null
        && completedHours.compareTo(requiredHours) > 0) {
      addFieldError(AcademicFieldErrorCodes.INVALID_COMPLETED_HOURS_EXCEEDS);
    }
  }
}
