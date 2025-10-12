package com.pug.identity.service.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CreateUserCommandTest {
  static Validator v;

  @BeforeAll
  static void init() {
    v = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void validPasses() {
    assertTrue(v.validate(new CreateUserCommand("93541134780", "Alice")).isEmpty());
  }

  @Test
  void blankCpfFails() {
    assertFalse(v.validate(new CreateUserCommand(" ", "Alice")).isEmpty());
  }

  @Test
  void blankNameFails() {
    assertFalse(v.validate(new CreateUserCommand("93541134780", " ")).isEmpty());
  }

  @Test
  void tooLongNameFails() {
    assertFalse(v.validate(new CreateUserCommand("93541134780", "x".repeat(151))).isEmpty());
  }
}
