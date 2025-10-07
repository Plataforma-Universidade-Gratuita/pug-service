package com.pug.shared.dtos;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

class ReadByPatternQueryTest {
  Validator v = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void nullQueryFails() {
    var q = new ReadByPatternQuery(null, 10, 0);
    assertFalse(v.validate(q).isEmpty());
  }

  @Test
  void limitBounds() {
    assertFalse(v.validate(new ReadByPatternQuery("x", 0, 0)).isEmpty());
    assertFalse(v.validate(new ReadByPatternQuery("x", 201, 0)).isEmpty());
    assertTrue(v.validate(new ReadByPatternQuery("x", 1, 0)).isEmpty());
    assertTrue(v.validate(new ReadByPatternQuery("x", 200, 0)).isEmpty());
  }

  @Test
  void offsetMustBeNonNegative() {
    assertFalse(v.validate(new ReadByPatternQuery("x", 10, -1)).isEmpty());
    assertTrue(v.validate(new ReadByPatternQuery("x", 10, 0)).isEmpty());
    assertTrue(v.validate(new ReadByPatternQuery("x", 10, 5)).isEmpty());
  }

  @Test
  void twoArgCtorSetsNullOffset() {
    var q = new ReadByPatternQuery("x", 10);
    assertNull(q.offset());
    assertTrue(v.validate(q).isEmpty());
  }
}
