package com.pug.partner.domain.enums;

import com.pug.shared.errors.GenericErrorCodes;
import lombok.Getter;

/** Enumeration of error codes related to partner identity validation. */
@Getter
public enum PartnerErrorCodes implements GenericErrorCodes {
  INVALID_CNPJ("error.domain.partner.invalid.cnpj"),
  INVALID_NAME_BLANK("error.domain.partner.invalid.name.blank"),
  INVALID_NAME_TOOLONG("error.domain.partner.invalid.name.toolong"),
  INVALID_CITY("error.domain.partner.invalid.city"),
  INVALID_ADDRESS_TOOLONG("error.domain.partner.invalid.address.toolong"),
  INVALID_STAFF_USER("error.domain.partner.invalid.staff_user"),
  INVALID_STAFF_ENTITY("error.domain.partner.invalid.staff_entity");

  private final String bundleKey;

  PartnerErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
