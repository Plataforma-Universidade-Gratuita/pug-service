/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.presenter;

import br.org.catolicasc.pug.identity.domain.Admin;
import br.org.catolicasc.pug.identity.infra.read.dtos.AdminView;
import br.org.catolicasc.pug.identity.presenter.dtos.accounts.AccountStatusRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.admins.AdminComplexSearchRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.admins.AdminCreateRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.admins.AdminResponse;
import br.org.catolicasc.pug.identity.presenter.dtos.admins.AdminUpdateRequest;
import br.org.catolicasc.pug.identity.presenter.mappers.AdminPresenter;
import br.org.catolicasc.pug.identity.service.AdminsReadService;
import br.org.catolicasc.pug.identity.service.AdminsService;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.identity.service.dtos.admins.AdminComplexSearchCriteria;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.dtos.PageResponse;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
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

/**
 * REST API resource controller for managing Administrator profiles.
 *
 * <p>This class exposes endpoints to create, retrieve, update, search, and revoke administrative
 * privileges. It delegates commands to the {@link AdminsService} (writes) and queries to the {@link
 * AdminsReadService} (reads), strictly adhering to CQRS principles.
 */
@Path("/v1/identity/admins")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
public class AdminsResource {

  @Inject AuthService authService;
  @Inject AdminsReadService readService;
  @Inject AdminsService writeService;
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
  @Path("{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    AdminView view = readService.getViewById(id);
    AdminResponse body = AdminPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves the administrator profile associated with the currently authenticated account.
   *
   * <p>The account identifier is resolved from the JWT {@code accountId} claim via {@link
   * AuthService}, ensuring that callers can only access their own administrator profile.
   *
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     AdminResponse}
   */
  @GET
  @Path("me")
  public Response getMe() {
    UUID accountId = authService.getCurrentAccountId();
    AdminView view = readService.getViewByAccountId(accountId);
    AdminResponse body = AdminPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves administrators, optionally filtered by a collection of linked account identifiers.
   *
   * <p>When one or more {@code ids} query parameters are provided, this endpoint returns only the
   * corresponding administrators. Otherwise, it returns the complete administrator list ordered
   * according to the underlying query implementation.
   *
   * @param ids the optional linked-account identifiers used to restrict the returned collection
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     AdminResponse}
   */
  @GET
  public Response list(@QueryParam("ids") List<UUID> ids) {
    List<AdminView> views =
        CollectionUtils.isEmpty(ids) ? readService.listViews() : readService.listViewsByIds(ids);
    List<AdminResponse> list =
        views.stream().map(v -> AdminPresenter.toResponse(v, locale(), i18n)).toList();
    return Response.ok(ApiEnvelope.ok(list)).build();
  }

  /**
   * Executes paginated administrator search using the complex-search contract.
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
      @Valid AdminComplexSearchRequest request) {
    AdminComplexSearchCriteria criteria =
        request == null
            ? new AdminComplexSearchCriteria(null, null, null, null, null, true)
            : new AdminComplexSearchCriteria(
                request.name(),
                request.cpf(),
                request.email(),
                request.dateFrom(),
                request.dateTo(),
                request.activeOnly() == null || request.activeOnly());

    var result = readService.search(new PageQuery(page, size), criteria);
    var responseBody =
        new PageResponse<>(
            result.content().stream()
                .map(v -> AdminPresenter.toComplexSearchResponse(v, locale(), i18n))
                .toList(),
            result.page(),
            result.size(),
            result.totalElements(),
            result.totalPages());

    return Response.ok(ApiEnvelope.ok(responseBody)).build();
  }

  /**
   * Registers a new administrator within the platform.
   *
   * <p>This endpoint processes an aggregated payload, automatically handling the provisioning of
   * the underlying user and authentication account. Password setup is intentionally deferred, so
   * the newly created account starts without a stored password hash.
   *
   * @param req the validated {@link AdminCreateRequest} containing the identity and campus data
   * @return an HTTP 201 Created response containing a {@code Location} header and the created
   *     {@link AdminResponse}
   * @throws DuplicateResourceException if an admin with the same email/CPF already exists
   * @throws AppValidationException if input validation fails at the domain level
   */
  @POST
  public Response create(@Valid AdminCreateRequest req) {
    var cmd = AdminPresenter.toCommand(req);
    Admin admin = writeService.save(cmd);

    AdminResponse body =
        AdminPresenter.toResponse(
            readService.getViewByAccountId(admin.getAccountId()), locale(), i18n);
    URI location = uri.getAbsolutePathBuilder().path(admin.getAccountId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Updates an existing administrator's details.
   *
   * <p>This endpoint accepts the account and campus fields that remain editable through the admin
   * maintenance workflow. Password and activation state changes are handled by dedicated flows.
   *
   * @param id the unique identifier (UUIDv7) of the admin's account
   * @param req the validated {@link AdminUpdateRequest} containing the modified data
   * @return an HTTP 200 OK response containing the updated {@link AdminResponse}
   * @throws ResourceNotFoundException if the admin does not exist
   * @throws DuplicateResourceException if an updated email conflicts with an existing record
   * @throws AppValidationException if input validation fails
   */
  @PUT
  @Path("{id}")
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid AdminUpdateRequest req) {
    var cmd = AdminPresenter.toCommand(req);
    Admin updatedAdmin = writeService.update(id, cmd);

    AdminResponse body =
        AdminPresenter.toResponse(
            readService.getViewByAccountId(updatedAdmin.getAccountId()), locale(), i18n);

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Updates the activation status of an existing administrator account.
   *
   * @param id the unique identifier (UUIDv7) of the admin's account
   * @param req the validated {@link AccountStatusRequest} containing the target activation flag
   * @return an HTTP 204 No Content response when the update succeeds
   * @throws ResourceNotFoundException if the admin does not exist
   * @throws AppValidationException if the resulting linked-account state is invalid
   */
  @PATCH
  @Path("{id}/status")
  public Response updateStatus(@PathParam("id") @UuidV7 UUID id, @Valid AccountStatusRequest req) {
    writeService.updateStatus(id, req.active());
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
  @Path("{id}")
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.noContent().build();
  }

  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
