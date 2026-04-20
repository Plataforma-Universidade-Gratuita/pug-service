package br.org.catolicasc.pug.partner.domain.enums;

import br.org.catolicasc.pug.shared.domain.enums.GenericCodes;
import lombok.Getter;

/**
 * Enumeration of high-level domain error codes specific to the Partner context.
 *
 * <p>This enum implements {@link GenericCodes} to map business rule violations and resource state
 * conflicts directly to localized messages in the application's resource bundles. Unlike
 * field-level validations, these codes represent aggregate-level or cross-cutting system states
 * (e.g., duplication, structural integrity, or missing records).
 */
@Getter
public enum PartnerErrorCodes implements GenericCodes {

  /**
   * Indicates an attempt to create or update a partner entity using a CNPJ that is already
   * registered to another organization in the system.
   */
  ENTITY_ALREADY_EXISTS("error.domain.partner.entity.exists"),

  /**
   * Indicates an attempt to physically delete a partner entity that already has registered
   * projects, violating strict historical data retention policies.
   */
  ENTITY_HAS_PROJECTS("error.domain.partner.entity.has.projects"),

  /**
   * Indicates that a requested partner entity could not be located in the underlying data store by
   * its unique identifier or CNPJ.
   */
  ENTITY_NOT_FOUND("error.domain.partner.entity.not.found"),

  /**
   * Indicates an attempt to assign staff privileges to an account that is already actively assigned
   * to that exact partner entity.
   */
  STAFF_ALREADY_EXISTS("error.domain.partner.staff.exists"),

  /**
   * Indicates an attempt to assign an account as Staff to a partner organization, but that account
   * is already assigned to a completely different organization. (An account may only serve one
   * partner entity at a time).
   */
  STAFF_ASSIGNED_TO_OTHER_ENTITY("error.domain.partner.staff.assigned.to.other.entity"),

  /**
   * Indicates an attempt to physically delete a staff member who has already validated student
   * attendances, which would compromise the audit trail.
   */
  STAFF_HAS_ATTENDANCES("error.domain.partner.staff.has.attendances"),

  /**
   * Indicates an attempt to physically delete a staff member who is listed as the creator of one or
   * more projects, which would leave those projects orphaned.
   */
  STAFF_HAS_PROJECTS("error.domain.partner.staff.has.projects"),

  /**
   * Indicates that a requested staff assignment could not be located in the underlying data store
   * by its linked account ID.
   */
  STAFF_NOT_FOUND("error.domain.partner.staff.not.found");

  /** The property key used to resolve the localized error message in the resource bundles. */
  private final String bundleKey;

  /**
   * Constructs the {@code PartnerErrorCodes} enum.
   *
   * @param bundleKey the unique i18n key mapping to the application's resource bundles (e.g.,
   *     {@code messages_en_US.properties})
   */
  PartnerErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
