package com.pug.geo.domain.enums;

import com.pug.shared.domain.enums.GenericErrorCodes;
import lombok.Getter;

/** Enumeration of error codes related to geographical operations. */
@Getter
public enum GeoErrorCodes implements GenericErrorCodes {
  /* Validation errors */
  INVALID_IBGE_CODE_BLANK("error.domain.geo.ibge.code.blank", "ibgeCode"),
  INVALID_IBGE_CODE_FORMAT("error.domain.geo.ibge.code.invalid", "ibgeCode"),
  /* Resource errors */
  CITY_ALREADY_EXISTS("error.domain.geo.city.alreadyexists", null),
  CITY_NOT_FOUND("error.domain.geo.city.notfound", null),
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
