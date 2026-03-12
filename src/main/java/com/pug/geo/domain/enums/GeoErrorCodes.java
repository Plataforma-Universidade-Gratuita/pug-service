package com.pug.geo.domain.enums;

import com.pug.shared.domain.enums.GenericCodes;
import lombok.Getter;

/**
 * Enumeration of high-level domain error codes specific to the Geographic (Geo) context.
 *
 * <p>This enum implements {@link GenericCodes} to map business rule violations and resource state
 * conflicts directly to localized messages in the application's resource bundles. Unlike
 * field-level validations, these codes represent aggregate-level or cross-cutting system states
 * (e.g., duplication, structural integrity, or missing records).
 */
@Getter
public enum GeoErrorCodes implements GenericCodes {

  /**
   * Indicates that a requested city could not be located in the underlying data store by its ID or
   * IBGE code.
   */
  CITY_NOT_FOUND("error.domain.geo.city.not.found");

  private final String bundleKey;

  GeoErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
