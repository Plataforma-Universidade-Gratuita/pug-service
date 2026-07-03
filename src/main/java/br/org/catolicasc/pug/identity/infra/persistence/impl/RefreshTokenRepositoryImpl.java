/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

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
   * Determines whether a specific refresh-token session is still active for the given account.
   *
   * @param refreshTokenId the refresh-token row UUID embedded in the access token
   * @param accountId the owning account UUID
   * @return {@code true} when the session exists, belongs to the account, and has not expired
   */
  public boolean existsActiveByIdAndAccountId(UUID refreshTokenId, UUID accountId) {
    return count(
            "id = ?1 and account.id = ?2 and expiresAt > ?3",
            refreshTokenId,
            accountId,
            OffsetDateTime.now())
        > 0;
  }

  /**
   * Determines whether an account still has at least one active refresh-token session.
   *
   * @param accountId the owning account UUID
   * @return {@code true} when the account has at least one unexpired refresh token
   */
  public boolean existsActiveByAccountId(UUID accountId) {
    return count("account.id = ?1 and expiresAt > ?2", accountId, OffsetDateTime.now()) > 0;
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
