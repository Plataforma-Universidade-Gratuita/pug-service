package br.org.catolicasc.pug.partner.presenter;

import br.org.catolicasc.pug.identity.presenter.dtos.AccountStatusRequest;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.partner.constants.PartnerApiPaths;
import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffView;
import br.org.catolicasc.pug.partner.presenter.dtos.StaffComplexSearchRequest;
import br.org.catolicasc.pug.partner.presenter.dtos.StaffCreateRequest;
import br.org.catolicasc.pug.partner.presenter.dtos.StaffResponse;
import br.org.catolicasc.pug.partner.presenter.dtos.StaffUpdateRequest;
import br.org.catolicasc.pug.partner.presenter.mappers.StaffPresenter;
import br.org.catolicasc.pug.partner.service.StaffReadService;
import br.org.catolicasc.pug.partner.service.StaffService;
import br.org.catolicasc.pug.partner.service.dtos.StaffComplexSearchCriteria;
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

/** REST API resource controller for managing Partner Staff privileges. */
@ApplicationScoped
@Path(PartnerApiPaths.STAFF)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StaffResource {

  @Inject StaffService writeService;
  @Inject StaffReadService readService;
  @Inject AuthService authService;
  @Inject I18n i18n;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  @GET
  @Path("/{id}")
  @Authenticated
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    StaffView v = readService.getViewByAccountId(id);
    StaffResponse body = StaffPresenter.toResponse(v, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  @GET
  @Path("/me")
  @Authenticated
  public Response getMe() {
    UUID accountId = authService.getCurrentAccountId();
    StaffView v = readService.getViewByAccountId(accountId);
    StaffResponse body = StaffPresenter.toResponse(v, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /** Retrieves staff members, optionally filtered by a collection of linked account identifiers. */
  @GET
  @Authenticated
  public Response list(@QueryParam("ids") List<UUID> ids) {
    List<StaffView> views =
        CollectionUtils.isEmpty(ids) ? readService.listViews() : readService.listViewsByIds(ids);
    List<StaffResponse> list =
        views.stream().map(v -> StaffPresenter.toResponse(v, locale(), i18n)).toList();
    return Response.ok(ApiEnvelope.ok(list)).build();
  }

  /** Executes paginated staff search using the complex-search contract. */
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

  @POST
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response create(@Valid StaffCreateRequest req) {
    Staff staff = writeService.save(StaffPresenter.toCommand(req));
    StaffView v = readService.getViewByAccountId(staff.getAccountId());
    StaffResponse out = StaffPresenter.toResponse(v, locale(), i18n);
    URI location = uri.getAbsolutePathBuilder().path(staff.getAccountId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(out)).build();
  }

  @PUT
  @Path("/{id}")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid StaffUpdateRequest req) {
    Staff updated = writeService.update(id, StaffPresenter.toCommand(req));
    StaffView v = readService.getViewByAccountId(updated.getAccountId());
    StaffResponse body = StaffPresenter.toResponse(v, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  @PATCH
  @Path("/{id}/status")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response updateStatus(@PathParam("id") @UuidV7 UUID id, @Valid AccountStatusRequest req) {
    writeService.updateStatus(id, req.active());
    return Response.noContent().build();
  }

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
