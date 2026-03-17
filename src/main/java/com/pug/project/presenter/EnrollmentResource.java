package com.pug.project.presenter;

import com.pug.project.infra.read.dtos.EnrollmentView;
import com.pug.project.presenter.dtos.EnrollmentCreateRequest;
import com.pug.project.presenter.dtos.EnrollmentResponse;
import com.pug.project.presenter.mappers.EnrollmentPresenter;
import com.pug.project.service.EnrollmentReadService;
import com.pug.project.service.EnrollmentService;
import com.pug.project.service.dtos.EnrollmentCreateCommand;
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
 * REST API Resource controller for managing Student Enrollments in Projects.
 *
 * <p>Methods are ordered strictly by HTTP verb (GET, POST, PATCH, DELETE) with single-item
 * endpoints preceding collections. Note that Enrollments use a composite key (projectId and
 * studentId).
 */
@ApplicationScoped
@Path("/projects/enrollments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EnrollmentResource {

  @Inject EnrollmentService writeService;
  @Inject EnrollmentReadService readService;
  @Inject I18n i18n;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  @GET
  @Path("/{projectId}/{studentId}")
  public Response get(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("studentId") @UuidV7 UUID studentId) {
    EnrollmentView view = readService.getViewByIds(projectId, studentId);
    EnrollmentResponse body = EnrollmentPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  @GET
  public Response list(
      @QueryParam("projectId") @UuidV7 UUID projectId,
      @QueryParam("studentId") @UuidV7 UUID studentId) {
    List<EnrollmentView> views;

    if (projectId != null) {
      views = readService.listViewsByProjectId(projectId);
    } else if (studentId != null) {
      views = readService.listViewsByStudentId(studentId);
    } else {
      views = readService.listViews();
    }

    List<EnrollmentResponse> body =
        views.stream()
            .map(v -> EnrollmentPresenter.toResponse(v, locale(), i18n))
            .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  @POST
  public Response create(@Valid EnrollmentCreateRequest req) {
    var cmd = new EnrollmentCreateCommand(req.projectId(), req.studentId());
    var created = writeService.save(cmd);

    EnrollmentView view =
        readService.getViewByIds(
            created.getIdentifier().getProjectId(), created.getIdentifier().getStudentId());
    EnrollmentResponse body = EnrollmentPresenter.toResponse(view, locale(), i18n);

    URI location =
        uri.getAbsolutePathBuilder()
            .path(created.getIdentifier().getProjectId().toString())
            .path(created.getIdentifier().getStudentId().toString())
            .build();

    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  @PATCH
  @Path("/{projectId}/{studentId}/accept")
  public Response accept(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("studentId") @UuidV7 UUID studentId) {
    writeService.accept(projectId, studentId);
    EnrollmentView view = readService.getViewByIds(projectId, studentId);
    return Response.ok(ApiEnvelope.ok(EnrollmentPresenter.toResponse(view, locale(), i18n)))
        .build();
  }

  @PATCH
  @Path("/{projectId}/{studentId}/cancel")
  public Response cancel(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("studentId") @UuidV7 UUID studentId) {
    writeService.cancel(projectId, studentId);
    EnrollmentView view = readService.getViewByIds(projectId, studentId);
    return Response.ok(ApiEnvelope.ok(EnrollmentPresenter.toResponse(view, locale(), i18n)))
        .build();
  }

  @PATCH
  @Path("/{projectId}/{studentId}/complete")
  public Response complete(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("studentId") @UuidV7 UUID studentId) {
    writeService.complete(projectId, studentId);
    EnrollmentView view = readService.getViewByIds(projectId, studentId);
    return Response.ok(ApiEnvelope.ok(EnrollmentPresenter.toResponse(view, locale(), i18n)))
        .build();
  }

  @PATCH
  @Path("/{projectId}/{studentId}/exit")
  public Response exit(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("studentId") @UuidV7 UUID studentId) {
    writeService.exit(projectId, studentId);
    EnrollmentView view = readService.getViewByIds(projectId, studentId);
    return Response.ok(ApiEnvelope.ok(EnrollmentPresenter.toResponse(view, locale(), i18n)))
        .build();
  }

  @PATCH
  @Path("/{projectId}/{studentId}/reject")
  public Response reject(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("studentId") @UuidV7 UUID studentId) {
    writeService.reject(projectId, studentId);
    EnrollmentView view = readService.getViewByIds(projectId, studentId);
    return Response.ok(ApiEnvelope.ok(EnrollmentPresenter.toResponse(view, locale(), i18n)))
        .build();
  }

  @PATCH
  @Path("/{projectId}/{studentId}/remove")
  public Response remove(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("studentId") @UuidV7 UUID studentId) {
    writeService.remove(projectId, studentId);
    EnrollmentView view = readService.getViewByIds(projectId, studentId);
    return Response.ok(ApiEnvelope.ok(EnrollmentPresenter.toResponse(view, locale(), i18n)))
        .build();
  }

  @DELETE
  @Path("/{projectId}/{studentId}")
  public Response delete(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("studentId") @UuidV7 UUID studentId) {
    writeService.delete(projectId, studentId);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
