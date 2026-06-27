package br.org.catolicasc.pug.shared.infra;

import br.org.catolicasc.pug.identity.service.PasswordService;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Startup observer that re-hashes local/QA seeded account passwords so they match the current
 * environment's pepper configuration.
 *
 * <p>The Flyway test seed stores recognizable placeholder password values such as {@code
 * "Admin123*"}, {@code "FormerS123*"}, and {@code "EntityS123*"}. At runtime, {@link
 * PasswordService} appends the configured pepper before hashing and verifying passwords, so a
 * single static database value cannot safely work across local and QA environments.
 *
 * <p>This observer is guarded by {@code pug.seed.passwords.enabled}. When enabled, it replaces the
 * seeded placeholder values with BCrypt hashes generated from the current environment's pepper.
 * Accounts intentionally seeded without a password remain unchanged.
 *
 * <p><strong>Security note:</strong> keep this disabled in production. It is intended only for
 * development and QA seed data.
 */
@ApplicationScoped
public class SeedPasswordSeeder {
  @Inject PasswordService passwordService;
  @Inject EntityManager em;

  @ConfigProperty(name = "pug.seed.passwords.enabled", defaultValue = "false")
  boolean enabled;

  void onStart(@Observes StartupEvent ev) {
    if (enabled) {
      rehashSeedPasswords();
    }
  }

  @Transactional
  void rehashSeedPasswords() {
    rehashLike("admin.%@pug.test", "Admin123*");
    rehashLike("student.%@pug.test", "FormerS123*");
    rehashLike("staff.%@pug.test", "EntityS123*");
  }

  void rehashLike(String emailPattern, String rawPassword) {
    em.createNativeQuery(
            """
        UPDATE accounts
           SET password_hash = :hash
         WHERE email LIKE :emailPattern
           AND password_hash IS NOT NULL
        """)
        .setParameter("hash", passwordService.hash(rawPassword))
        .setParameter("emailPattern", emailPattern)
        .executeUpdate();
  }
}
