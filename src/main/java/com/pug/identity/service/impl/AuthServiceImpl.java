package com.pug.identity.service.impl;

import com.pug.identity.domain.Account;
import com.pug.identity.presenter.dtos.auth.LoginRequest;
import com.pug.identity.presenter.dtos.auth.TokenResponse;
import com.pug.identity.service.AccountService;
import com.pug.identity.service.AuthService;
import com.pug.identity.service.PasswordService;
import com.pug.identity.service.utils.ExceptionHelper;
import com.pug.shared.exceptions.ResourceNotFoundException;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Set;
import org.eclipse.microprofile.config.inject.ConfigProperty;
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

  @ConfigProperty(name = "smallrye.jwt.new-token.lifespan", defaultValue = "28800")
  long lifespan;

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
}
