package br.org.catolicasc.pug.identity.infra;

import br.org.catolicasc.pug.identity.infra.persistence.impl.RefreshTokenRepositoryImpl;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

/**
 * Scheduled job that periodically purges expired refresh tokens from the database.
 *
 * <p>This prevents the {@code refresh_tokens} table from growing unboundedly with stale entries
 * that can no longer be used. Runs once every 24 hours.
 */
@ApplicationScoped
public class ExpiredTokenCleanupJob {

  private static final Logger LOG = Logger.getLogger(ExpiredTokenCleanupJob.class);

  @Inject RefreshTokenRepositoryImpl refreshTokenRepository;

  /**
   * Deletes all refresh tokens whose {@code expiresAt} timestamp is in the past.
   *
   * <p>Scheduled to run daily. The cron expression {@code 0 0 3 * * ?} triggers at 03:00 AM every
   * day, minimizing impact on peak traffic.
   */
  @Scheduled(cron = "0 0 3 * * ?", identity = "expired-token-cleanup")
  @Transactional
  void purgeExpiredTokens() {
    long deleted = refreshTokenRepository.deleteExpired();
    if (deleted > 0) {
      LOG.infof("Expired token cleanup: removed %d stale refresh token(s)", deleted);
    }
  }
}
