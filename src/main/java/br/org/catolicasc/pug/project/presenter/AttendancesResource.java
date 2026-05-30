package br.org.catolicasc.pug.project.presenter;

import br.org.catolicasc.pug.project.infra.read.dtos.AttendanceView;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceComplexSearchRequest;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceComplexSearchResponse;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceCreateRequest;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceResponse;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceValidateRequest;
import br.org.catolicasc.pug.project.presenter.mappers.AttendancePresenter;
import br.org.catolicasc.pug.project.service.AttendancesReadService;
import br.org.catolicasc.pug.project.service.AttendancesService;
import br.org.catolicasc.pug.project.service.dtos.AttendanceComplexSearchCriteria;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.dtos.PageResponse;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
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
import java.util.UUID;

/**
 * REST API resource controller responsible for attendance endpoints.
 *
 * <p>Methods are ordered strictly by HTTP verb with single-item endpoints preceding collection
 * endpoints inside each verb group.
 */
@ApplicationScoped
@Path("/v1/projects/attendances")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class AttendancesResource {

  @Inject AttendancesService writeService;
  @Inject AttendancesReadService readService;
  @Inject I18n i18n;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    AttendanceView view = readService.getViewById(id);
    AttendanceResponse body = AttendancePresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  @GET
  public Response list(@QueryParam("ids") List<@UuidV7 UUID> ids) {
    List<AttendanceView> views =
        CollectionUtils.isEmpty(ids) ? readService.listViews() : readService.listViewsByIds(ids);

    List<AttendanceResponse> body =
        views.stream().map(view -> AttendancePresenter.toResponse(view, locale(), i18n)).toList();

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  @POST
  @Path("/search")
  public Response search(
      @Valid AttendanceComplexSearchRequest req,
      @QueryParam("page") Integer page,
      @QueryParam("size") Integer size) {
    AttendanceComplexSearchCriteria criteria =
        req == null
            ? new AttendanceComplexSearchCriteria(
                List.of(), List.of(), List.of(), List.of(), null, null, null, null)
            : new AttendanceComplexSearchCriteria(
                req.projectIds(),
                req.formerStudentIds(),
                req.statuses(),
                req.validatedByIds(),
                req.durationFrom(),
                req.durationTo(),
                req.dateFrom(),
                req.dateTo());

    PageResult<AttendanceView> result =
        readService.search(
            criteria, new PageQuery(page == null ? 0 : page, size == null ? 25 : size));

    PageResponse<AttendanceComplexSearchResponse> body =
        new PageResponse<>(
            result.content().stream()
                .map(view -> AttendancePresenter.toComplexSearchResponse(view, locale(), i18n))
                .toList(),
            result.page(),
            result.size(),
            result.totalElements(),
            result.totalPages());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  @POST
  public Response create(@Valid AttendanceCreateRequest req) {
    var created = writeService.save(AttendancePresenter.toCommand(req));

    AttendanceView view = readService.getViewById(created.getId());
    AttendanceResponse body = AttendancePresenter.toResponse(view, locale(), i18n);

    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  @PATCH
  @Path("/{id}/validate")
  public Response validate(@PathParam("id") @UuidV7 UUID id, @Valid AttendanceValidateRequest req) {
    writeService.validate(id, AttendancePresenter.toCommand(req));
    AttendanceView view = readService.getViewById(id);
    AttendanceResponse body = AttendancePresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  @DELETE
  @Path("/{id}")
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.noContent().build();
  }

  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
