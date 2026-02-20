package com.pug.shared.exceptions;

import com.pug.shared.domain.enums.GenericErrorCodes;
import lombok.Getter;

/**
 * Exception thrown when a requested resource is not found. Does not use the 'Problem' domain
 * pattern, as this is a specific lookup failure.
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {

  private final GenericErrorCodes code;
  private final String searchField;
  private final String searchValue;

  /**
   * Constructs a new ResourceNotFoundException.
   *
   * @param code The specific error code (e.g. USER_NOT_FOUND).
   * @param searchField The name of the field used for the search (e.g. "id", "email").
   * @param searchValue The value that was not found (e.g. "uuid-123").
   */
  public ResourceNotFoundException(GenericErrorCodes code, String searchField, String searchValue) {
    super(code.getBundleKey());
    this.code = code;
    this.searchField = searchField;
    this.searchValue = searchValue;
  }
}
