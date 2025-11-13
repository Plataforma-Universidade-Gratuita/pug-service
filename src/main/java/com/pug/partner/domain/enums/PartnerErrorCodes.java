package com.pug.partner.domain.enums;

import com.pug.shared.domain.enums.GenericErrorCodes;
import lombok.Getter;

/**
 * Enum representing error codes specific to the partner domain.
 *
 * <p>Each error code is associated with a specific validation failure scenario and has a {@code
 * bundleKey} that results into a localized error message.
 */
@Getter
public enum PartnerErrorCodes implements GenericErrorCodes {
  ENTITY_ALREADY_EXISTS("partner.error.entity.exists"),
  ENTITY_NOT_FOUND("partner.error.entity.notfound"),
  INVALID_ADDRESS_LENGTH("partner.error.address.toolong"),
  INVALID_CITY_BLANK("partner.error.city.blank"),
  INVALID_CNPJ_BLANK("partner.error.cnpj.blank"),
  INVALID_CNPJ_LENGTH("partner.error.cnpj.length"),
  INVALID_CNPJ_FORMAT("partner.error.cnpj.format"),
  INVALID_NAME_BLANK("partner.error.name.blank"),
  INVALID_NAME_LENGTH("partner.error.name.toolong"),
  INVALID_STAFF_ACCOUNT_BLANK("partner.error.staff.account.blank"),
  INVALID_STAFF_ENTITY_BLANK("partner.error.staff.entity.blank");

  private final String bundleKey;

  PartnerErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
