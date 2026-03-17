package com.pug.project.presenter;

import com.pug.project.infra.read.dtos.AttendanceView;
import com.pug.project.presenter.dtos.AttendanceCreateRequest;
import com.pug.project.presenter.dtos.AttendanceResponse;
import com.pug.project.presenter.dtos.AttendanceValidateRequest;
import com.pug.project.presenter.mappers.AttendancePresenter;
import com.pug.project.service.AttendanceReadService;
import com.pug.project.service.AttendanceService;
import com.pug.project.service.dtos.AttendanceCreateCommand;
import com.pug.project.service.dtos.AttendanceValidateCommand;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.utils.PresenterUtils;
import com.pug.shared.validation.UuidV7;
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
import java.util.stream.Collectors;

/**
 * REST API Resource controller for managing student Attendances.
 *
 * <p>Methods are ordered strictly by HTTP verb (GET, POST, PATCH, DELETE) with single-item
 * endpoints preceding collections.
 */
@ApplicationScoped
@Path("/projects/attendances")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AttendanceResource {

  @Inject AttendanceService writeService;
  @Inject AttendanceReadService readService;
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
  public Response list(
      @QueryParam("projectId") @UuidV7 UUID projectId,
      @QueryParam("studentId") @UuidV7 UUID studentId) {
    List<AttendanceView> views;

    if (projectId != null) {
      views = readService.listViewsByProjectId(projectId);
    } else if (studentId != null) {
      views = readService.listViewsByStudentId(studentId);
    } else {
      views = readService.listViews();
    }

    List<AttendanceResponse> body =
        views.stream()
            .map(v -> AttendancePresenter.toResponse(v, locale(), i18n))
            .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  @POST
  public Response create(@Valid AttendanceCreateRequest req) {
    var cmd = new AttendanceCreateCommand(req.projectId(), req.studentId(), req.duration());
    var created = writeService.save(cmd);

    AttendanceView view = readService.getViewById(created.getId());
    AttendanceResponse body = AttendancePresenter.toResponse(view, locale(), i18n);

    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  @PATCH
  @Path("/{id}/validate")
  public Response validate(@PathParam("id") @UuidV7 UUID id, @Valid AttendanceValidateRequest req) {
    var cmd =
        new AttendanceValidateCommand(
            req.validatorId(), req.latitude(), req.longitude(), req.qrValidationHash());

    writeService.validate(id, cmd);
    AttendanceView view = readService.getViewById(id);
    AttendanceResponse body = AttendancePresenter.toResponse(view, locale(), i18n);

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  @DELETE
  @Path("/{id}")
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
