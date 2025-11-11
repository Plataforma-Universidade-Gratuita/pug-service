package com.pug.partner.domain.enums;

import com.pug.shared.domain.enums.GenericErrorCodes;
import lombok.Getter;

/**
 * Enum representing error codes specific to the partner domain.
 *
 * <p>Each error code is associated with a specific validation failure scenario
 * and has a {@code bundleKey} that results into a located error message</p>
 */
@Getter
public enum PartnerErrorCodes implements GenericErrorCodes {
  INVALID_CNPJ("error.domain.partner.invalid.cnpj"),
  INVALID_NAME_BLANK("error.domain.partner.invalid.name.blank"),
  INVALID_NAME_TOOLONG("error.domain.partner.invalid.name.toolong"),
  INVALID_CITY("error.domain.partner.invalid.city"),
  INVALID_ADDRESS_TOOLONG("error.domain.partner.invalid.address.toolong"),
  INVALID_STAFF_USER("error.domain.partner.invalid.staff_user"),
  INVALID_STAFF_ENTITY("error.domain.partner.invalid.staff_entity"),
  ENTITY_ALREADY_EXISTS("error.domain.partner.entity.already.exists"),
  ENTITY_NOT_FOUND("error.domain.partner.entity.not.found"),
  STAFF_ALREADY_EXISTS("error.domain.partner.staff.already.exists"),
  STAFF_NOT_FOUND("error.domain.partner.staff.not.found");

  private final String bundleKey;

  PartnerErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
