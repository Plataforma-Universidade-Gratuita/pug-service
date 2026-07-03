/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.presenter;

import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentView;
import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentComplexSearchRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentCreateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentUpdateRequest;
import br.org.catolicasc.pug.academic.presenter.mappers.FormerStudentPresenter;
import br.org.catolicasc.pug.academic.service.FormerStudentsReadService;
import br.org.catolicasc.pug.academic.service.FormerStudentsService;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentComplexSearchCriteria;
import br.org.catolicasc.pug.identity.presenter.dtos.accounts.AccountStatusRequest;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.dtos.PageResponse;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
 * REST API resource controller for managing former-student records.
 *
 * <p>This resource exposes the academic former-student endpoints for direct lookup, authenticated
 * self-lookup, collection reads, complex search, creation, bulk creation, updates, status mutation,
 * and deletion. Command operations are delegated to {@link FormerStudentsService} while read
 * operations are delegated to {@link FormerStudentsReadService}, preserving the module's CQRS
 * boundary.
 */
@ApplicationScoped
@Path("/v1/academic/former-students")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FormerStudentsResource {

  @Inject AuthService authService;
  @Inject FormerStudentsService writeService;
  @Inject FormerStudentsReadService readService;
  @Inject I18n i18n;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  /**
   * Retrieves a former-student record by the linked account identifier.
   *
   * @param id linked account identifier
   * @return the requested former-student response
   */
  @GET
  @Path("/{id}")
  @Authenticated
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    FormerStudentView view = readService.getViewByAccountId(id);
    FormerStudentResponse body = FormerStudentPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves the former-student record associated with the authenticated account.
   *
   * @return the authenticated former-student response
   */
  @GET
  @Path("/me")
  @Authenticated
  public Response getMe() {
    FormerStudentView view = readService.getViewByAccountId(authService.getCurrentAccountId());
    FormerStudentResponse body = FormerStudentPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves former students, optionally filtered by a collection of linked account identifiers.
   *
   * @param ids the optional linked account identifiers used to restrict the result set
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     FormerStudentResponse}
   */
  @GET
  @Authenticated
  public Response list(@QueryParam("ids") List<UUID> ids) {
    List<FormerStudentView> views =
        CollectionUtils.isEmpty(ids) ? readService.listViews() : readService.listViewsByIds(ids);
    List<FormerStudentResponse> body =
        views.stream()
            .map(view -> FormerStudentPresenter.toResponse(view, locale(), i18n))
            .toList();
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Executes paginated former-student complex search.
   *
   * @param page the zero-based page index
   * @param size the requested page size; {@code 1} returns the full result set in a single page
   * @param request the optional complex-search filters
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the paginated search
   *     result
   */
  @POST
  @Path("/search")
  @Authenticated
  public Response search(
      @QueryParam("page") @DefaultValue("0") @Min(0) int page,
      @QueryParam("size") @DefaultValue("25") @Min(1) int size,
      @Valid FormerStudentComplexSearchRequest request) {
    FormerStudentComplexSearchCriteria criteria =
        request == null
            ? new FormerStudentComplexSearchCriteria(
                null, null, null, null, null, null, null, false, null, null, true, null, null)
            : new FormerStudentComplexSearchCriteria(
                request.name(),
                request.cpf(),
                request.email(),
                request.academicRegistration(),
                request.campi(),
                request.periodFrom(),
                request.periodTo(),
                Boolean.TRUE.equals(request.includeConcluded()),
                request.dateFrom(),
                request.dateTo(),
                request.activeOnly() == null || request.activeOnly(),
                request.courseIds(),
                request.areaOfExpertiseIds());

    var result = readService.search(new PageQuery(page, size), criteria);
    var responseBody =
        new PageResponse<>(
            result.content().stream()
                .map(view -> FormerStudentPresenter.toComplexSearchResponse(view, locale(), i18n))
                .toList(),
            result.page(),
            result.size(),
            result.totalElements(),
            result.totalPages());

    return Response.ok(ApiEnvelope.ok(responseBody)).build();
  }

  /**
   * Creates a single former-student record, including the linked identity structures required by
   * the academic module contract.
   *
   * @param request the validated creation payload received from the presenter layer
   * @return HTTP 201 containing the canonical former-student response and its resource location
   */
  @POST
  @RolesAllowed("ADMIN")
  public Response create(@Valid FormerStudentCreateRequest request) {
    FormerStudent created = writeService.save(FormerStudentPresenter.toCommand(request));
    FormerStudentView view = readService.getViewByAccountId(created.getAccountId());
    FormerStudentResponse body = FormerStudentPresenter.toResponse(view, locale(), i18n);
    URI location = uri.getAbsolutePathBuilder().path(created.getAccountId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Creates multiple former-student records in a single request.
   *
   * @param requests validated batch payload
   * @return the created former-student responses
   */
  @POST
  @Path("/bulk")
  @RolesAllowed("ADMIN")
  public Response createInBulk(@Valid @NotNull List<FormerStudentCreateRequest> requests) {
    List<FormerStudent> created =
        writeService.saveInBulk(requests.stream().map(FormerStudentPresenter::toCommand).toList());
    List<FormerStudentView> views =
        readService.listViewsByIds(created.stream().map(FormerStudent::getAccountId).toList());
    List<FormerStudentResponse> body =
        views.stream()
            .map(view -> FormerStudentPresenter.toResponse(view, locale(), i18n))
            .toList();
    return Response.status(Response.Status.CREATED).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Updates an existing former-student record.
   *
   * @param id linked account identifier
   * @param request validated update payload
   * @return the updated former-student response
   */
  @PUT
  @Path("/{id}")
  @RolesAllowed("ADMIN")
  public Response update(
      @PathParam("id") @UuidV7 UUID id, @Valid FormerStudentUpdateRequest request) {
    writeService.update(id, FormerStudentPresenter.toCommand(request));
    FormerStudentView view = readService.getViewByAccountId(id);
    FormerStudentResponse body = FormerStudentPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Updates the activation status of the linked former-student account.
   *
   * @param id linked account identifier
   * @param request validated account-status payload
   * @return HTTP 204 when the status update succeeds
   */
  @PATCH
  @Path("/{id}/status")
  @RolesAllowed("ADMIN")
  public Response updateStatus(
      @PathParam("id") @UuidV7 UUID id, @Valid AccountStatusRequest request) {
    writeService.updateStatus(id, request.active());
    return Response.noContent().build();
  }

  /**
   * Deletes a former-student record and its linked account according to the module rules.
   *
   * @param id linked account identifier
   * @return HTTP 204 when deletion succeeds
   */
  @DELETE
  @Path("/{id}")
  @RolesAllowed("ADMIN")
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.noContent().build();
  }

  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
