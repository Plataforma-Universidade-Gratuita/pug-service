/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.service.impl;

import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.infra.persistence.AccountEntity;
import br.org.catolicasc.pug.identity.infra.persistence.RefreshTokenEntity;
import br.org.catolicasc.pug.identity.infra.persistence.impl.RefreshTokenRepositoryImpl;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.CredentialsRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.LoginRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.LogoutRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.RefreshRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.TokenResponse;
import br.org.catolicasc.pug.identity.service.AccountsService;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.identity.service.PasswordService;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountUpdateCommand;
import br.org.catolicasc.pug.identity.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.Set;
import java.util.UUID;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link AuthService} utilizing SmallRye JWT for short-lived access tokens
 * and database-persisted opaque refresh tokens for session management.
 *
 * <p>This application-scoped bean verifies credentials via the AccountsService, signs a new JWT
 * containing the user's role and identifiers as claims, and manages refresh token lifecycle
 * (creation, validation, revocation).
 */
@ApplicationScoped
public class AuthServiceImpl implements AuthService {

  private static final Logger LOG = Logger.getLogger(AuthServiceImpl.class);

  @Inject AccountsService accountService;

  @Inject PasswordService passwordService;

  @Inject SecurityIdentity identity;

  @Inject RefreshTokenRepositoryImpl refreshTokenRepository;

  @Inject EntityManager em;

  @ConfigProperty(name = "pug.auth.access-token.lifespan", defaultValue = "900")
  long accessTokenLifespan;

  @ConfigProperty(name = "pug.auth.refresh-token.lifespan", defaultValue = "604800")
  long refreshTokenLifespan;

  /** {@inheritDoc} */
  @Override
  public UUID getCurrentAccountId() {
    String accountId = getRequiredClaim("accountId");
    return UUID.fromString(accountId);
  }

  /** {@inheritDoc} */
  @Override
  public AccountType getCurrentAccountType() {
    if (identity == null || identity.isAnonymous()) {
      throw ExceptionHelper.unauthorized();
    }

    Object principal = identity.getPrincipal();
    if (!(principal instanceof JsonWebToken jwt)) {
      throw ExceptionHelper.unauthorized();
    }

    var groups = jwt.getGroups();
    if (groups == null || groups.isEmpty()) {
      throw ExceptionHelper.unauthorized();
    }

    String rawType = groups.iterator().next();
    try {
      return AccountType.valueOf(rawType);
    } catch (IllegalArgumentException e) {
      throw ExceptionHelper.unauthorized();
    }
  }

  /** {@inheritDoc} */
  @Override
  public UUID getCurrentUserId() {
    String userId = getRequiredClaim("userId");
    return UUID.fromString(userId);
  }

  private String getRequiredClaim(String claimName) {
    if (identity == null || identity.isAnonymous()) {
      throw ExceptionHelper.unauthorized();
    }

    Object principal = identity.getPrincipal();
    if (!(principal instanceof JsonWebToken jwt)) {
      throw ExceptionHelper.unauthorized();
    }

    String claim = jwt.getClaim(claimName);
    if (claim == null) {
      throw ExceptionHelper.unauthorized();
    }

    return claim;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public TokenResponse login(LoginRequest request) {
    LOG.debugf("Attempting authentication for email: %s", request.email());

    Account account;
    try {
      account = accountService.getByEmail(request.email());
    } catch (ResourceNotFoundException e) {
      LOG.warnf("Authentication failed: Email %s not found", request.email());
      throw ExceptionHelper.unauthorized();
    }

    if (Boolean.FALSE.equals(account.getActive())) {
      LOG.warnf("Authentication failed: Account %s is deactivated", account.getId());
      throw ExceptionHelper.unauthorized();
    }

    if (passwordService.isConfigured(account.getPasswordHash())
        && !passwordService.verify(account.getPasswordHash(), request.password())) {
      LOG.warnf("Authentication failed: Invalid password for account %s", account.getId());
      throw ExceptionHelper.unauthorized();
    }

    boolean passwordWired = passwordService.isConfigured(account.getPasswordHash());
    String rawRefreshToken = UUID.randomUUID().toString();
    RefreshTokenEntity refreshToken = persistRefreshToken(account.getId(), rawRefreshToken);
    String accessToken = signAccessToken(account, passwordWired, refreshToken.getId());

    LOG.infof("Authentication successful for account %s", account.getId());
    return new TokenResponse(
        accessToken,
        rawRefreshToken,
        account.getId(),
        account.getAccountType(),
        passwordWired,
        accessTokenLifespan,
        refreshTokenLifespan);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public void wireCredentials(CredentialsRequest request) {
    Account account = accountService.getByEmail(request.email());
    passwordService.validateStrength(request.password());

    accountService.update(
        account.getId(),
        new AccountUpdateCommand(null, passwordService.hash(request.password()), null, null));
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public TokenResponse refresh(RefreshRequest request) {
    String hash = sha256(request.refreshToken());

    RefreshTokenEntity entity =
        refreshTokenRepository.findByTokenHash(hash).orElseThrow(ExceptionHelper::unauthorized);

    if (entity.getExpiresAt().isBefore(OffsetDateTime.now())) {
      refreshTokenRepository.deleteByTokenHash(hash);
      LOG.warn("Refresh token expired, deleting it");
      throw ExceptionHelper.unauthorized();
    }

    AccountEntity accountEntity = entity.getAccount();
    if (Boolean.FALSE.equals(accountEntity.getActive())) {
      LOG.warnf("Refresh failed: Account %s is deactivated", accountEntity.getId());
      throw ExceptionHelper.unauthorized();
    }

    boolean passwordWired = passwordService.isConfigured(accountEntity.getPasswordHash());
    Account account = accountService.getById(accountEntity.getId());
    String accessToken = signAccessToken(account, passwordWired, entity.getId());

    LOG.infof("Access token refreshed for account %s", account.getId());
    return new TokenResponse(
        accessToken,
        request.refreshToken(),
        account.getId(),
        account.getAccountType(),
        passwordWired,
        accessTokenLifespan,
        refreshTokenLifespan);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public void logout(LogoutRequest request) {
    String hash = sha256(request.refreshToken());
    long deleted = refreshTokenRepository.deleteByTokenHash(hash);
    if (deleted > 0) {
      LOG.info("Refresh token revoked successfully");
    } else {
      LOG.warn("Logout attempted with unknown refresh token");
    }
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public void logoutAll() {
    UUID accountId = getCurrentAccountId();
    long deleted = refreshTokenRepository.deleteByAccountId(accountId);
    LOG.infof("Revoked %d refresh token(s) for account %s", deleted, accountId);
  }

  /** {@inheritDoc} */
  @Override
  public void requireCurrentAccountNotOfType(AccountType forbidden) {
    AccountType current = getCurrentAccountType();
    if (current == forbidden) {
      throw ExceptionHelper.unauthorized();
    }
  }

  /** {@inheritDoc} */
  @Override
  public void requireCurrentAccountOfType(AccountType allowed) {
    AccountType current = getCurrentAccountType();
    if (current != allowed) {
      throw ExceptionHelper.unauthorized();
    }
  }

  private String signAccessToken(Account account, boolean isPasswordWired, UUID refreshTokenId) {
    return Jwt.upn(account.getEmail().getValue())
        .groups(Set.of(account.getAccountType().name()))
        .claim("accountId", account.getId().toString())
        .claim("userId", account.getUserId().toString())
        .claim("passwordWired", isPasswordWired)
        .claim("refreshTokenId", refreshTokenId.toString())
        .sign();
  }

  private RefreshTokenEntity persistRefreshToken(UUID accountId, String rawToken) {
    AccountEntity accountRef = em.getReference(AccountEntity.class, accountId);

    OffsetDateTime now = OffsetDateTime.now();
    RefreshTokenEntity entity =
        RefreshTokenEntity.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .account(accountRef)
            .tokenHash(sha256(rawToken))
            .expiresAt(now.plusSeconds(refreshTokenLifespan))
            .createdAt(now)
            .updatedAt(now)
            .build();

    refreshTokenRepository.persist(entity);
    return entity;
  }

  static String sha256(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(digest);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 algorithm not available", e);
    }
  }
}
