package com.pug.shared.presenter.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ApiErrorTest {

  @Test
  void testApiError_withNullDetails() {
    ApiError apiError = new ApiError("ERROR_CODE", "An error occurred", null);
    assertNotNull(apiError.details(), "Details should not be null");
    assertTrue(apiError.details().isEmpty(), "Details should be an empty map");
  }

  @Test
  void testApiError_withEmptyDetails() {
    ApiError apiError = new ApiError("ERROR_CODE", "An error occurred", Map.of());
    assertNotNull(apiError.details(), "Details should not be null");
    assertTrue(apiError.details().isEmpty(), "Details should be an empty map");
  }

  @Test
  void testApiError_withNonEmptyDetails() {
    Map<String, Object> details = Map.of("key", "value");
    ApiError apiError = new ApiError("ERROR_CODE", "An error occurred", details);
    assertNotNull(apiError.details(), "Details should not be null");
    assertFalse(apiError.details().isEmpty(), "Details should not be empty");
    assertEquals(
        "value",
        apiError.details().get("key"),
        "Details should contain the correct key-value pair");
  }

  @Test
  void testApiError_ofMethod() {
    Map<String, Object> details = Map.of("key", "value");
    ApiError apiError = ApiError.of("ERROR_CODE", "An error occurred", details);
    assertNotNull(apiError, "ApiError should not be null");
    assertEquals("ERROR_CODE", apiError.code(), "Code should match");
    assertEquals("An error occurred", apiError.message(), "Message should match");
    assertFalse(apiError.details().isEmpty(), "Details should not be empty");
    assertEquals(
        "value",
        apiError.details().get("key"),
        "Details should contain the correct key-value pair");
  }
}
