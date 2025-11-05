package com.pug.shared.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pug.helpers.TestErrorCodes;
import org.junit.jupiter.api.Test;

public class DuplicateResourceExceptionTest {

  @Test
  public void testDuplicateResourceExceptionWithCode() {
    TestErrorCodes expectedCode = TestErrorCodes.INVALID_DATA;

    DuplicateResourceException exception = new DuplicateResourceException(expectedCode);

    assertEquals(expectedCode, exception.code());
  }

  @Test
  public void testDuplicateResourceExceptionWithCause() {
    TestErrorCodes expectedCode = TestErrorCodes.INVALID_FORMAT;
    Throwable cause = new IllegalArgumentException("Invalid format");

    DuplicateResourceException exception = new DuplicateResourceException(expectedCode, cause);

    assertEquals(expectedCode, exception.code());
    assertEquals(cause, exception.getCause());
  }
}
