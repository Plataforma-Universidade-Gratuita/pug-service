package com.pug.shared.exceptions;

import com.pug.shared.domain.enums.GenericErrorCodes;

import java.util.Map;

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
   * Creates a new ResourceNotFoundException with the specified error code and details.
   *
   * @param code the error code representing the resource not found error.
   * @param details additional details about the error.
   */
  public ResourceNotFoundException(GenericErrorCodes code, Map<String, Object> details) {
    super(code, details);
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
