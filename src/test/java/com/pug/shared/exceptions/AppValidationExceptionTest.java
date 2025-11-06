package com.pug.shared.exceptions;

import com.pug.helpers.TestErrorCodes;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppValidationExceptionTest {

  @Test
  public void testAppValidationExceptionWithCode() {
    TestErrorCodes expectedCode = TestErrorCodes.INVALID_DATA;

    AppValidationException exception = new AppValidationException(expectedCode);

    assertEquals(expectedCode, exception.code());
  }

  @Test
  public void testAppValidationExceptionWithDetails() {
    TestErrorCodes expectedCode = TestErrorCodes.MISSING_FIELD;

    Map<String, Object> details = new HashMap<>();
    details.put("field", "username");
    details.put("message", "Field 'username' is required");

    AppValidationException exception = new AppValidationException(expectedCode, details);

    assertEquals(expectedCode, exception.code());

    assertEquals("username", exception.getDetails().get("field"));
    assertEquals("Field 'username' is required", exception.getDetails().get("message"));
  }

  @Test
  public void testAppValidationExceptionWithNullDetails() {
    TestErrorCodes expectedCode = TestErrorCodes.INVALID_FORMAT;

    AppValidationException exception =
            new AppValidationException(expectedCode, (Map<String, Object>) null);

    assertEquals(expectedCode, exception.code());

    assertTrue(exception.getDetails().isEmpty());
  }

  @Test
  public void testAppValidationExceptionWithCause() {
    TestErrorCodes expectedCode = TestErrorCodes.INVALID_FORMAT;
    Throwable cause = new IllegalArgumentException("Invalid format");

    AppValidationException exception = new AppValidationException(expectedCode, cause);

    assertEquals(expectedCode, exception.code());

    assertEquals(cause, exception.getCause());
  }
}
