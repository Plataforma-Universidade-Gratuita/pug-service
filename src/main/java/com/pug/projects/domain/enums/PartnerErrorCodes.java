package com.pug.projects.domain.enums;

import com.pug.shared.errors.GenericErrorCodes;
import lombok.Getter;

/** Enum representing error codes specific to the partner domain. */
@Getter
public enum PartnerErrorCodes implements GenericErrorCodes {
  PLACEHOLDER("partner.error.placeholder");

  private final String bundleKey;

  PartnerErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
