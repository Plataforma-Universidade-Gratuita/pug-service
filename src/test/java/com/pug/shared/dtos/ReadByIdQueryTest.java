package com.pug.shared.dtos;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

class ReadByIdQueryTest {
  Validator v = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void nullIdFails() {
    var q = new ReadByIdQuery(null);
    assertFalse(v.validate(q).isEmpty());
  }

  @Test
  void validIdPasses() {
    var q = new ReadByIdQuery(java.util.UUID.randomUUID());
    assertTrue(v.validate(q).isEmpty());
  }
}
