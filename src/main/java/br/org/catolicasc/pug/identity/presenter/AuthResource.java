package br.org.catolicasc.pug.identity.presenter;

import br.org.catolicasc.pug.identity.presenter.dtos.auth.CredentialsRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.LoginRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.LogoutRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.RefreshRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.TokenResponse;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import io.quarkus.security.Authenticated;

/**
 * REST API Resource controller for handling authentication operations.
 *
 * <p>This class exposes endpoints allowing users to authenticate, refresh their access tokens,
 * wire first-time credentials, and log out by revoking refresh tokens.
 */
@ApplicationScoped
@Path("/v1/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

  @Inject AuthService authService;

  /**
   * Authenticates a user and generates an access/refresh token pair.
   *
   * @param request the validated {@link LoginRequest} containing the user's email and password
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     TokenResponse}
   */
  @POST
  @Path("/login")
  @PermitAll
  public Response login(@Valid LoginRequest request) {
    TokenResponse body = authService.login(request);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Revokes a refresh token, effectively logging the user out.
   *
   * @param request the validated {@link LogoutRequest} containing the refresh token to revoke
   * @return an HTTP 204 No Content response
   */
  @POST
  @Path("/logout")
  @PermitAll
  public Response logout(@Valid LogoutRequest request) {
    authService.logout(request);
    return Response.noContent().build();
  }

  /**
   * Revokes all refresh tokens for the currently authenticated account, logging out from all
   * devices and sessions.
   *
   * @return an HTTP 204 No Content response
   */
  @POST
  @Path("/logout-all")
  @Consumes(MediaType.WILDCARD)
  @RolesAllowed({"ADMIN", "PARTNER", "FORMER_STUDENT"})
  public Response logoutAll() {
    authService.logoutAll();
    return Response.noContent().build();
  }

    /**
     * Validates a refresh token and issues a new short-lived access token.
     *
     * @param request the validated {@link RefreshRequest} containing the opaque refresh token
     * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the new {@link
     *     TokenResponse}
     */
    @POST
    @Path("/refresh")
    @PermitAll
    public Response refresh(@Valid RefreshRequest request) {
        TokenResponse body = authService.refresh(request);
        return Response.ok(ApiEnvelope.ok(body)).build();
    }

  /**
   * Wires the password credentials for an already provisioned account.
   *
   * <p>This endpoint is intended for first-access onboarding flows where the underlying account was
   * created without an initial password hash.
   *
   * @param request the validated {@link CredentialsRequest} containing the account email and the
   *     desired raw password
   * @return an HTTP 204 No Content response
   */
  @POST
  @Path("/wire-credentials")
  @Authenticated
  public Response wireCredentials(@Valid CredentialsRequest request) {
    authService.wireCredentials(request);
    return Response.noContent().build();
  }
}
