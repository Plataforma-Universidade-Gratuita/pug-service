package com.pug.shared.exceptions;

import com.pug.shared.domain.enums.GenericErrorCodes;
import lombok.Getter;

/**
 * Exception thrown when attempting to create or add a resource that already exists. Does not use
 * the 'Problem' domain pattern, as this is a resource-level conflict.
 */
@Getter
public class DuplicateResourceException extends RuntimeException {

  private final GenericErrorCodes code;
  private final String conflictingField;
  private final String conflictingValue;

  /**
   * Constructs a new DuplicateResourceException.
   *
   * @param code The specific error code (e.g. USER_ALREADY_EXISTS).
   * @param conflictingField The name of the field that caused the conflict (e.g. "cpf").
   * @param conflictingValue The value that already exists (e.g. "123.456.789-00").
   */
  public DuplicateResourceException(
      GenericErrorCodes code, String conflictingField, String conflictingValue) {
    super(code.getBundleKey());
    this.code = code;
    this.conflictingField = conflictingField;
    this.conflictingValue = conflictingValue;
  }
}
