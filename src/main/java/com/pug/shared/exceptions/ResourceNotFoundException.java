package com.pug.shared.exceptions;

import com.pug.shared.domain.enums.GenericErrorCodes;

import java.util.Map;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends ApplicationException {
  /**
   * Constructs a new ResourceNotFoundException with the specified error code and details.
   *
   * @param errorCode the generic error code representing the resource not found error
   * @param details   additional details about the error
   */
  public ResourceNotFoundException(GenericErrorCodes errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }

  /**
   * Constructs a new ResourceNotFoundException with the specified error code.
   *
   * @param errorCode the generic error code representing the resource not found error
   */
  public ResourceNotFoundException(GenericErrorCodes errorCode) {
    super(errorCode);
  }
}
