package com.pug.identity.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AdminTest {
  private static ValidatorFactory vf;
  private static Validator validator;

  @BeforeAll
  static void setup() {
    vf = Validation.buildDefaultValidatorFactory();
    validator = vf.getValidator();
  }

  @AfterAll
  static void tearDown() {
    vf.close();
  }

  @Test
  void validAdminHasNoViolations() {
    var a = new Admin(UUID.randomUUID(), Instant.now());
    assertTrue(validator.validate(a).isEmpty());
  }

  @Test
  void nullUserIdAndGrantedAtAreViolations() {
    var a1 = new Admin(null, Instant.now());
    var a2 = new Admin(UUID.randomUUID(), null);
    assertFalse(validator.validate(a1).isEmpty());
    assertFalse(validator.validate(a2).isEmpty());
  }
}
