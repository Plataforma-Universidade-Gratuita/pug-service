package com.pug.geo.domain.enums;

import com.pug.shared.domain.enums.GenericErrorCodes;
import lombok.Getter;

/**
 * Enum representing error codes specific to the geographic domain.
 *
 * <p>Each error code is associated with a specific validation failure scenario and has a {@code
 * bundleKey} that results in a localized error message.
 */
@Getter
public enum GeoErrorCodes implements GenericErrorCodes {
  INVALID_IBGE_CODE_BLANK("geo.error.ibge.code.invalid"),
  INVALID_IBGE_CODE_FORMAT("geo.error.ibge.code.format"),
  INVALID_CITY_NAME_BLANK("geo.error.city.name.blank"),
  INVALID_CITY_NAME_LENGTH("geo.error.city.name.length"),
  CITY_NOT_FOUND("geo.error.city.not.found"),
  CITY_ALREADY_EXISTS("geo.error.city.already.exists"),
  CITY_STILL_REFERENCED_BY_ENTITY("geo.error.city.still.referenced.by.entity");

  private final String bundleKey;

  GeoErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
