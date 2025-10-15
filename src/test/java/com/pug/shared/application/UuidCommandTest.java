package com.pug.shared.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class UuidCommandTest {
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
  void validUuidPasses() {
    assertTrue(validator.validate(new UuidCommand(UUID.randomUUID())).isEmpty());
  }

  @Test
  void nullUuidRejected() {
    assertFalse(validator.validate(new UuidCommand(null)).isEmpty());
  }
}
