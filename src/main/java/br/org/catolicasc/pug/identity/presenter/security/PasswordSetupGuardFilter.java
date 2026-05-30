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
 * complete the credential-onboarding flow. Until that happens, only the authentication endpoints
 * remain accessible.
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

    String path = requestContext.getUriInfo().getPath();
    if (path != null && path.startsWith("/v1/auth")) {
      return;
    }

    Object principal = identity.getPrincipal();
    if (!(principal instanceof JsonWebToken jwt)) {
      return;
    }

    Object passwordWired = jwt.getClaim("passwordWired");
    if (Boolean.FALSE.equals(passwordWired)) {
      throw ExceptionHelper.accountPasswordSetupRequired();
    }
  }
}
