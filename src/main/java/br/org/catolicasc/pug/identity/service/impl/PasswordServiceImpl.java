/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.service.impl;

import br.org.catolicasc.pug.identity.service.PasswordService;
import br.org.catolicasc.pug.identity.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Implementation of the {@link PasswordService} utilizing Elytron's Bcrypt utility.
 *
 * <p>This application-scoped bean reads a secret pepper from the application's configuration
 * properties (via MicroProfile Config). The pepper is kept strictly in memory/configuration and is
 * never stored alongside the hashes in the database. It also centralizes the password-strength
 * policy used by onboarding and credential-reset flows.
 */
@ApplicationScoped
public class PasswordServiceImpl implements PasswordService {

  private static final String SPECIAL_CHARACTERS = "!@#$%^&*()_+-=[]{}|;:,.<>?/";

  @ConfigProperty(name = "security.password.pepper", defaultValue = "")
  String pepper;

  /** {@inheritDoc} */
  @Override
  public String hash(String raw) {
    return BcryptUtil.bcryptHash(raw + pepper);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isConfigured(String storedHash) {
    return StringUtils.isNotEmpty(storedHash);
  }

  /** {@inheritDoc} */
  @Override
  public void validateStrength(String raw) {
    if (raw == null
        || raw.length() < 8
        || raw.chars().noneMatch(Character::isUpperCase)
        || raw.chars().noneMatch(Character::isLowerCase)
        || raw.chars().noneMatch(Character::isDigit)
        || raw.chars().anyMatch(Character::isWhitespace)
        || raw.chars().noneMatch(c -> SPECIAL_CHARACTERS.indexOf(c) >= 0)) {
      throw ExceptionHelper.weakPassword();
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean verify(String storedHash, String raw) {
    if (!isConfigured(storedHash)) {
      return false;
    }
    return BcryptUtil.matches(raw + pepper, storedHash);
  }
}
