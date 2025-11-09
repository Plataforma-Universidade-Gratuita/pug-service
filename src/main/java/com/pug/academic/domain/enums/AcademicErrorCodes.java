package com.pug.academic.domain.enums;

import com.pug.shared.errors.GenericErrorCodes;
import lombok.Getter;

/** Enum representing error codes specific to the academic domain. */
@Getter
public enum AcademicErrorCodes implements GenericErrorCodes {
  PLACEHOLDER("academic.error.placeholder");

  private final String bundleKey;

  AcademicErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
