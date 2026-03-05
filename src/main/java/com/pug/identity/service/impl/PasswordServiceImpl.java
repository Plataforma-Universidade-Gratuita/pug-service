package com.pug.identity.service.impl;

import com.pug.identity.service.PasswordService;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Implementation of the {@link PasswordService} utilizing Elytron's Bcrypt utility.
 *
 * <p>This application-scoped bean reads a secret pepper from the application's configuration
 * properties (via MicroProfile Config). The pepper is kept strictly in memory/configuration and is
 * never stored alongside the hashes in the database.
 */
@ApplicationScoped
public class PasswordServiceImpl implements PasswordService {

  /**
   * The secret pepper string injected from the application configuration. Defaults to an empty
   * string if not explicitly configured (e.g., in some test environments).
   */
  @ConfigProperty(name = "security.password.pepper", defaultValue = "")
  String pepper;

  /** {@inheritDoc} */
  @Override
  public String hash(String raw) {
    return BcryptUtil.bcryptHash(raw + pepper);
  }

  /** {@inheritDoc} */
  @Override
  public boolean verify(String storedHash, String raw) {
    return BcryptUtil.matches(raw + pepper, storedHash);
  }
}
