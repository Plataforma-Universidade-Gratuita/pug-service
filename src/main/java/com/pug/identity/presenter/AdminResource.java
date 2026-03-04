package com.pug.identity.presenter;

import com.pug.identity.domain.Admin;
import com.pug.identity.infra.read.dtos.AdminView;
import com.pug.identity.presenter.dtos.AdminCreateRequest;
import com.pug.identity.presenter.dtos.AdminResponse;
import com.pug.identity.presenter.dtos.AdminUpdateRequest;
import com.pug.identity.presenter.mappers.AdminPresenter;
import com.pug.identity.service.AdminReadService;
import com.pug.identity.service.AdminService;
import com.pug.identity.service.PasswordService;
import com.pug.identity.service.dtos.AccountCreateCommand;
import com.pug.identity.service.dtos.AccountUpdateCommand;
import com.pug.identity.service.dtos.AdminCreateCommand;
import com.pug.identity.service.dtos.AdminUpdateCommand;
import com.pug.identity.service.dtos.UserCreateCommand;
import com.pug.identity.service.dtos.UserUpdateCommand;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.utils.PresenterUtils;
import com.pug.shared.utils.StringUtils;
import com.pug.shared.validation.UuidV7;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
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
 * <p>
 * This class exposes endpoints to create, retrieve, update, and revoke administrative
 * privileges. It delegates commands to the {@link AdminService} (writes) and queries
 * to the {@link AdminReadService} (reads), strictly adhering to CQRS principles.
 */
@Path("/identity/admins")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

  @Inject
  PasswordService passwordService;
  @Inject
  AdminReadService readService;
  @Inject
  AdminService writeService;
  @Inject
  I18n i18n;

  @Context
  HttpHeaders headers;
  @Context
  UriInfo uri;

  /**
   * Retrieves a specific administrator by their linked account ID.
   *
   * @param id the unique identifier (UUIDv7) of the admin's account
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link AdminResponse}
   * @throws ResourceNotFoundException if the admin is not found
   */
  @GET
  @Path("{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    AdminView v = readService.getViewByAccountId(id);
    AdminResponse body = AdminPresenter.toResponse(v, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a specific administrator by their registered email address.
   *
   * @param emailRaw the exact email string of the admin
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link AdminResponse}
   * @throws AppValidationException    if the provided email is malformed
   * @throws ResourceNotFoundException if no admin with the given email is found
   */
  @GET
  @Path("by-email/{email}")
  public Response getByEmail(@PathParam("email") @NotNull String emailRaw) {
    AdminView view = readService.getViewByEmail(emailRaw);
    AdminResponse body = AdminPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a collection of administrators.
   * <p>
   * If the optional {@code q} parameter is provided, it executes a full-text search against
   * the admins' personal names. If omitted, it returns an unfiltered list of all admins.
   *
   * @param query the optional search query string
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link AdminResponse}
   */
  @GET
  public Response list(@QueryParam("q") String query) {
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
   * Retrieves a collection of administrators filtered by the user's CPF.
   *
   * @param cpfRaw the raw 11-digit numeric CPF string
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link AdminResponse}
   * @throws AppValidationException if the provided CPF is malformed
   */
  @GET
  @Path("by-cpf/{cpf}")
  public Response listByCpf(@PathParam("cpf") String cpfRaw) {
    List<AdminResponse> list =
            readService.listViewsByCpf(cpfRaw).stream()
                    .map(v -> AdminPresenter.toResponse(v, locale(), i18n))
                    .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(list)).build();
  }

  /**
   * Registers a new administrator within the platform.
   * <p>
   * This endpoint processes an aggregated payload, automatically handling the
   * provisioning of the underlying user and authentication account.
   *
   * @param req the validated {@link AdminCreateRequest} containing the identity, credentials, and campus
   * @return an HTTP 201 Created response containing a {@code Location} header and the created {@link AdminResponse}
   * @throws DuplicateResourceException if an admin with the same email/CPF already exists
   * @throws AppValidationException     if input validation fails at the domain level
   */
  @POST
  public Response create(@Valid AdminCreateRequest req) {
    String hashedPassword = passwordService.hash(req.password());

    UserCreateCommand userCmd = new UserCreateCommand(req.cpfString(), req.name());
    AccountCreateCommand accountCmd =
            new AccountCreateCommand(req.emailString(), AccountType.ADMIN, hashedPassword, userCmd);
    AdminCreateCommand adminCmd = new AdminCreateCommand(accountCmd, req.campus());

    Admin admin = writeService.save(adminCmd);

    AdminResponse body =
            AdminPresenter.toResponse(
                    readService.getViewByAccountId(admin.getAccountId()), locale(), i18n);
    URI location = uri.getAbsolutePathBuilder().path(admin.getAccountId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Partially updates an existing administrator's details.
   * <p>
   * Omitting fields in the request payload will result in those fields retaining their
   * current state in the database.
   *
   * @param id  the unique identifier (UUIDv7) of the admin's account
   * @param req the validated {@link AdminUpdateRequest} containing the modified data
   * @return an HTTP 200 OK response containing the updated {@link AdminResponse}
   * @throws ResourceNotFoundException  if the admin does not exist
   * @throws DuplicateResourceException if an updated email/CPF conflicts with an existing record
   * @throws AppValidationException     if input validation fails
   */
  @PUT
  @Path("{id}")
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid AdminUpdateRequest req) {
    String hashedPassword = passwordService.hash(req.password());

    UserUpdateCommand userCmd = new UserUpdateCommand(req.cpfString(), req.name());
    AccountUpdateCommand accountCmd =
            new AccountUpdateCommand(req.emailString(), hashedPassword, userCmd);
    AdminUpdateCommand adminCmd = new AdminUpdateCommand(accountCmd, req.campus());

    Admin updatedAdmin = writeService.update(id, adminCmd);
    AdminResponse body =
            AdminPresenter.toResponse(
                    readService.getViewByAccountId(updatedAdmin.getAccountId()), locale(), i18n);

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Permanently revokes administrative privileges by deleting the admin record and its associated account.
   *
   * @param id the unique identifier (UUIDv7) of the admin's account
   * @return an HTTP 200 OK response with an empty data payload indicating successful deletion
   * @throws ResourceNotFoundException if the admin does not exist
   */
  @DELETE
  @Path("{id}")
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  /**
   * Helper method to determine the preferred locale from the incoming request headers.
   */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}