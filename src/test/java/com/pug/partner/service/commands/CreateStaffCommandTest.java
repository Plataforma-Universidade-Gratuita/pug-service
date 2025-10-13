package com.pug.partner.service.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CreateStaffCommandTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void beanValidationAnnotationsWork() {
    var bad = new CreateStaffCommand(null, null, "not-an-email");
    var props = validator.validate(bad).stream().map(v -> v.getPropertyPath().toString()).toList();
    assertTrue(props.contains("userId"));
    assertTrue(props.contains("entityId"));
    assertTrue(props.contains("email"));

    var ok = new CreateStaffCommand(UUID.randomUUID(), UUID.randomUUID(), "a@b.com");
    assertTrue(validator.validate(ok).isEmpty());
  }
}
