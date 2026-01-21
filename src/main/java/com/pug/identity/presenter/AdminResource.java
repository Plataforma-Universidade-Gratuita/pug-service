package com.pug.identity.presenter;

import com.pug.identity.domain.Admin;
import com.pug.identity.domain.IAdminRepository;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.identity.infra.read.dtos.AdminView;
import com.pug.identity.presenter.dtos.AdminCreateOrUpdateRequest;
import com.pug.identity.presenter.dtos.AdminResponse;
import com.pug.identity.presenter.mappers.AdminPresenter;
import com.pug.identity.service.IAdminService;
import com.pug.identity.service.IPasswordService;
import com.pug.identity.service.dtos.AccountCreateCommand;
import com.pug.identity.service.dtos.AccountUpdateCommand;
import com.pug.identity.service.dtos.AdminCreateCommand;
import com.pug.identity.service.dtos.AdminUpdateCommand;
import com.pug.identity.service.dtos.UserCreateOrUpdateCommand;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.AppValidationException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST resource for managing admin users.
 */
@Path("/identity/admins")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

  @Inject
  IPasswordService passwordService;
  @Inject
  IAdminRepository readService;
  @Inject
  IAdminService writeService;
  @Inject
  I18n i18n;

  @Context
  HttpHeaders headers;
  @Context
  UriInfo uri;

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
   * @param id the admin's account ID.
   * @return the admin response.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the admin is not found.
   */
  @GET
  @Path("{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    AdminView v = readService.getViewById(id);
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
                    .collect(Collectors.toList());
    return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(list))).build();
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
    Cpf cpfVO;
    cpfVO = new Cpf(cpfRaw);
    List<AdminResponse> list =
            readService.listViewsByCpf(cpfVO.toString()).stream()
                    .map(v -> AdminPresenter.toResponse(v, locale(), i18n))
                    .collect(Collectors.toList());
    return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(list))).build();
  }

  /**
   * Gets an admin by email.
   *
   * @param emailRaw the email string.
   * @return the admin response.
   * @throws AppValidationException if the provided email is malformed.
   */
  @GET
  @Path("by-email")
  public Response getByEmail(@QueryParam("email") String emailRaw) {
    if (StringUtils.isEmpty(emailRaw)) {
      return list();
    }
    Email emailVO;
    emailVO = new Email(emailRaw);
    AdminView view = readService.getViewByEmail(emailVO.toString());
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
                    .collect(Collectors.toList());
    return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(list))).build();
  }

  /**
   * Creates a new admin.
   *
   * @param req the admin creation request.
   * @return the response with the created admin.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if an admin with the same email/CPF already exists.
   * @throws AppValidationException                               if input validation fails.
   */
  @POST
  public Response create(@Valid AdminCreateOrUpdateRequest req) {
    String hashedPassword = passwordService.hash(req.password());

    UserCreateOrUpdateCommand userCmd = new UserCreateOrUpdateCommand(req.cpfString(), req.name());
    AccountCreateCommand accountCmd = new AccountCreateCommand(req.emailString(), AccountType.ADMIN, hashedPassword, userCmd);
    AdminCreateCommand adminCmd = new AdminCreateCommand(accountCmd);

    Admin admin = writeService.save(adminCmd);

    AdminResponse body =
            AdminPresenter.toResponse(readService.getViewById(admin.getAccountId()), locale(), i18n);
    URI location = uri.getAbsolutePathBuilder().path(admin.getAccountId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Creates multiple new admins in bulk.
   *
   * @param reqs the list of admin creation requests.
   * @return the response with the amount of admins created.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if any admin with the same email/CPF already exists.
   * @throws AppValidationException                               if input validation fails for any admin in the bulk.
   */
  @POST
  @Path("bulk")
  public Response createBulk(@Valid List<AdminCreateOrUpdateRequest> reqs) {
    List<AdminCreateCommand> cmds =
            reqs.stream()
                    .map(
                            req -> {
                              String hashedPassword = passwordService.hash(req.password());
                              UserCreateOrUpdateCommand userCmd = new UserCreateOrUpdateCommand(req.cpfString(), req.name());
                              AccountCreateCommand accountCmd = new AccountCreateCommand(req.emailString(), AccountType.ADMIN, hashedPassword, userCmd);
                              return new AdminCreateCommand(accountCmd);
                            })
                    .collect(Collectors.toList());

    List<Admin> admins = writeService.saveAll(cmds);
    return Response.ok(ApiEnvelope.ok(BulkCreateResult.sizeOnly(admins.size()))).build();
  }

  /**
   * Updates an existing admin.
   *
   * @param id  the admin's account ID.
   * @param req the admin update request.
   * @return the response with the updated admin.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException  if the admin does not exist.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if an admin with the updated email/CPF already exists.
   * @throws AppValidationException                               if input validation fails.
   */
  @PUT
  @Path("{id}")
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid AdminCreateOrUpdateRequest req) {
    String hashedPassword = passwordService.hash(req.password());

    UserCreateOrUpdateCommand userCmd = new UserCreateOrUpdateCommand(req.cpfString(), req.name());
    AccountUpdateCommand accountCmd = new AccountUpdateCommand(req.emailString(), hashedPassword, userCmd);
    AdminUpdateCommand adminCmd = new AdminUpdateCommand(accountCmd);

    Admin updatedAdmin = writeService.update(id, adminCmd);

    AdminResponse body =
            AdminPresenter.toResponse(readService.getViewById(updatedAdmin.getAccountId()), locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Deletes admins by their IDs.
   *
   * @param req the request containing the IDs to delete.
   * @return the response with the deletion result.
   * @throws com.pug.shared.exceptions.ReferencedEntityException if any admin is still referenced.
   */
  @DELETE
  public Response delete(@Valid UuidsRequest req) {
    Map<DeleteKeys, Long> result = writeService.deleteAll(req.ids());

    return Response.ok(ApiEnvelope.ok(new DeleteResult(result))).build();
  }
}