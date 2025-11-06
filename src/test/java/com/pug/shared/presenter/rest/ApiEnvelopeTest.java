package com.pug.shared.presenter.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ApiEnvelopeTest {

  @Test
  void testApiEnvelope_ok_withData() {
    String testData = "Test Data";
    ApiEnvelope<String> envelope = ApiEnvelope.ok(testData);

    assertTrue(envelope.success(), "The success flag should be true");
    assertEquals(testData, envelope.data(), "Data should match the provided value");
    assertNull(envelope.error(), "Error should be null for a successful response");
    assertNotNull(envelope.timestamp(), "Timestamp should not be null");
  }

  @Test
  void testApiEnvelope_ok_withNullData() {
    ApiEnvelope<Void> envelope = ApiEnvelope.ok(null);

    assertTrue(envelope.success(), "The success flag should be true");
    assertNull(envelope.data(), "Data should be null for an empty response");
    assertNull(envelope.error(), "Error should be null for a successful response");
    assertNotNull(envelope.timestamp(), "Timestamp should not be null");
  }

  @Test
  void testApiEnvelope_error_withApiError() {
    ApiError error =
        new ApiError("ERROR_CODE", "An error occurred", Map.of("detail", "Invalid data"));
    ApiEnvelope<Void> envelope = ApiEnvelope.error(error);

    assertFalse(envelope.success(), "The success flag should be false for an error response");
    assertNull(envelope.data(), "Data should be null for an error response");
    assertNotNull(envelope.error(), "Error should not be null");
    assertEquals("ERROR_CODE", envelope.error().code(), "Error code should match");
    assertNotNull(envelope.timestamp(), "Timestamp should not be null");
  }

  @Test
  void testApiEnvelope_error_withCodeAndMessage() {
    ApiEnvelope<Void> envelope = ApiEnvelope.error("ERROR_CODE", "An error occurred");

    assertFalse(envelope.success(), "The success flag should be false for an error response");
    assertNull(envelope.data(), "Data should be null for an error response");
    assertNotNull(envelope.error(), "Error should not be null");
    assertEquals("ERROR_CODE", envelope.error().code(), "Error code should match");
    assertEquals("An error occurred", envelope.error().message(), "Error message should match");
    assertNotNull(envelope.timestamp(), "Timestamp should not be null");
  }

  @Test
  void testApiEnvelope_error_withCodeMessageAndDetails() {
    Map<String, Object> details = Map.of("field", "username", "error", "is required");
    ApiEnvelope<Void> envelope = ApiEnvelope.error("ERROR_CODE", "An error occurred", details);

    assertFalse(envelope.success(), "The success flag should be false for an error response");
    assertNull(envelope.data(), "Data should be null for an error response");
    assertNotNull(envelope.error(), "Error should not be null");
    assertEquals("ERROR_CODE", envelope.error().code(), "Error code should match");
    assertEquals("An error occurred", envelope.error().message(), "Error message should match");
    assertEquals(details, envelope.error().details(), "Details should match the provided value");
    assertNotNull(envelope.timestamp(), "Timestamp should not be null");
  }
}
