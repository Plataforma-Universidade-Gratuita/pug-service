package com.pug.helpers;

import com.pug.shared.domain.enums.GenericErrorCodes;
import lombok.Getter;

@Getter
public enum TestErrorCodes implements GenericErrorCodes {
  INVALID_DATA("error.test.invalid_data"),
  MISSING_FIELD("error.test.missing_field"),
  INVALID_FORMAT("error.test.invalid_format");

  private final String bundleKey;

  TestErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
