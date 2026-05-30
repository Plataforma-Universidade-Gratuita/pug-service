package br.org.catolicasc.pug.partner.presenter;

import br.org.catolicasc.pug.identity.presenter.dtos.accounts.AccountStatusRequest;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffView;
import br.org.catolicasc.pug.partner.presenter.dtos.staff.StaffComplexSearchRequest;
import br.org.catolicasc.pug.partner.presenter.dtos.staff.StaffCreateRequest;
import br.org.catolicasc.pug.partner.presenter.dtos.staff.StaffResponse;
import br.org.catolicasc.pug.partner.presenter.dtos.staff.StaffUpdateRequest;
import br.org.catolicasc.pug.partner.presenter.mappers.StaffPresenter;
import br.org.catolicasc.pug.partner.service.StaffReadService;
import br.org.catolicasc.pug.partner.service.StaffService;
import br.org.catolicasc.pug.partner.service.dtos.staff.StaffComplexSearchCriteria;
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
 * REST API resource controller for managing partner staff members.
 *
 * <p>This class exposes endpoints to create, retrieve, update, delete, update status, and
 * complex-search staff members linked to partner entities. It delegates commands to the {@link
 * StaffService} and queries to the {@link StaffReadService}, adhering to CQRS principles.
 */
@ApplicationScoped
@Path("/v1/partners/staff")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StaffResource {

  @Inject StaffService writeService;
  @Inject StaffReadService readService;
  @Inject AuthService authService;
  @Inject I18n i18n;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  /**
   * Retrieves a specific staff member by the linked account UUID identifier.
   *
   * @param id the unique identifier (UUIDv7) of the linked staff account
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     StaffResponse}
   */
  @GET
  @Path("/{id}")
  @Authenticated
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    StaffView v = readService.getViewByAccountId(id);
    StaffResponse body = StaffPresenter.toResponse(v, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves the staff profile associated with the currently authenticated account.
   *
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the current {@link
   *     StaffResponse}
   */
  @GET
  @Path("/me")
  @Authenticated
  public Response getMe() {
    UUID accountId = authService.getCurrentAccountId();
    StaffView v = readService.getViewByAccountId(accountId);
    StaffResponse body = StaffPresenter.toResponse(v, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves staff members, optionally restricted to the provided linked account identifiers.
   *
   * @param ids the optional linked account identifiers used to restrict the returned staff members
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the matching {@link
   *     StaffResponse} list
   */
  @GET
  @Authenticated
  public Response list(@QueryParam("ids") List<UUID> ids) {
    List<StaffView> views =
        CollectionUtils.isEmpty(ids) ? readService.listViews() : readService.listViewsByIds(ids);
    List<StaffResponse> list =
        views.stream().map(v -> StaffPresenter.toResponse(v, locale(), i18n)).toList();
    return Response.ok(ApiEnvelope.ok(list)).build();
  }

  /**
   * Executes paginated staff search using the complex-search contract.
   *
   * @param page the requested zero-based page index
   * @param size the requested page size; {@code 1} triggers the shared fetch-all behavior
   * @param request the optional complex-search payload
   * @return an HTTP 200 OK response containing a paginated complex-search result set
   */
  @POST
  @Path("/search")
  @Authenticated
  public Response search(
      @QueryParam("page") @DefaultValue("0") @Min(0) int page,
      @QueryParam("size") @DefaultValue("25") @Min(1) int size,
      @Valid StaffComplexSearchRequest request) {
    StaffComplexSearchCriteria criteria =
        request == null
            ? new StaffComplexSearchCriteria(null, null, null, null, null, true, null)
            : new StaffComplexSearchCriteria(
                request.name(),
                request.cpf(),
                request.email(),
                request.dateFrom(),
                request.dateTo(),
                request.activeOnly() == null || request.activeOnly(),
                request.entityIds());

    var result = readService.search(new PageQuery(page, size), criteria);
    var responseBody =
        new PageResponse<>(
            result.content().stream()
                .map(v -> StaffPresenter.toComplexSearchResponse(v, locale(), i18n))
                .toList(),
            result.page(),
            result.size(),
            result.totalElements(),
            result.totalPages());

    return Response.ok(ApiEnvelope.ok(responseBody)).build();
  }

  /**
   * Registers a new partner staff member within the platform.
   *
   * @param req the validated {@link StaffCreateRequest} containing the staff member's details
   * @return an HTTP 201 Created response containing the created {@link StaffResponse}
   */
  @POST
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response create(@Valid StaffCreateRequest req) {
    Staff staff = writeService.save(StaffPresenter.toCommand(req));
    StaffView v = readService.getViewByAccountId(staff.getAccountId());
    StaffResponse out = StaffPresenter.toResponse(v, locale(), i18n);
    URI location = uri.getAbsolutePathBuilder().path(staff.getAccountId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(out)).build();
  }

  /**
   * Updates an existing partner staff member's details.
   *
   * @param id the unique identifier (UUIDv7) of the linked staff account to update
   * @param req the validated {@link StaffUpdateRequest} containing the modified data
   * @return an HTTP 200 OK response containing the updated {@link StaffResponse}
   */
  @PUT
  @Path("/{id}")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid StaffUpdateRequest req) {
    Staff updated = writeService.update(id, StaffPresenter.toCommand(req));
    StaffView v = readService.getViewByAccountId(updated.getAccountId());
    StaffResponse body = StaffPresenter.toResponse(v, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Updates the active status of an existing partner staff account.
   *
   * @param id the unique identifier (UUIDv7) of the linked staff account to update
   * @param req the validated {@link AccountStatusRequest} containing the desired active status
   * @return an HTTP 204 No Content response when the status update succeeds
   */
  @PATCH
  @Path("/{id}/status")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response updateStatus(@PathParam("id") @UuidV7 UUID id, @Valid AccountStatusRequest req) {
    writeService.updateStatus(id, req.active());
    return Response.noContent().build();
  }

  /**
   * Permanently removes a partner staff member from the system.
   *
   * @param id the unique identifier (UUIDv7) of the linked staff account to delete
   * @return an HTTP 204 No Content response when deletion succeeds
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
