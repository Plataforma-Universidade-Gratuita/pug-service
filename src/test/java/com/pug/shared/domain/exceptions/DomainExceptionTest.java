package com.pug.shared.domain.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class DomainExceptionTest {

  private static class TestDomainException extends DomainException {
    public TestDomainException(String code) {
      super(code);
    }

    public TestDomainException(String code, Throwable cause) {
      super(code, cause);
    }
  }

  @Test
  public void testDomainException() {
    String expectedCode = "Some domain error occurred";
    DomainException exception = new TestDomainException(expectedCode);
    assertEquals(
        expectedCode, exception.getMessage(), "The message (code) should be correctly set.");
    assertEquals(
        expectedCode,
        exception.code(),
        "The code method should return the same value as the message.");
  }

  @Test
  public void testDomainExceptionNullCode() {
    DomainException exception = new TestDomainException(null);
    assertNull(exception.getMessage(), "The message should be null when passed null.");
    assertNull(exception.code(), "The code method should return null for null code.");
  }

  @Test
  public void testDomainExceptionEmptyCode() {
    String expectedCode = "";
    DomainException exception = new TestDomainException(expectedCode);
    assertEquals(expectedCode, exception.getMessage(), "The code should be an empty string.");
    assertEquals(expectedCode, exception.code(), "The code method should return an empty string.");
  }

  @Test
  public void testDomainExceptionSpecialCharactersInCode() {
    String expectedCode = "Error: @!#$%^&*()";
    DomainException exception = new TestDomainException(expectedCode);
    assertEquals(
        expectedCode,
        exception.getMessage(),
        "The exception should handle special characters in the code.");
    assertEquals(
        expectedCode, exception.code(), "The code method should return the correct value.");
  }

  @Test
  public void testDomainExceptionLongCode() {
    String expectedCode = "A".repeat(1000);
    DomainException exception = new TestDomainException(expectedCode);
    assertEquals(
        expectedCode, exception.getMessage(), "The exception should handle very long codes.");
    assertEquals(
        expectedCode, exception.code(), "The code method should handle long codes correctly.");
  }

  @Test
  public void testDomainExceptionWithNullCause() {
    String expectedCode = "Some domain error occurred";
    DomainException exception = new TestDomainException(expectedCode, null);
    assertEquals(
        expectedCode,
        exception.getMessage(),
        "The message (code) should be set correctly with a null cause.");
    assertNull(exception.getCause(), "The cause should be null.");
    assertEquals(
        expectedCode, exception.code(), "The code method should return the correct value.");
  }

  @Test
  public void testDomainExceptionWithCause() {
    String expectedCode = "Some domain error occurred";
    Throwable cause = new Throwable("Cause of the domain error");
    DomainException exception = new TestDomainException(expectedCode, cause);
    assertEquals(
        expectedCode, exception.getMessage(), "The message (code) should be set correctly.");
    assertEquals(cause, exception.getCause(), "The cause should be passed correctly.");
    assertEquals(
        expectedCode, exception.code(), "The code method should return the correct value.");
  }
}
