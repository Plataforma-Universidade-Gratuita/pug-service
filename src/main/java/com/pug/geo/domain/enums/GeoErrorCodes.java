package com.pug.geo.domain.enums;

import com.pug.shared.domain.enums.GenericErrorCodes;
import lombok.Getter;

/** Enumeration of error codes related to geographical operations. */
@Getter
public enum GeoErrorCodes implements GenericErrorCodes {
  INVALID_CITY_ID_BLANK("error.domain.geo.city-id.blank", "id"),
  INVALID_CITY_NAME_BLANK("error.domain.geo.city-name.blank", "name"),
  INVALID_CITY_NAME_LENGTH("error.domain.geo.city-name.toolong", "name"),
  INVALID_IBGE_CODE_BLANK("error.domain.geo.ibge-code.blank", "ibgeCode"),
  INVALID_IBGE_CODE_FORMAT("error.domain.geo.ibge-code.invalid", "ibgeCode"),

  CITY_NOT_FOUND("error.domain.geo.city.notfound", null),
  CITY_ALREADY_EXISTS("error.domain.geo.city.alreadyexists", null),
  CITY_STILL_REFERENCED_BY_ENTITY("error.domain.geo.city.referenced", null);

  private final String bundleKey;
  private final String fieldName;

  /**
   * Constructor for GeoErrorCodes enum.
   *
   * @param bundleKey the key for the error message in the resource bundle
   * @param fieldName the name of the field associated with the error, if applicable
   */
  GeoErrorCodes(String bundleKey, String fieldName) {
    this.bundleKey = bundleKey;
    this.fieldName = fieldName;
  }
}
