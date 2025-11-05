package com.pug.shared.errors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ErrorCodesTest {

  @Test
  public void testInternalError() {
    String bundleKey = ErrorCodes.bundleKey(ErrorCodes.INTERNAL_ERROR);
    assertEquals(
        "error.internal", bundleKey, "INTERNAL_ERROR should map to the correct bundle key");
  }

  @Test
  public void testValidationError() {
    String bundleKey = ErrorCodes.bundleKey(ErrorCodes.VALIDATION_ERROR);
    assertEquals(
        "error.validation", bundleKey, "VALIDATION_ERROR should map to the correct bundle key");
  }

  @Test
  public void testNonExistentErrorCode() {
    String nonExistentCode = "NON_EXISTENT_ERROR";
    String bundleKey = ErrorCodes.bundleKey(nonExistentCode);
    assertEquals(
        nonExistentCode, bundleKey, "Non-existent error codes should return the code itself");
  }
}
