package com.pug.shared.domain.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DuplicateResourceExceptionTest {
  @Test
  void carriesCode() {
    var ex = new DuplicateResourceException("error.duplicate");
    assertEquals("error.duplicate", ex.code());
    assertTrue(ex instanceof RuntimeException);
  }
}
