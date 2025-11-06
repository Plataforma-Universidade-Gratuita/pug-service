package com.pug.shared.exceptions;

import com.pug.shared.errors.GenericErrorCodes;

/**
 * Exception thrown when attempting to create or add a resource that already exists.
 */
public class DuplicateResourceException extends DomainException {
  /**
   * Constructor for DuplicateResourceException.
   *
   * @param code The specific error code representing the duplicate resource error.
   */
  public DuplicateResourceException(GenericErrorCodes code) {
    super(code);
  }

  /**
   * Constructor for DuplicateResourceException with a cause.
   *
   * @param code  The specific error code representing the duplicate resource error.
   * @param cause The underlying cause of the exception.
   */
  public DuplicateResourceException(GenericErrorCodes code, Throwable cause) {
    super(code, cause);
  }
}
