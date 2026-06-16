package br.org.catolicasc.pug.identity.presenter;

import br.org.catolicasc.pug.identity.infra.read.dtos.UserView;
import br.org.catolicasc.pug.identity.presenter.dtos.users.UserComplexSearchRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.users.UserResponse;
import br.org.catolicasc.pug.identity.presenter.mappers.UserPresenter;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.identity.service.UsersReadService;
import br.org.catolicasc.pug.identity.service.dtos.users.UserComplexSearchCriteria;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.presenter.dtos.PageResponse;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
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
 * <p>This class exposes endpoints to retrieve existing user identities. It acts as the HTTP entry
 * point, delegating queries to the {@link UsersReadService} and adhering to CQRS principles. Direct
 * write operations for users are typically orchestrated through account-creation endpoints (like
 * Admins or Former Students) rather than standalone user endpoints.
 */
@Path("/v1/identity/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
public class UsersReadOnlyResource {

  @Inject UsersReadService readService;

  @Inject AuthService authService;

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
   * Retrieves the identity details of the currently authenticated user.
   *
   * <p>The user identifier is resolved exclusively from the JWT {@code userId} claim via {@link
   * AuthService}, ensuring that callers can only access their own user record, regardless of the
   * request parameters. This endpoint is available to any authenticated role.
   *
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link UserResponse}
   * @throws jakarta.ws.rs.NotAuthorizedException if the token is missing or does not contain the
   *     required {@code userId} claim
   */
  @GET
  @Path("me")
  @Authenticated
  public Response getMe() {
    UUID userId = authService.getCurrentUserId();
    UserResponse body = UserPresenter.toResponse(readService.getViewById(userId), locale());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves users, optionally filtered by a collection of identifiers.
   *
   * <p>When one or more {@code ids} query parameters are provided, this endpoint returns only the
   * corresponding users. Otherwise, it returns the complete user list ordered according to the
   * underlying query implementation.
   *
   * @param ids the optional user identifiers used to restrict the returned collection
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     UserResponse}
   */
  @GET
  public Response list(@QueryParam("ids") List<UUID> ids) {
    List<UserView> views =
        CollectionUtils.isEmpty(ids) ? readService.listViews() : readService.listViewsByIds(ids);
    List<UserResponse> list =
        views.stream().map(v -> UserPresenter.toResponse(v, locale())).toList();

    return Response.ok(ApiEnvelope.ok(list)).build();
  }

  /**
   * Executes paginated user search using the complex-search contract.
   *
   * @param page the zero-based page index
   * @param size the requested page size; {@code 1} returns the full result set in a single page
   * @param request the optional complex-search filters
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the paginated search
   *     result
   */
  @POST
  @Path("search")
  public Response search(
      @QueryParam("page") @DefaultValue("0") @Min(0) int page,
      @QueryParam("size") @DefaultValue("25") @Min(1) int size,
      @Valid UserComplexSearchRequest request) {
    UserComplexSearchCriteria criteria =
        request == null
            ? new UserComplexSearchCriteria(null, null, null, null)
            : new UserComplexSearchCriteria(
                request.cpf(), request.dateFrom(), request.dateTo(), request.name());
    var result = readService.search(new PageQuery(page, size), criteria);
    var responseBody =
        new PageResponse<>(
            result.content().stream().map(v -> UserPresenter.toResponse(v, locale())).toList(),
            result.page(),
            result.size(),
            result.totalElements(),
            result.totalPages());

    return Response.ok(ApiEnvelope.ok(responseBody)).build();
  }

  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
