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

/** REST resource for managing admin users. */
@Path("/identity/admins")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

  @Inject PasswordService passwordService;
  @Inject AdminReadService readService;
  @Inject AdminService writeService;
  @Inject I18n i18n;

  @Context HttpHeaders headers;
  @Context UriInfo uri;

  /**
   * Gets an admin by ID.
   *
   * @param id the admin's account ID.
   * @return the admin response.
   * @throws ResourceNotFoundException if the admin is not found.
   */
  @GET
  @Path("{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    AdminView v = readService.getViewByAccountId(id);
    AdminResponse body = AdminPresenter.toResponse(v, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Lists admins.
   *
   * <p>If the 'q' query parameter is provided, performs a search by account name. Otherwise, returns
   * all admins.
   *
   * @param query optional name query to search for.
   * @return the list of admin responses.
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
   * Lists admins by CPF.
   *
   * @param cpfRaw the CPF string.
   * @return the list of admin responses.
   * @throws AppValidationException if the provided CPF is malformed.
   */
  @GET
  @Path("by-cpf/{cpf}")
  public Response getByCpf(@PathParam("cpf") String cpfRaw) {
    List<AdminResponse> list =
        readService.listViewsByCpf(cpfRaw).stream()
            .map(v -> AdminPresenter.toResponse(v, locale(), i18n))
            .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(list)).build();
  }

  /**
   * Gets an admin by email.
   *
   * @param emailRaw the email string.
   * @return the admin response.
   * @throws AppValidationException if the provided email is malformed.
   * @throws ResourceNotFoundException if no admin with the given email is found.
   */
  @GET
  @Path("by-email/{email}")
  public Response getByEmail(@PathParam("email") @NotNull String emailRaw) {
    AdminView view = readService.getViewByEmail(emailRaw);
    AdminResponse body = AdminPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Creates a new admin.
   *
   * @param req the admin creation request.
   * @return the response with the created admin.
   * @throws DuplicateResourceException if an admin with the same email/CPF already exists.
   * @throws AppValidationException if input validation fails.
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
   * Updates an existing admin.
   *
   * @param id the admin's account ID.
   * @param req the admin update request.
   * @return the response with the updated admin.
   * @throws ResourceNotFoundException if the admin does not exist.
   * @throws DuplicateResourceException if an admin with the updated email/CPF already exists.
   * @throws AppValidationException if input validation fails.
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
   * Deletes an admin by ID.
   *
   * @param id the admin's account ID.
   * @return the response indicating successful deletion.
   * @throws ResourceNotFoundException if the admin does not exist.
   */
  @DELETE
  @Path("{id}")
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  /** Picks the best locale from the request headers. */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
