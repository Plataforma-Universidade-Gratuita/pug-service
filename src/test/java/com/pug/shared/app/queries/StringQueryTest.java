package com.pug.shared.app.queries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class StringQueryTest {

  private static Validator validator;

  @BeforeAll
  static void setup() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void validNotBlankPasses() {
    Set<ConstraintViolation<StringQuery>> v = validator.validate(new StringQuery("abc"));
    assertEquals(0, v.size());
  }

  @Test
  void blankFails() {
    Set<ConstraintViolation<StringQuery>> v = validator.validate(new StringQuery(" "));
    assertTrue(v.stream().anyMatch(cv -> cv.getPropertyPath().toString().equals("value")));
  }

  @Test
  void nullFails() {
    Set<ConstraintViolation<StringQuery>> v = validator.validate(new StringQuery(null));
    assertTrue(v.stream().anyMatch(cv -> cv.getPropertyPath().toString().equals("value")));
  }
}
