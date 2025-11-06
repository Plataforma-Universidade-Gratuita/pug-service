package com.pug.shared.exceptions;

import com.pug.shared.errors.GenericErrorCodes;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Base class for domain-specific exceptions in the application. */
public abstract class DomainException extends RuntimeException {
  private final GenericErrorCodes code;
  private final Map<String, Object> details;

  /**
   * Creates a new DomainException with the specified error code.
   *
   * @param code the generic error code representing the error condition.
   */
  protected DomainException(GenericErrorCodes code) {
    super(code.toString());
    this.code = code;
    this.details = Collections.emptyMap();
  }

  /**
   * Creates a new DomainException with the specified error code and additional details.
   *
   * @param code the generic error code representing the error condition.
   * @param details a map of additional details related to the error.
   */
  protected DomainException(GenericErrorCodes code, Map<String, Object> details) {
    super(code.toString());
    this.code = code;
    this.details = details != null ? new HashMap<>(details) : Collections.emptyMap();
  }

  /**
   * Creates a new DomainException with the specified error code and cause.
   *
   * @param code the generic error code representing the error condition.
   * @param cause the underlying cause of the exception.
   */
  protected DomainException(GenericErrorCodes code, Throwable cause) {
    super(code.toString(), cause);
    this.code = code;
    this.details = Collections.emptyMap();
  }

  /**
   * Gets the generic error code associated with this exception.
   *
   * @return the generic error code.
   */
  public GenericErrorCodes code() {
    return code;
  }

  /**
   * Gets the additional details associated with this exception.
   *
   * @return an unmodifiable map of additional details.
   */
  public Map<String, Object> getDetails() {
    return Collections.unmodifiableMap(details);
  }
}
