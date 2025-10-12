package com.pug.identity.service.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class UpdateUserCommandTest {
  static Validator v;

  @BeforeAll
  static void init() {
    v = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void validWithNameOnlyPasses() {
    assertTrue(v.validate(new UpdateUserCommand(UUID.randomUUID(), null, "Bob")).isEmpty());
  }

  @Test
  void nullIdFails() {
    assertFalse(v.validate(new UpdateUserCommand(null, null, "Bob")).isEmpty());
  }

  @Test
  void tooLongNameFails() {
    assertFalse(
        v.validate(new UpdateUserCommand(UUID.randomUUID(), null, "x".repeat(151))).isEmpty());
  }
}
