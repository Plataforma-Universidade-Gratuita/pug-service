package com.pug.shared.domain.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ResourceNotFoundExceptionTest {
  @Test
  void carriesCode() {
    var ex = new ResourceNotFoundException("error.not_found");
    assertEquals("error.not_found", ex.code());
    assertTrue(ex instanceof RuntimeException);
  }
}
