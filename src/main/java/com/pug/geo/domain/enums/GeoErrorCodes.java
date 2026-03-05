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
   * Indicates an attempt to create or update a city using an IBGE code that is already registered
   * to another city in the system.
   */
  CITY_ALREADY_EXISTS("error.domain.geo.city.already.exists"),

  /**
   * Indicates an attempt to modify or delete a protected system default city (e.g., fixed
   * university campus locations like Jaraguá do Sul or Joinville).
   */
  CITY_IS_DEFAULT("error.domain.geo.city.is.default"),

  /**
   * Indicates that a requested city could not be located in the underlying data store by its ID or
   * IBGE code.
   */
  CITY_NOT_FOUND("error.domain.geo.city.not.found"),

  /**
   * Indicates an attempt to delete a city that is currently assigned to one or more active Partner
   * entities, violating data retention policies.
   */
  CITY_STILL_REFERENCED_BY_ENTITY("error.domain.geo.city.referenced");

  /** The property key used to resolve the localized error message in the resource bundles. */
  private final String bundleKey;

  /**
   * Constructs the {@code GeoErrorCodes} enum.
   *
   * @param bundleKey the unique i18n key mapping to the application's resource bundles (e.g.,
   *     {@code messages_en_US.properties})
   */
  GeoErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
