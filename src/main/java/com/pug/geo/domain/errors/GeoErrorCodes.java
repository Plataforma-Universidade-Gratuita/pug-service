package com.pug.geo.domain.errors;

import com.pug.shared.errors.GenericErrorCodes;
import lombok.Getter;

/**
 * Enumeration of error codes related to geographical data validation.
 */
@Getter
public enum GeoErrorCodes implements GenericErrorCodes {
  INVALID_IBGE_CODE("error.domain.geo.ibge-code.invalid"),
  INVALID_CITY_NAME_BLANK("error.domain.geo.city-name.blank"),
  INVALID_CITY_NAME_TOOLONG("error.domain.geo.city-name.toolong");

  private final String bundleKey;

  GeoErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
