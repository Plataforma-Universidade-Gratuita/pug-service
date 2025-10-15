package com.pug.shared.domain.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EmailBasicValidatorTest {

  static Validator validator;

  record Dto(@EmailBasic String email, @NotBlank @EmailBasic String requiredEmail) {}

  @BeforeAll
  static void setup() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void directValidatorCases() {
    var v = new EmailBasicValidator();
    assertTrue(v.isValid(null, null));
    assertTrue(v.isValid("", null));
    assertTrue(v.isValid("user.name+tag@example.com", null));

    assertFalse(v.isValid("a@b", null));
    assertFalse(v.isValid("abc", null));
    assertFalse(v.isValid("@host", null));
    assertFalse(v.isValid("user@", null));
  }

  @Test
  void beanValidationIntegration_optionalAllowsNull_invalidFails() {
    var ok = new Dto(null, "x@y");
    assertFalse(validator.validate(ok).isEmpty());

    var bad = new Dto("bad", "bad");
    assertFalse(validator.validate(bad).isEmpty());
  }
}
