package com.pug.shared.exceptions;

import com.pug.shared.domain.enums.GenericErrorCodes;
import lombok.Getter;

/**
 * Exception thrown when a valid request (syntactically correct) violates a specific business rule
 * or policy of the application (e.g., "Cannot delete default city", "Date must be in future").
 * <p>
 * Maps to HTTP 422 (Unprocessable Entity).
 */
@Getter
public class BusinessRuleException extends RuntimeException {

  private final GenericErrorCodes code;
  private final String field;
  private final String value;

  /**
   * Constructs a new BusinessRuleException.
   *
   * @param code  The specific error code (e.g. CITY_IS_DEFAULT).
   * @param field The name of the field related to the rule (e.g. "id" or "ibgeCode").
   * @param value The value that caused the violation (e.g. "4205407").
   */
  public BusinessRuleException(GenericErrorCodes code, String field, String value) {
    super(code.getBundleKey());
    this.code = code;
    this.field = field;
    this.value = value;
  }
}