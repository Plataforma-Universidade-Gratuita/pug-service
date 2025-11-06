package com.pug.shared.exceptions;

import com.pug.shared.errors.GenericErrorCodes;

/** Exception thrown when a requested resource is not found. */
public class ResourceNotFoundException extends DomainException {
  /**
   * Creates a new ResourceNotFoundException with the specified error code.
   *
   * @param code the error code representing the resource not found error.
   */
  public ResourceNotFoundException(GenericErrorCodes code) {
    super(code);
  }

  /**
   * Creates a new ResourceNotFoundException with the specified error code and cause.
   *
   * @param code the error code representing the resource not found error.
   * @param cause the cause of the exception.
   */
  public ResourceNotFoundException(GenericErrorCodes code, Throwable cause) {
    super(code, cause);
  }
}
