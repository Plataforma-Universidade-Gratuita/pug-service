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
import com.pug.identity.service.AccountService;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.i18n.I18n;
import com.pug.shared.utils.PresenterUtils;
import com.pug.shared.presenter.dtos.BulkCreateResult;
import com.pug.shared.presenter.dtos.DeleteResult;
import com.pug.shared.presenter.dtos.UuidsRequest;
import com.pug.shared.presenter.rest.ApiEnvelope;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/** REST resource for managing admin users. */
@Path("/admins")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

  @Inject AdminReadService readService;
  @Inject AdminService adminService;
  @Inject
  AccountService accountService;
  @Inject I18n i18n;
  @Context HttpHeaders headers;

  /**
   * Get admin by ID.
   *
   * @param id Admin user ID.
   * @return Response with admin data.
   */
  @GET
  @Path("{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    AdminView v = readService.getView(id);
    Locale locale = PresenterUtils.pickLocale(headers.getAcceptableLanguages());
    AdminResponse body = AdminPresenter.toResponse(v, locale, i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * List all admins.
   *
   * @return Response with list of admins.
   */
  @GET
  public Response list() {
    Locale locale = PresenterUtils.pickLocale(headers.getAcceptableLanguages());
    List<AdminResponse> list =
        readService.listViews().stream()
            .map(v -> AdminPresenter.toResponse(v, locale, i18n))
            .toList();
    return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(list))).build();
  }

  /**
   * Get admin by CPF.
   *
   * @param cpfRaw Admin CPF.
   * @return Response with admin data.
   */
  @GET
  @Path("by-cpf/{cpf}")
  public Response getByCpf(@PathParam("cpf") String cpfRaw) {
    Locale locale = PresenterUtils.pickLocale(headers.getAcceptableLanguages());
    String cpf = new Cpf(cpfRaw).toString();
    var users =
        accountService.listByCpf(cpf).stream()
            .filter(u -> u.getAccountType() == AccountType.ADMIN)
            .toList();

    List<AdminResponse> list =
        users.stream()
            .map(u -> readService.getView(u.getId()))
            .filter(Objects::nonNull)
            .map(v -> AdminPresenter.toResponse(v, locale, i18n))
            .toList();

    if (list.isEmpty()) {
      throw new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND);
    }
    return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(list))).build();
  }

  /**
   * Get admin by email.
   *
   * @param email Admin email.
   * @return Response with admin data.
   */
  @GET
  @Path("by-email")
  public Response getByEmail(@QueryParam("email") String email) {
    if (email == null || email.isBlank()) {
      return list();
    }
    var user = accountService.getByEmail(email);
    if (user.getAccountType() != AccountType.ADMIN) {
      throw new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND);
    }
    var view = readService.getView(user.getId());
    if (view == null) {
      throw new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND);
    }
    Locale locale = PresenterUtils.pickLocale(headers.getAcceptableLanguages());
    AdminResponse body = AdminPresenter.toResponse(view, locale, i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Search admins by name.
   *
   * @param query Search query.
   * @return Response with list of matching admins.
   */
  @GET
  @Path("by-name")
  public Response listByName(@QueryParam("q") String query) {
    if (query == null || query.isBlank()) {
      return list();
    }
    Locale locale = PresenterUtils.pickLocale(headers.getAcceptableLanguages());
    var users =
        accountService.search(query).stream()
            .filter(u -> u.getAccountType() == AccountType.ADMIN)
            .toList();

    List<AdminResponse> list =
        users.stream()
            .map(u -> readService.getView(u.getId()))
            .filter(Objects::nonNull)
            .map(v -> AdminPresenter.toResponse(v, locale, i18n))
            .toList();

    return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(list))).build();
  }

  /**
   * Create a new admin.
   *
   * @param req CreateRequest payload.
   * @param uriInfo UriInfo for building location header.
   * @return Response with created admin data.
   */
  @POST
  public Response create(@Valid AdminCreateRequest req, @Context UriInfo uriInfo) {
    var admin =
        adminService.save(new Cpf(req.cpf()), req.name(), new Email(req.email()), req.password());
    var view = readService.getView(admin.getUserId());
    if (view == null) {
      throw new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND);
    }
    Locale locale = PresenterUtils.pickLocale(headers.getAcceptableLanguages());
    AdminResponse body = AdminPresenter.toResponse(view, locale, i18n);
    URI location = uriInfo.getAbsolutePathBuilder().path(admin.getUserId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Delete (revoke) an admin by ID.
   *
   * @param req Iterable of admin user IDs to delete.
   * @return Response with deletion result.
   */
  @DELETE
  public Response delete(@Valid UuidsRequest req) {
    Map<String, Long> result = adminService.deleteAll(req.ids());
    return Response.ok(ApiEnvelope.ok(new DeleteResult(result))).build();
  }
}
