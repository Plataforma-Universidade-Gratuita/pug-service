package com.pug.shared.domain.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class AppValidationExceptionTest {

  @Test
  public void testAppValidationException() {
    String expectedMessage = "Invalid data provided";
    AppValidationException exception = new AppValidationException(expectedMessage);
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  public void testAppValidationExceptionNullMessage() {
    AppValidationException exception = new AppValidationException(null);
    assertNull(exception.getMessage(), "Message should be null when passed null");
  }

  @Test
  public void testAppValidationExceptionEmptyMessage() {
    String expectedMessage = "";
    AppValidationException exception = new AppValidationException(expectedMessage);
    assertEquals(expectedMessage, exception.getMessage(), "Message should be an empty string");
  }

  @Test
  public void testAppValidationExceptionSpecialCharactersInMessage() {
    String expectedMessage = "Error: @!#$%^&*()";
    AppValidationException exception = new AppValidationException(expectedMessage);
    assertEquals(
        expectedMessage,
        exception.getMessage(),
        "Message with special characters should be handled correctly");
  }

  @Test
  public void testAppValidationExceptionLongMessage() {
    String expectedMessage = "A".repeat(1000);
    AppValidationException exception = new AppValidationException(expectedMessage);
    assertEquals(
        expectedMessage, exception.getMessage(), "Long messages should be handled correctly");
  }
}
