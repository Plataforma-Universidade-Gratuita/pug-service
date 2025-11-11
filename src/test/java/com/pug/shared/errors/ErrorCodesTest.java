package com.pug.shared.errors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pug.shared.domain.enums.ErrorCodes;
import org.junit.jupiter.api.Test;

public class ErrorCodesTest {

  @Test
  public void testInternalErrorBundleKey() {
    ErrorCodes errorCode = ErrorCodes.INTERNAL_ERROR;

    String bundleKey = errorCode.getBundleKey();

    assertEquals(
        "error.internal",
        bundleKey,
        "The bundleKey for INTERNAL_ERROR should be 'error.internal'.");
  }

  @Test
  public void testValidationErrorBundleKey() {
    ErrorCodes errorCode = ErrorCodes.VALIDATION_ERROR;

    String bundleKey = errorCode.getBundleKey();

    assertEquals(
        "error.validation",
        bundleKey,
        "The bundleKey for VALIDATION_ERROR should be 'error.validation'.");
  }

  @Test
  public void testEnumToString() {
    ErrorCodes errorCode = ErrorCodes.INTERNAL_ERROR;

    String name = errorCode.toString();

    assertEquals("INTERNAL_ERROR", name, "The toString() method should return 'INTERNAL_ERROR'.");
  }

  @Test
  public void testEnumToStringForValidationError() {
    ErrorCodes errorCode = ErrorCodes.VALIDATION_ERROR;

    String name = errorCode.toString();

    assertEquals(
        "VALIDATION_ERROR", name, "The toString() method should return 'VALIDATION_ERROR'.");
  }
}
