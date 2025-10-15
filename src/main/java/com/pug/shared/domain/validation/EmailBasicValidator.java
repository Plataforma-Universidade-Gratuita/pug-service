package com.pug.shared.domain.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public final class EmailBasicValidator implements ConstraintValidator<EmailBasic, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isBlank()) return true;
    int first = value.indexOf('@');
    int last = value.lastIndexOf('@');
    if (first <= 0 || first != last || last == value.length() - 1) return false;
    String domain = value.substring(last + 1);
    return domain.indexOf('.') > 0 && !domain.endsWith(".");
  }
}
