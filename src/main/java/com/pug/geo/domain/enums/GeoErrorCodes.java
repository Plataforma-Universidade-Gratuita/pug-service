package com.pug.geo.domain.enums;

import com.pug.shared.domain.enums.GenericErrorCodes;
import lombok.Getter;

/**
 * Enum representing error codes specific to the geographic domain.
 *
 * <p>Each error code is associated with a specific validation failure scenario
 * and has a {@code bundleKey} that results into a located error message</p>
 */
@Getter
public enum GeoErrorCodes implements GenericErrorCodes {
  INVALID_IBGE_CODE("error.domain.geo.ibge-code.invalid"),
  INVALID_CITY_NAME_BLANK("error.domain.geo.city-name.blank"),
  INVALID_CITY_NAME_TOOLONG("error.domain.geo.city-name.toolong"),
  CITY_NOT_FOUND("error.domain.geo.city.notfound"),
  CITY_ALREADY_EXISTS("error.domain.geo.city.alreadyexists"),
  CITY_REFERENCED_BY_ENTITY("error.domain.geo.city.referencedbyentity");

  private final String bundleKey;

  GeoErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
