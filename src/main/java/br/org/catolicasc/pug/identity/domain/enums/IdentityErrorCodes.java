package br.org.catolicasc.pug.identity.domain.enums;

import br.org.catolicasc.pug.shared.domain.enums.GenericCodes;
import lombok.Getter;

/**
 * Enumeration of high-level domain error codes specific to the Identity context.
 *
 * <p>This enum implements {@link GenericCodes} to map business rule violations and resource state
 * conflicts directly to localized messages in the application's resource bundles. Unlike
 * field-level validations, these codes represent aggregate-level or cross-cutting system states
 * (e.g., duplication, structural integrity, or missing records).
 */
@Getter
public enum IdentityErrorCodes implements GenericCodes {

  /**
   * Indicates an attempt to create an account using an email address that is already registered to
   * another account in the system.
   */
  ACCOUNT_ALREADY_EXISTS("error.domain.identity.account.already.exists"),

  /**
   * Indicates that a requested account could not be located in the underlying data store by its
   * unique identifier or email.
   */
  ACCOUNT_NOT_FOUND("error.domain.identity.account.not.found"),

  /**
   * Indicates that a requested administrator profile could not be located in the underlying data
   * store.
   */
  ADMIN_NOT_FOUND("error.domain.identity.admin.not.found"),

  /**
   * Indicates that an administrator cannot be removed because they are linked to existing projects
   * as creator.
   */
  ADMIN_HAS_PROJECTS("error.domain.identity.admin.has.projects"),

  /**
   * Indicates that the authenticated account must wire a password before it can access protected
   * application operations.
   */
  ACCOUNT_PASSWORD_SETUP_REQUIRED("error.domain.identity.account.password.setup.required"),

  /** Indicates an attempt to register a user using a CPF that is already present in the system. */
  USER_ALREADY_EXISTS("error.domain.identity.user.already.exists"),

  /**
   * Indicates that a requested user could not be located in the underlying data store by their
   * unique identifier or CPF.
   */
  USER_NOT_FOUND("error.domain.identity.user.not.found"),

  /**
   * Indicates that a provided raw password does not satisfy the platform's password-strength
   * policy.
   */
  WEAK_PASSWORD("error.domain.identity.password.weak");

  /** The property key used to resolve the localized error message in the resource bundles. */
  private final String bundleKey;

  /**
   * Constructs the {@code IdentityErrorCodes} enum.
   *
   * @param bundleKey the unique i18n key mapping to the application's resource bundles (e.g.,
   *     {@code messages_en_US.properties})
   */
  IdentityErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
