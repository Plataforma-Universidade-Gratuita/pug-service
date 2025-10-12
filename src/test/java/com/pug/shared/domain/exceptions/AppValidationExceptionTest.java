package com.pug.shared.domain.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AppValidationExceptionTest {
  @Test
  void carriesCodeAndMessage() {
    var ex = new AppValidationException("error.validation");
    assertEquals("error.validation", ex.code());
    assertEquals("error.validation", ex.getMessage());
    assertTrue(ex instanceof RuntimeException);
  }
}
