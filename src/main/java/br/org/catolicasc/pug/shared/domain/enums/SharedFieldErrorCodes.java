package br.org.catolicasc.pug.shared.domain.enums;

import br.org.catolicasc.pug.shared.domain.DomainError;
import lombok.Getter;

/**
 * Enumeration of common, field-specific validation error codes used across the application.
 *
 * <p>These codes implement {@link GenericFieldErrorCodes} and are typically accumulated inside
 * {@link DomainError} instances. They represent standard constraints applied to generic entity
 * properties (like IDs, names, or audit timestamps) that are shared among multiple different
 * domains.
 */
@Getter
public enum SharedFieldErrorCodes implements GenericFieldErrorCodes {
  /** Indicates that the audit information (auditInfo) was null or unassigned. */
  INVALID_AUDIT_INFO_BLANK("error.domain.audit.info.blank", "auditInfo"),

  /** Indicates that the campus field was null or unassigned. */
  INVALID_CAMPUS_BLANK("error.domain.campus.blank", "campus"),

  /** Indicates that the creation timestamp (createdAt) was null or unassigned. */
  INVALID_CREATED_AT_BLANK("error.domain.created.at.blank", "createdAt"),

  /** Indicates that a generic name string was null, empty, or consisted only of whitespace. */
  INVALID_NAME_BLANK("error.domain.name.blank", "name"),

  /**
   * Indicates that a generic name string exceeded the maximum allowed length (e.g., 150
   * characters).
   */
  INVALID_NAME_TOO_LONG("error.domain.name.too.long", "name"),

  /** Indicates that a unique identifier (ID) was null or unassigned. */
  INVALID_ID_BLANK("error.domain.id.blank", "id"),

  /** Indicates that the last update timestamp (updatedAt) was null or unassigned. */
  INVALID_UPDATED_AT_BLANK("error.domain.updated.at.blank", "updatedAt"),

  /**
   * Indicates that the last update timestamp (updatedAt) logically precedes the creation timestamp.
   */
  INVALID_UPDATED_AT_BEFORE_CREATED_AT("error.domain.updated.at.before.created.at", "updatedAt");

  private final String bundleKey;

  private final String fieldName;

  SharedFieldErrorCodes(String bundleKey, String fieldName) {
    this.bundleKey = bundleKey;
    this.fieldName = fieldName;
  }
}
