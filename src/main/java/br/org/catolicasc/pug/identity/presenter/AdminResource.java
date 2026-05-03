package br.org.catolicasc.pug.identity.presenter;

import br.org.catolicasc.pug.identity.constants.IdentityApiPaths;
import br.org.catolicasc.pug.identity.domain.Admin;
import br.org.catolicasc.pug.identity.infra.read.dtos.AdminView;
import br.org.catolicasc.pug.identity.presenter.dtos.AdminCreateRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.AdminResponse;
import br.org.catolicasc.pug.identity.presenter.dtos.AdminUpdateRequest;
import br.org.catolicasc.pug.identity.presenter.mappers.AdminPresenter;
import br.org.catolicasc.pug.identity.service.AdminReadService;
import br.org.catolicasc.pug.identity.service.AdminService;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.identity.service.PasswordService;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST API Resource controller for managing Administrator profiles.
 *
 * <p>This class exposes endpoints to create, retrieve, update, patch, and revoke administrative
 * privileges. It delegates commands to the {@link AdminService} (writes) and queries to the {@link
 * AdminReadService} (reads), strictly adhering to CQRS principles.
 */
@Path(IdentityApiPaths.ADMINS)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
public class AdminResource {

  @Inject AuthService authService;
  @Inject PasswordService passwordService;
  @Inject AdminReadService readService;
  @Inject AdminService writeService;
  @Inject I18n i18n;

  @Context HttpHeaders headers;
  @Context UriInfo uri;

  /**
   * Retrieves a specific administrator by their linked account ID.
   *
   * @param id the unique identifier (UUIDv7) of the admin's account
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     AdminResponse}
   * @throws ResourceNotFoundException if the admin is not found
   */
  @GET
  @Path(IdentityApiPaths.ITEM)
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    AdminView v = readService.getViewByAccountId(id);
    AdminResponse body = AdminPresenter.toResponse(v, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves the administrator profile associated with the currently authenticated account.
   *
   * <p>The account identifier is resolved from the JWT {@code accountId} claim via {@link
   * AuthService}, ensuring that callers can only access their own administrator profile. This
   * endpoint is restricted to users with the ADMIN role.
   *
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     AdminResponse}
   * @throws jakarta.ws.rs.NotAuthorizedException if the token is missing, invalid, or does not
   *     contain the required {@code accountId} claim
   */
  @GET
  @Path(IdentityApiPaths.SELF)
  public Response getMe() {
    UUID accountId = authService.getCurrentAccountId();
    AdminView v = readService.getViewByAccountId(accountId);
    AdminResponse body = AdminPresenter.toResponse(v, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves administrators, optionally filtered by query parameters.
   *
   * <p>When {@code email} is provided, this endpoint returns the administrator linked to that
   * account email. When {@code cpf} is provided, it returns the administrators associated with that
   * CPF. If neither identifier is present, it falls back to full-text search with {@code q} or
   * lists all administrators when no filters are supplied.
   *
   * @param query the optional search query string
   * @param emailRaw the optional email used to retrieve a single administrator
   * @param cpfRaw the optional CPF used to retrieve the administrators associated with a user
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with either a single {@link
   *     AdminResponse} or a list of {@link AdminResponse}
   */
  @GET
  public Response list(
      @QueryParam("q") String query,
      @QueryParam("email") String emailRaw,
      @QueryParam("cpf") String cpfRaw) {
    if (StringUtils.isNotEmpty(emailRaw)) {
      AdminView view = readService.getViewByEmail(emailRaw);
      AdminResponse body = AdminPresenter.toResponse(view, locale(), i18n);
      return Response.ok(ApiEnvelope.ok(body)).build();
    }

    if (StringUtils.isNotEmpty(cpfRaw)) {
      List<AdminResponse> list =
          readService.listViewsByCpf(cpfRaw).stream()
              .map(v -> AdminPresenter.toResponse(v, locale(), i18n))
              .collect(Collectors.toList());

      return Response.ok(ApiEnvelope.ok(list)).build();
    }

    List<AdminView> views;

    if (StringUtils.isNotEmpty(query)) {
      views = readService.search(query);
    } else {
      views = readService.listViews();
    }

    List<AdminResponse> list =
        views.stream()
            .map(v -> AdminPresenter.toResponse(v, locale(), i18n))
            .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(list)).build();
  }

  /**
   * Registers a new administrator within the platform.
   *
   * <p>This endpoint processes an aggregated payload, automatically handling the provisioning of
   * the underlying user and authentication account.
   *
   * @param req the validated {@link AdminCreateRequest} containing the identity, credentials, and
   *     campus
   * @return an HTTP 201 Created response containing a {@code Location} header and the created
   *     {@link AdminResponse}
   * @throws DuplicateResourceException if an admin with the same email/CPF already exists
   * @throws AppValidationException if input validation fails at the domain level
   */
  @POST
  public Response create(@Valid AdminCreateRequest req) {
    String hashedPassword = passwordService.hash(req.password());
    var cmd = AdminPresenter.toCommand(req, hashedPassword);

    Admin admin = writeService.save(cmd);

    AdminResponse body =
        AdminPresenter.toResponse(
            readService.getViewByAccountId(admin.getAccountId()), locale(), i18n);
    URI location = uri.getAbsolutePathBuilder().path(admin.getAccountId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Partially updates an existing administrator's details.
   *
   * <p>Omitting fields in the request payload will result in those fields retaining their current
   * state in the database.
   *
   * @param id the unique identifier (UUIDv7) of the admin's account
   * @param req the validated {@link AdminUpdateRequest} containing the modified data
   * @return an HTTP 200 OK response containing the updated {@link AdminResponse}
   * @throws ResourceNotFoundException if the admin does not exist
   * @throws DuplicateResourceException if an updated email/CPF conflicts with an existing record
   * @throws AppValidationException if input validation fails
   */
  @PUT
  @Path(IdentityApiPaths.ITEM)
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid AdminUpdateRequest req) {
    String hashedPassword = req.password() != null ? passwordService.hash(req.password()) : null;
    var cmd = AdminPresenter.toCommand(req, hashedPassword);

    Admin updatedAdmin = writeService.update(id, cmd);

    AdminResponse body =
        AdminPresenter.toResponse(
            readService.getViewByAccountId(updatedAdmin.getAccountId()), locale(), i18n);

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Applies a partial update to an existing administrator.
   *
   * <p>This endpoint supports the same partial payload semantics as {@link #update(UUID,
   * AdminUpdateRequest)} and is also used for activation-state changes, such as setting {@code
   * active} to {@code false} to deactivate the account.
   *
   * @param id the unique identifier (UUIDv7) of the admin's account
   * @param req the validated {@link AdminUpdateRequest} containing the fields to change
   * @return an HTTP 204 No Content response when the update succeeds
   * @throws ResourceNotFoundException if the admin does not exist
   * @throws DuplicateResourceException if an updated email/CPF conflicts with an existing record
   * @throws AppValidationException if input validation fails
   */
  @PATCH
  @Path(IdentityApiPaths.ITEM)
  public Response patch(@PathParam("id") @UuidV7 UUID id, @Valid AdminUpdateRequest req) {
    String hashedPassword = req.password() != null ? passwordService.hash(req.password()) : null;
    var cmd = AdminPresenter.toCommand(req, hashedPassword);

    writeService.update(id, cmd);
    return Response.noContent().build();
  }

  /**
   * Permanently revokes administrative privileges by deleting the admin record and its associated
   * account.
   *
   * @param id the unique identifier (UUIDv7) of the admin's account
   * @return an HTTP 204 No Content response when deletion succeeds
   * @throws ResourceNotFoundException if the admin does not exist
   */
  @DELETE
  @Path(IdentityApiPaths.ITEM)
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.noContent().build();
  }

  /** Helper method to determine the preferred locale from the incoming request headers. */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
