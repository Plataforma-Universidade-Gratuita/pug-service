package com.pug.shared.domain.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public final class EmailBasicValidator implements ConstraintValidator<EmailBasic, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isBlank()) return true;
    int at = value.indexOf('@');
    return at > 0 && at < value.length() - 1;
  }
}
