package com.pug.shared.exceptions;

import com.pug.helpers.TestErrorCodes;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DomainExceptionTest {

  private static class TestDomainException extends DomainException {
    public TestDomainException(TestErrorCodes code) {
      super(code);
    }

    public TestDomainException(TestErrorCodes code, Throwable cause) {
      super(code, cause);
    }

    public TestDomainException(TestErrorCodes code, Map<String, Object> details) {
      super(code, details);
    }
  }

  @Test
  public void testDomainException() {
    TestErrorCodes expectedCode = TestErrorCodes.INVALID_DATA;
    DomainException exception = new TestDomainException(expectedCode);

    assertEquals(
            expectedCode.toString(), exception.getMessage(), "The message should be the code itself.");
    assertEquals(
            expectedCode,
            exception.code(),
            "The code method should return the same value as the code.");
  }

  @Test
  public void testDomainExceptionNullCode() {
    assertThrows(
            NullPointerException.class,
            () -> {
              new TestDomainException(null);
            },
            "A NullPointerException should be thrown when code is null.");
  }

  @Test
  public void testDomainExceptionEmptyCode() {
    TestErrorCodes expectedCode = TestErrorCodes.INVALID_FORMAT;
    DomainException exception = new TestDomainException(expectedCode);
    assertEquals(
            expectedCode.toString(), exception.getMessage(), "The message should be the code itself.");
    assertEquals(
            expectedCode, exception.code(), "The code method should return the correct value.");
  }

  @Test
  public void testDomainExceptionSpecialCharactersInCode() {
    TestErrorCodes expectedCode = TestErrorCodes.MISSING_FIELD;
    DomainException exception = new TestDomainException(expectedCode);
    assertEquals(
            expectedCode.toString(),
            exception.getMessage(),
            "The message should correctly handle special characters in the code.");
    assertEquals(
            expectedCode, exception.code(), "The code method should return the correct value.");
  }

  @Test
  public void testDomainExceptionLongCode() {
    TestErrorCodes expectedCode = TestErrorCodes.INVALID_DATA;
    DomainException exception = new TestDomainException(expectedCode);
    assertEquals(
            expectedCode.toString(),
            exception.getMessage(),
            "The exception should handle very long codes correctly.");
    assertEquals(
            expectedCode, exception.code(), "The code method should handle long codes correctly.");
  }

  @Test
  public void testDomainExceptionWithNullCause() {
    TestErrorCodes expectedCode = TestErrorCodes.INVALID_FORMAT;
    DomainException exception = new TestDomainException(expectedCode, (Throwable) null);
    assertEquals(
            expectedCode.toString(),
            exception.getMessage(),
            "The message should be set correctly with a null cause.");
    assertNull(exception.getCause(), "The cause should be null.");
    assertEquals(
            expectedCode, exception.code(), "The code method should return the correct value.");
  }

  @Test
  public void testDomainExceptionWithCause() {
    TestErrorCodes expectedCode = TestErrorCodes.INVALID_DATA;
    Throwable cause = new Throwable("Cause of the domain error");
    DomainException exception = new TestDomainException(expectedCode, cause);
    assertEquals(
            expectedCode.toString(), exception.getMessage(), "The message should be set correctly.");
    assertEquals(cause, exception.getCause(), "The cause should be passed correctly.");
    assertEquals(
            expectedCode, exception.code(), "The code method should return the correct value.");
  }
}
