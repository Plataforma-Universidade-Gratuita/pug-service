package com.pug.shared.domain.enums;

import lombok.Getter;

/**
 * Enum representing common, shared error codes for the entire application. These codes are
 * typically used for general validation failures, internal server errors, or generic constraint
 * violations that are not specific to a particular domain.
 */
@Getter
public enum SharedErrorCodes implements GenericErrorCodes {
  /* Validation Errors */
  INVALID_CREATED_AT_BLANK("error.domain.created.at.blank", "createdAt"),
  INVALID_FIELD_BLANK("error.domain.field.blank", null),
  INVALID_FIELD_LENGTH("error.domain.field.length", null),
  INVALID_FOREIGN_KEY_BLANK("error.domain.foreign.key.blank", null),
  INVALID_ID_BLANK("error.domain.id.blank", "id"),
  INVALID_UPDATED_AT_BLANK("error.domain.updated.at.blank", "updatedAt"),
  INVALID_UPDATED_AT_BEFORE_CREATED("error.domain.updated.at.before.created", "updatedAt"),
  /* Resource Errors */
  DATA_INTEGRITY_ERROR("error.data.integrity", null),
  DUPLICATED_RESOURCE_ERROR("error.duplicated.resource", null),
  INTERNAL_ERROR("error.internal", null),
  RESOURCE_NOT_FOUND_ERROR("error.resource.not.found", null),
  VALIDATION_ERROR("error.validation", null);

  private final String bundleKey;
  private final String fieldName;

  /**
   * Constructor for the SharedErrorCodes enum.
   *
   * @param bundleKey The internationalization resource key associated with the error.
   * @param fieldName The name of the field associated with the error, or null if not
   *     field-specific.
   */
  SharedErrorCodes(String bundleKey, String fieldName) {
    this.bundleKey = bundleKey;
    this.fieldName = fieldName;
  }
}
