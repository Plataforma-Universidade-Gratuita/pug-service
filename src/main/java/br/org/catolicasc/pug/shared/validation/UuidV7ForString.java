/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.shared.validation;

import br.org.catolicasc.pug.shared.utils.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.UUID;

/**
 * Constraint validator implementation for evaluating {@link String} fields annotated with {@link
 * UuidV7}.
 *
 * <p>This validator ensures that the string can be successfully parsed into a UUID, and that the
 * resulting UUID's version is exactly 7.
 */
public final class UuidV7ForString implements ConstraintValidator<UuidV7, String> {

  /** {@inheritDoc} */
  @Override
  public boolean isValid(String value, ConstraintValidatorContext ctx) {
    if (StringUtils.isEmpty(value)) {
      return true;
    }
    try {
      return UUID.fromString(value).version() == 7;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
