package com.pug.identity.presenter;

import com.pug.identity.infra.read.dtos.UserView;
import com.pug.identity.presenter.dtos.UserResponse;
import com.pug.identity.presenter.mappers.UserPresenter;
import com.pug.identity.service.UserReadService;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.utils.PresenterUtils;
import com.pug.shared.utils.StringUtils;
import com.pug.shared.validation.UuidV7;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * REST API Resource controller for read-only operations on Users.
 *
 * <p>This class exposes endpoints to retrieve existing user identities (names and CPFs). It acts as
 * the HTTP entry point, delegating queries to the {@link UserReadService} and adhering to CQRS
 * principles. Direct write operations for users are typically orchestrated through account-creation
 * endpoints (like Admins or Students) rather than standalone user endpoints.
 */
@Path("/identity/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
public class UserReadOnlyResource {

  @Inject UserReadService readService;

  @Context HttpHeaders headers;

  /**
   * Retrieves a specific user by their unique UUID identifier.
   *
   * @param id the unique identifier (UUIDv7) of the user
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link UserResponse}
   * @throws ResourceNotFoundException if no user with the given ID is found
   */
  @GET
  @Path("{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    UserResponse body = UserPresenter.toResponse(readService.getViewById(id), locale());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a specific user by their exact CPF.
   *
   * @param cpfRaw the raw 11-digit numeric CPF string
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link UserResponse}
   * @throws AppValidationException if the provided CPF is malformed
   * @throws ResourceNotFoundException if no user with the given CPF is found
   */
  @GET
  @Path("by-cpf/{cpf}")
  public Response getByCpf(@PathParam("cpf") @NotNull String cpfRaw) {
    UserResponse body = UserPresenter.toResponse(readService.getViewByCpf(cpfRaw), locale());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a collection of users.
   *
   * <p>If the optional {@code q} parameter is provided, it executes a full-text search against the
   * users' names. If omitted, it returns an unfiltered list of all users.
   *
   * @param query the optional search query string used to filter by name
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     UserResponse}
   */
  @GET
  public Response list(@QueryParam("q") String query) {
    List<UserView> views;

    if (StringUtils.isNotEmpty(query)) {
      views = readService.search(query);
    } else {
      views = readService.listViews();
    }

    List<UserResponse> list =
        views.stream().map(v -> UserPresenter.toResponse(v, locale())).toList();

    return Response.ok(ApiEnvelope.ok(list)).build();
  }

  /** Helper method to determine the preferred locale from the incoming request headers. */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
