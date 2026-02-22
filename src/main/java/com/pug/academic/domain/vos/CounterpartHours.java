package com.pug.academic.domain.vos;

import com.pug.shared.domain.DomainError;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.math.BigDecimal;

/**
 * Value Object representing Counterpart Hours. Extends DomainError to allow deferred validation.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class CounterpartHours extends DomainError {
  BigDecimal requiredHours;
  Boolean concluded;

  @Builder(toBuilder = true)
  private CounterpartHours(BigDecimal requiredHours, Boolean concluded) {
    this.requiredHours = requiredHours;
    this.concluded = concluded;
  }

  /**
   * Factory method to create a new CounterpartHours.
   *
   * @param requiredHours the required hours
   * @return The CounterpartHours instance (which may contain errors)
   */
  public static CounterpartHours factory(BigDecimal requiredHours, Boolean concluded) {
    CounterpartHours vo =
            CounterpartHours.builder()
                    .requiredHours(requiredHours)
                    .concluded(concluded != null ? concluded : Boolean.FALSE)
                    .build();
    vo.collectValidationProblems();
    return vo;
  }

  /**
   * Validates the hours.
   */
  private void collectValidationProblems() {
    validateBigDecimalField(requiredHours, "requiredHours", false, false);
  }
}
