package com.pug.partner.service.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

class CreatePartnerEntityCommandTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void beanValidationAnnotationsWork() {
    var cmd = new CreatePartnerEntityCommand("", "", null, "a".repeat(300));
    var props = validator.validate(cmd).stream().map(v -> v.getPropertyPath().toString()).toList();
    assertTrue(props.contains("cnpj"));
    assertTrue(props.contains("name"));
    assertTrue(props.contains("cityId"));
  }
}
