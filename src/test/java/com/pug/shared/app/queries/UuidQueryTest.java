// src/test/java/com/pug/shared/app/queries/UuidQueryTest.java
package com.pug.shared.app.queries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class UuidQueryTest {

  private static Validator validator;

  @BeforeAll
  static void setup() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void validUuidPasses() {
    Set<ConstraintViolation<UuidQuery>> v = validator.validate(new UuidQuery(UUID.randomUUID()));
    assertEquals(0, v.size());
  }

  @Test
  void nullFails() {
    Set<ConstraintViolation<UuidQuery>> v = validator.validate(new UuidQuery(null));
    assertTrue(v.stream().anyMatch(cv -> cv.getPropertyPath().toString().equals("id")));
  }
}
