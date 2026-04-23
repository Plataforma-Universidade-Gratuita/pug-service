package br.org.catolicasc.pug.identity.infra.persistence.impl;

import br.org.catolicasc.pug.identity.infra.persistence.RefreshTokenEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Panache-based repository for managing {@link RefreshTokenEntity} persistence operations.
 *
 * <p>Provides lookup by token hash, deletion by hash, deletion by account, and cleanup of expired
 * tokens.
 */
@ApplicationScoped
public class RefreshTokenRepositoryImpl implements PanacheRepositoryBase<RefreshTokenEntity, UUID> {

  /**
   * Finds a refresh token entity by its SHA-256 hash.
   *
   * @param tokenHash the SHA-256 hex digest of the raw refresh token
   * @return an {@link Optional} containing the entity if found
   */
  public Optional<RefreshTokenEntity> findByTokenHash(String tokenHash) {
    return find("tokenHash", tokenHash).firstResultOptional();
  }

  /**
   * Deletes a specific refresh token identified by its hash.
   *
   * @param tokenHash the SHA-256 hex digest of the raw refresh token
   * @return the number of deleted rows (0 or 1)
   */
  @Transactional
  public long deleteByTokenHash(String tokenHash) {
    return delete("tokenHash", tokenHash);
  }

  /**
   * Deletes all refresh tokens belonging to a specific account.
   *
   * @param accountId the account UUID whose tokens should be revoked
   * @return the number of deleted rows
   */
  @Transactional
  public long deleteByAccountId(UUID accountId) {
    return delete("account.id", accountId);
  }

  /**
   * Removes all expired refresh tokens from the database.
   *
   * @return the number of deleted rows
   */
  @Transactional
  public long deleteExpired() {
    return delete("expiresAt < ?1", OffsetDateTime.now());
  }
}
