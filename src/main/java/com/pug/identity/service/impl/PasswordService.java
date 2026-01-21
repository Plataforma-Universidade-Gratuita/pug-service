package com.pug.identity.service.impl;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Service for hashing and verifying passwords using bcrypt with an added pepper.
 */
@ApplicationScoped
public class PasswordService {
  @ConfigProperty(name = "security.password.pepper", defaultValue = "")
  String pepper;

  /**
   * Hashes the raw password combined with a pepper using bcrypt.
   *
   * @param raw the raw password
   * @return the bcrypt hash of the password with pepper
   */
  public String hash(String raw) {
    return BcryptUtil.bcryptHash(raw + pepper);
  }

  /**
   * Verifies a raw password against a stored bcrypt hash, considering the pepper.
   *
   * @param storedHash the stored bcrypt hash
   * @param raw        the raw password to verify
   * @return true if the password matches the hash, false otherwise
   */
  public boolean verify(String storedHash, String raw) {
    return BcryptUtil.matches(raw + pepper, storedHash);
  }
}