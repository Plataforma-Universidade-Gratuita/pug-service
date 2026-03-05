package com.pug.shared.domain.enums;

import lombok.Getter;

/**
 * Enumeration of common, high-level shared error codes used across the entire application.
 *
 * <p>These codes implement {@link GenericCodes} and map to the generic, cross-cutting error states
 * of the system (e.g., internal errors, data integrity issues, or global validation failures). They
 * are not tied to any specific domain and do not contain granular field-level details.
 */
@Getter
public enum SharedErrorCodes implements GenericCodes {

  /** Indicates a generic violation of a high-level business rule. */
  BUSINESS_RULE_ERROR("error.business.rule.violation"),

  /** Indicates a structural database violation, such as a foreign key constraint failure. */
  DATA_INTEGRITY_ERROR("error.data.integrity"),

  /** Indicates an attempt to create or update a resource that violates a unique constraint. */
  DUPLICATED_RESOURCE_ERROR("error.duplicated.resource"),

  /** Indicates an unexpected or unhandled exception within the system (HTTP 500). */
  INTERNAL_ERROR("error.internal"),

  /** Indicates that a requested entity or aggregate root could not be found. */
  RESOURCE_NOT_FOUND_ERROR("error.resource.not.found"),

  /** Indicates a general request payload validation failure. */
  VALIDATION_ERROR("error.validation");

  /** The property key used to resolve the localized error message in the resource bundles. */
  private final String bundleKey;

  /**
   * Constructs the {@code SharedErrorCodes} enum.
   *
   * @param bundleKey the unique i18n key mapping to the application's resource bundles
   */
  SharedErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
