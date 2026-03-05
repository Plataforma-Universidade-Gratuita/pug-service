package com.pug.shared.exceptions;

import com.pug.shared.domain.enums.GenericCodes;
import lombok.Getter;

/**
 * Exception thrown when a requested domain resource or aggregate root cannot be found in the
 * underlying data store or external system.
 *
 * <p>This exception abstracts away the underlying persistence mechanism (e.g., database lookup
 * failure) and translates it into a standard domain-level error. It typically maps to an HTTP 404
 * (Not Found) response, delivering a localized message indicating which entity type was not found.
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {

  /** The specific domain code representing the missing resource. */
  private final GenericCodes code;

  /**
   * Constructs a new {@code ResourceNotFoundException}.
   *
   * @param code The specific {@link GenericCodes} indicating the type of resource that was not
   *     found (e.g., {@code GeoErrorCodes.CITY_NOT_FOUND}).
   */
  public ResourceNotFoundException(GenericCodes code) {
    super(code.getBundleKey());
    this.code = code;
  }
}
