package com.pug.shared.exceptions;

import com.pug.shared.domain.enums.GenericErrorCodes;

/**
 * Exception thrown when an entity is referenced by another entity and cannot be deleted or
 * modified.
 */
public class ReferencedEntityException extends DomainException {
  /**
   * Constructor.
   *
   * @param code the error code.
   */
  public ReferencedEntityException(GenericErrorCodes code) {
    super(code);
  }

  /**
   * Constructor.
   *
   * @param code the error code.
   * @param cause the cause.
   */
  public ReferencedEntityException(GenericErrorCodes code, Throwable cause) {
    super(code, cause);
  }
}
