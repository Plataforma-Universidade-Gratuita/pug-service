package br.org.catolicasc.pug.identity.presenter.security;

import br.org.catolicasc.pug.identity.service.utils.ExceptionHelper;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * Prevents authenticated accounts without wired credentials from accessing protected application
 * endpoints.
 *
 * <p>Accounts provisioned without a password are temporarily allowed to authenticate so they can
 * complete the credential-onboarding flow. Until that happens, only authentication endpoints and
 * self-service {@code /me} endpoints remain accessible.
 */
@Provider
@ApplicationScoped
@Priority(Priorities.AUTHORIZATION)
public class PasswordSetupGuardFilter implements ContainerRequestFilter {

  @Inject SecurityIdentity identity;

  /** {@inheritDoc} */
  @Override
  public void filter(ContainerRequestContext requestContext) {
    if (identity == null || identity.isAnonymous()) {
      return;
    }

    String path = normalizePath(requestContext.getUriInfo().getPath());
    if (isAllowedWithoutPassword(path)) {
      return;
    }

    Object principal = identity.getPrincipal();
    if (!(principal instanceof JsonWebToken jwt)) {
      return;
    }

    if (isPasswordSetupRequired(jwt.getClaim("passwordWired"))) {
      throw ExceptionHelper.accountPasswordSetupRequired();
    }
  }

  static String normalizePath(String path) {
    if (path == null || path.isBlank()) {
      return "";
    }

    return path.startsWith("/") ? path.substring(1) : path;
  }

  static boolean isAllowedWithoutPassword(String normalizedPath) {
    if (normalizedPath.equals("v1/auth") || normalizedPath.startsWith("v1/auth/")) {
      return true;
    }

    return normalizedPath.equals("me") || normalizedPath.endsWith("/me");
  }

  static boolean isPasswordSetupRequired(Object passwordWiredClaim) {
    if (passwordWiredClaim instanceof Boolean wired) {
      return !wired;
    }

    if (passwordWiredClaim == null) {
      return false;
    }

    return "false".equalsIgnoreCase(String.valueOf(passwordWiredClaim));
  }
}
