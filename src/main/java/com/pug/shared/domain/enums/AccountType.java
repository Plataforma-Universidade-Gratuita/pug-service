package com.pug.shared.domain.enums;

import lombok.Getter;

/** Enumeration representing different types of user accounts. */
@Getter
public enum AccountType {
  ADMIN("shared.account.type.admin"),
  PARTNER("shared.account.type.partner"),
  STUDENT("shared.account.type.student");

  private final String bundleKey;

  /**
   * Constructor for the AccountType enum.
   *
   * @param bundleKey The internationalization resource key associated with the account type.
   */
  AccountType(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
