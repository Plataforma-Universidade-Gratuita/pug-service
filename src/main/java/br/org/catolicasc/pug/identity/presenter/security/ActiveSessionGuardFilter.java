/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.presenter.security;

import br.org.catolicasc.pug.identity.infra.persistence.impl.RefreshTokenRepositoryImpl;
import br.org.catolicasc.pug.identity.service.utils.ExceptionHelper;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import java.util.UUID;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * Rejects authenticated requests whose backing refresh-token session has already been revoked.
 *
 * <p>This keeps logout and logout-all effective immediately instead of waiting for the short-lived
 * access token to expire naturally.
 */
@Provider
@ApplicationScoped
@Priority(Priorities.AUTHORIZATION + 1)
public class ActiveSessionGuardFilter implements ContainerRequestFilter {

  @Inject SecurityIdentity identity;

  @Inject RefreshTokenRepositoryImpl refreshTokenRepository;

  /** {@inheritDoc} */
  @Override
  public void filter(ContainerRequestContext requestContext) {
    if (identity == null || identity.isAnonymous()) {
      return;
    }

    String path = PasswordSetupGuardFilter.normalizePath(requestContext.getUriInfo().getPath());
    if (path.equals("v1/auth") || path.startsWith("v1/auth/")) {
      return;
    }

    Object principal = identity.getPrincipal();
    if (!(principal instanceof JsonWebToken jwt)) {
      return;
    }

    UUID accountId = parseUuidClaim(jwt.getClaim("accountId"));
    if (accountId == null) {
      return;
    }

    UUID refreshTokenId = parseUuidClaim(jwt.getClaim("refreshTokenId"));
    if (refreshTokenId != null) {
      if (!refreshTokenRepository.existsActiveByIdAndAccountId(refreshTokenId, accountId)) {
        throw ExceptionHelper.unauthorized();
      }
      return;
    }

    if (!refreshTokenRepository.existsActiveByAccountId(accountId)) {
      throw ExceptionHelper.unauthorized();
    }
  }

  private UUID parseUuidClaim(Object rawClaim) {
    if (rawClaim == null) {
      return null;
    }

    try {
      return UUID.fromString(String.valueOf(rawClaim));
    } catch (IllegalArgumentException e) {
      throw ExceptionHelper.unauthorized();
    }
  }
}
