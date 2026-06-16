package br.org.catolicasc.pug.shared.domain.enums;

import lombok.Getter;

/**
 * Enumeration representing the distinct roles or types of accounts within the platform.
 *
 * <p>Implements {@link GenericCodes} to provide internationalized (i18n) display names for each
 * account type. This allows the UI or API responses to present localized text based on the
 * application's resource bundles.
 */
@Getter
public enum AccountType implements GenericCodes {

  /**
   * System administrator account type, representing users with elevated platform-wide privileges.
   */
  ADMIN("shared.account.type.admin"),

  /**
   * Partner account type, representing staff members associated with external entities or
   * organizations.
   */
  PARTNER("shared.account.type.partner"),

  /**
   * FormerStudent account type, representing alumni participating in academic workflows and project
   * activities.
   */
  FORMER_STUDENT("shared.account.type.formerStudent");

  private final String bundleKey;

  AccountType(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
