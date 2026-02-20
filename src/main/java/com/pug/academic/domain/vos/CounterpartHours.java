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

  @Builder(toBuilder = true)
  private CounterpartHours(BigDecimal requiredHours) {
    this.requiredHours = requiredHours;
  }

  /**
   * Factory method to create a new CounterpartHours.
   *
   * @param requiredHours the required hours
   * @return The CounterpartHours instance (which may contain errors)
   */
  public static CounterpartHours factory(BigDecimal requiredHours) {
    CounterpartHours vo =
            CounterpartHours.builder()
                    .requiredHours(requiredHours)
                    .build();
    vo.collectValidationProblems();
    return vo;
  }

  /**
   * Validates the hours.
   */
  private void collectValidationProblems() {
    validateBigDecimalField(requiredHours, "requiredHours", false, true);
  }
}
