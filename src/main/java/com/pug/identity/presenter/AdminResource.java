package com.pug.identity.presenter;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.identity.infra.read.dtos.AdminView;
import com.pug.identity.presenter.dtos.AdminCreateRequest;
import com.pug.identity.presenter.dtos.AdminResponse;
import com.pug.identity.presenter.mappers.AdminPresenter;
import com.pug.identity.service.AdminReadService;
import com.pug.identity.service.AdminService;
import com.pug.identity.service.PasswordService;
import com.pug.identity.service.dtos.CreateAccountCommand;
import com.pug.identity.service.dtos.CreateAdminCommand;
import com.pug.identity.service.dtos.CreateOrUpdateUserCommand;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.dtos.BulkCreateResult;
import com.pug.shared.presenter.dtos.DeleteResult;
import com.pug.shared.presenter.dtos.UuidsRequest;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.utils.PresenterUtils;
import com.pug.shared.utils.StringUtils;
import com.pug.shared.validation.UuidV7;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
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
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/** REST resource for managing admin users (read + create/delete). */
@Path("/identity/admins")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

  @Inject PasswordService passwordService;
  @Inject AdminReadService readService;
  @Inject AdminService writeService;
  @Inject I18n i18n;

  @Context HttpHeaders headers;

  /**
   * Picks the best locale from the request headers.
   *
   * @return the selected locale.
   */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }

  /**
   * Gets an admin by ID.
   *
   * @param id the admin ID.
   * @return the admin response.
   */
  @GET
  @Path("{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    AdminView v = readService.getView(id);
    AdminResponse body = AdminPresenter.toResponse(v, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Lists all admins.
   *
   * @return the list of admin responses.
   */
  @GET
  public Response list() {
    List<AdminResponse> list =
        readService.listViews().stream()
            .map(v -> AdminPresenter.toResponse(v, locale(), i18n))
            .toList();
    return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(list))).build();
  }

  /**
   * Lists admins by CPF.
   *
   * @param cpfRaw the CPF string.
   * @return the list of admin responses.
   */
  @GET
  @Path("by-cpf/{cpf}")
  public Response getByCpf(@PathParam("cpf") String cpfRaw) {
    String cpf = new Cpf(cpfRaw).toString();
    List<AdminResponse> list =
        readService.listViewsByCpf(cpf).stream()
            .map(v -> AdminPresenter.toResponse(v, locale(), i18n))
            .toList();
    if (list.isEmpty()) {
      throw new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND);
    }
    return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(list))).build();
  }

  /**
   * Gets an admin by email.
   *
   * @param emailRaw the email string.
   * @return the admin response.
   */
  @GET
  @Path("by-email")
  public Response getByEmail(@QueryParam("email") String emailRaw) {
    if (StringUtils.isEmpty(emailRaw)) {
      return list();
    }
    String email = new Email(emailRaw).toString();
    var view = readService.getViewByEmail(email); // throws if not found
    AdminResponse body = AdminPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Searches admins by name.
   *
   * @param query the name query string.
   * @return the list of admin responses.
   */
  @GET
  @Path("by-name")
  public Response listByName(@QueryParam("q") String query) {
    if (StringUtils.isEmpty(query)) {
      return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(new ArrayList<>()))).build();
    }
    List<AdminResponse> list =
        readService.search(query).stream()
            .map(v -> AdminPresenter.toResponse(v, locale(), i18n))
            .toList();
    return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(list))).build();
  }

  /**
   * Creates a new admin.
   *
   * @param req the admin creation request.
   * @param uriInfo the URI info.
   * @return the response with the created admin.
   */
  @POST
  public Response create(@Valid AdminCreateRequest req, @Context UriInfo uriInfo) {
    String hashedPassword = passwordService.hash(req.password());
    var cmd =
        new CreateAdminCommand(
            new CreateAccountCommand(
                new CreateOrUpdateUserCommand(new Cpf(req.cpf()), req.name()),
                new Email(req.email()),
                AccountType.ADMIN,
                hashedPassword));
    var admin = writeService.save(cmd);

    AdminResponse body =
        AdminPresenter.toResponse(readService.getView(admin.getAccountId()), locale(), i18n);
    URI location = uriInfo.getAbsolutePathBuilder().path(admin.getAccountId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Creates multiple new admins in bulk.
   *
   * @param reqs the list of admin creation requests.
   * @return the response with the amount of admins created.
   */
  @POST
  @Path("bulk")
  public Response createBulk(@Valid List<AdminCreateRequest> reqs) {
    var cmds =
        reqs.stream()
            .map(
                req -> {
                  String hashedPassword = passwordService.hash(req.password());
                  return new CreateAdminCommand(
                      new CreateAccountCommand(
                          new CreateOrUpdateUserCommand(new Cpf(req.cpf()), req.name()),
                          new Email(req.email()),
                          AccountType.ADMIN,
                          hashedPassword));
                })
            .toList();

    var admins = writeService.saveAll(cmds);
    List<AdminResponse> bodies =
        admins.stream()
            .map(
                admin ->
                    AdminPresenter.toResponse(
                        readService.getView(admin.getAccountId()), locale(), i18n))
            .toList();
    return Response.ok(ApiEnvelope.ok(BulkCreateResult.sizeOnly(bodies.size()))).build();
  }

  /**
   * Deletes admins by their IDs.
   *
   * @param req the request containing the IDs to delete.
   * @return the response with the deletion result.
   */
  @DELETE
  public Response delete(@Valid UuidsRequest req) {
    Map<DeleteKeys, Long> result = writeService.deleteAll(req.ids());

    return Response.ok(ApiEnvelope.ok(new DeleteResult(result))).build();
  }
}
