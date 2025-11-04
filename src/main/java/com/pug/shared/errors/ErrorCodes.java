package com.pug.shared.errors;

import java.util.Map;

public final class ErrorCodes {
  private ErrorCodes() {}

  public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
  public static final String VALIDATION_ERROR = "VALIDATION_ERROR";

  private static final Map<String, String> MAP =
      Map.ofEntries(
          Map.entry(VALIDATION_ERROR, "error.validation"),
          Map.entry(INTERNAL_ERROR, "error.internal"));

  public static String bundleKey(String code) {
    return MAP.getOrDefault(code, code);
  }
}
