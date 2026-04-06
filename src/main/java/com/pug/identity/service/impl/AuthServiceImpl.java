package com.pug.identity.service.impl;

import com.pug.identity.domain.Account;
import com.pug.identity.presenter.dtos.auth.LoginRequest;
import com.pug.identity.presenter.dtos.auth.TokenResponse;
import com.pug.identity.service.AccountService;
import com.pug.identity.service.AuthService;
import com.pug.identity.service.PasswordService;
import com.pug.identity.service.utils.ExceptionHelper;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.exceptions.ResourceNotFoundException;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Set;
import java.util.UUID;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link AuthService} utilizing SmallRye JWT.
 *
 * <p>This application-scoped bean verifies credentials via the AccountService and signs a new JWT
 * containing the user's role and identifiers as claims.
 */
@ApplicationScoped
public class AuthServiceImpl implements AuthService {

  private static final Logger LOG = Logger.getLogger(AuthServiceImpl.class);

  @Inject AccountService accountService;

  @Inject PasswordService passwordService;

  @Inject SecurityIdentity identity;

  @ConfigProperty(name = "smallrye.jwt.new-token.lifespan", defaultValue = "28800")
  long lifespan;

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

  /**
   * Retrieves a required string claim from the current JWT principal.
   *
   * <p>If there is no authenticated principal, the principal is not a {@link JsonWebToken}, or the
   * claim is missing, this method throws the standardized unauthorized exception.
   *
   * @param claimName the name of the claim to resolve (e.g., {@code "accountId"}, {@code "userId"})
   * @return the claim value as a non-null {@link String}
   * @throws jakarta.ws.rs.NotAuthorizedException if the claim cannot be resolved
   */
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

    if (!passwordService.verify(account.getPasswordHash(), request.password())) {
      LOG.warnf("Authentication failed: Invalid password for account %s", account.getId());
      throw ExceptionHelper.unauthorized();
    }

    String token =
        Jwt.upn(account.getEmail().getValue())
            .groups(Set.of(account.getAccountType().name()))
            .claim("accountId", account.getId().toString())
            .claim("userId", account.getUserId().toString())
            .sign();

    LOG.infof("Authentication successful for account %s", account.getId());
    return new TokenResponse(token, account.getId(), account.getAccountType(), lifespan);
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
}
