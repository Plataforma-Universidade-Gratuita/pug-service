/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.UUID;

/**
 * Constraint validator implementation for evaluating {@link java.util.UUID} objects annotated with
 * {@link UuidV7}.
 *
 * <p>This is typically used when the framework (like Jackson or JAX-RS) has already deserialized
 * the payload into a UUID object, but we must verify that the underlying version of that UUID is
 * specifically version 7.
 */
public final class UuidV7ForUuid implements ConstraintValidator<UuidV7, UUID> {

  /** {@inheritDoc} */
  @Override
  public boolean isValid(UUID value, ConstraintValidatorContext ctx) {
    return value == null || value.version() == 7;
  }
}
