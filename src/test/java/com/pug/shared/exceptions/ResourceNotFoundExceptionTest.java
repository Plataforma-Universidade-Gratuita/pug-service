package com.pug.shared.exceptions;

import com.pug.helpers.TestErrorCodes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResourceNotFoundExceptionTest {

  @Test
  public void testResourceNotFoundExceptionWithCode() {
    TestErrorCodes expectedCode = TestErrorCodes.INVALID_DATA;

    ResourceNotFoundException exception = new ResourceNotFoundException(expectedCode);

    assertEquals(expectedCode, exception.code());
  }

  @Test
  public void testResourceNotFoundExceptionWithCause() {
    TestErrorCodes expectedCode = TestErrorCodes.INVALID_FORMAT;
    Throwable cause = new IllegalArgumentException("Invalid format");

    ResourceNotFoundException exception = new ResourceNotFoundException(expectedCode, cause);

    assertEquals(expectedCode, exception.code());
    assertEquals(cause, exception.getCause());
  }
}
