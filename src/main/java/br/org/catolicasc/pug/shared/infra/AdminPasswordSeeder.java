package br.org.catolicasc.pug.shared.infra;

import br.org.catolicasc.pug.identity.service.PasswordService;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

/**
 * Startup observer that re-hashes the seeded System Administrator password so it matches the
 * current environment's pepper configuration.
 *
 * <p>The Flyway seed migration ({@code V014}) stores a BCrypt hash for the plain password {@code
 * "admin"} <em>without</em> a pepper. At runtime, however, the {@link PasswordService} appends a
 * secret pepper before hashing and verifying. Because the pepper differs per environment, a single
 * static hash in the migration can never satisfy all profiles.
 *
 * <p>This observer solves the problem by running on <strong>every</strong> non-test startup (both
 * DEV and NORMAL/production) and re-hashing the password with the currently configured pepper. The
 * update is idempotent — BCrypt always produces a different hash, but the net effect is that the
 * admin account can always be authenticated with the well-known password {@code "admin"}.
 *
 * <p><strong>Security note:</strong> In a real production deployment you should change this
 * password immediately after the first login, or disable this seeder entirely by removing it or
 * guarding it behind a configuration flag.
 */
@ApplicationScoped
public class AdminPasswordSeeder {

  private static final Logger LOG = Logger.getLogger(AdminPasswordSeeder.class);
  private static final String ADMIN_EMAIL = "admin@pug.com";
  private static final String ADMIN_RAW_PASSWORD = "admin";

  @Inject PasswordService passwordService;
  @Inject EntityManager em;

  void onStart(@Observes StartupEvent ev) {
    if (LaunchMode.current() != LaunchMode.TEST) {
      rehashAdminPassword();
    }
  }

  @Transactional
  void rehashAdminPassword() {
    String peppered = passwordService.hash(ADMIN_RAW_PASSWORD);
    int updated =
        em.createNativeQuery("UPDATE accounts SET password_hash = :hash WHERE email = :email")
            .setParameter("hash", peppered)
            .setParameter("email", ADMIN_EMAIL)
            .executeUpdate();

    if (updated > 0) {
      LOG.infof("Admin seeder: re-hashed password for '%s' with current pepper", ADMIN_EMAIL);
    }
  }
}
