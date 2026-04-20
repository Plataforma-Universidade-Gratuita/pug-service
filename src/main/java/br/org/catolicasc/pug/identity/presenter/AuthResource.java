package br.org.catolicasc.pug.identity.presenter;

import br.org.catolicasc.pug.identity.presenter.dtos.auth.LoginRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.TokenResponse;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST API Resource controller for handling authentication operations.
 *
 * <p>This class exposes public endpoints (unprotected by JWT) allowing users to authenticate and
 * receive their access tokens.
 */
@ApplicationScoped
@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

  @Inject AuthService authService;

  /**
   * Authenticates a user and generates a JSON Web Token (JWT).
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
}
