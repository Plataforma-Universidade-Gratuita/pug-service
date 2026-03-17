package com.pug.project.presenter;

import com.pug.project.infra.read.dtos.ProjectView;
import com.pug.project.presenter.dtos.ProjectCreateRequest;
import com.pug.project.presenter.dtos.ProjectResponse;
import com.pug.project.presenter.dtos.ProjectUpdateRequest;
import com.pug.project.presenter.mappers.ProjectPresenter;
import com.pug.project.service.ProjectReadService;
import com.pug.project.service.ProjectService;
import com.pug.project.service.dtos.ProjectCreateCommand;
import com.pug.project.service.dtos.ProjectUpdateCommand;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.utils.PresenterUtils;
import com.pug.shared.utils.StringUtils;
import com.pug.shared.validation.UuidV7;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
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
import java.util.stream.Collectors;

/**
 * REST API Resource controller for managing Projects.
 *
 * <p>Methods are ordered strictly by HTTP verb (GET, POST, PUT, PATCH, DELETE) with single-item
 * endpoints preceding collections.
 */
@ApplicationScoped
@Path("/projects")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProjectResource {

  @Inject ProjectService writeService;
  @Inject ProjectReadService readService;
  @Inject I18n i18n;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    ProjectView view = readService.getViewById(id);
    ProjectResponse body = ProjectPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  @GET
  public Response list(
      @QueryParam("q") String query, @QueryParam("entityId") @UuidV7 UUID entityId) {
    List<ProjectView> views;

    if (entityId != null) {
      views = readService.listViewsByEntityId(entityId);
    } else if (StringUtils.isNotEmpty(query)) {
      views = readService.searchByName(query);
    } else {
      views = readService.listViews();
    }

    List<ProjectResponse> body =
        views.stream()
            .map(v -> ProjectPresenter.toResponse(v, locale(), i18n))
            .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  @POST
  public Response create(@Valid ProjectCreateRequest req) {
    var cmd =
        new ProjectCreateCommand(
            req.name(),
            req.entityId(),
            req.description(),
            req.createdBy(),
            req.maxParticipants(),
            req.offeredHours());

    var created = writeService.save(cmd);
    ProjectView view = readService.getViewById(created.getId());
    ProjectResponse body = ProjectPresenter.toResponse(view, locale(), i18n);

    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid ProjectUpdateRequest req) {
    var cmd =
        new ProjectUpdateCommand(
            req.name(), req.description(), req.maxParticipants(), req.offeredHours());

    writeService.update(id, cmd);
    ProjectView view = readService.getViewById(id);
    ProjectResponse body = ProjectPresenter.toResponse(view, locale(), i18n);

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  @PATCH
  @Path("/{id}/cancel")
  public Response cancel(@PathParam("id") @UuidV7 UUID id) {
    writeService.cancel(id);
    ProjectView view = readService.getViewById(id);
    return Response.ok(ApiEnvelope.ok(ProjectPresenter.toResponse(view, locale(), i18n))).build();
  }

  @PATCH
  @Path("/{id}/complete")
  public Response complete(@PathParam("id") @UuidV7 UUID id) {
    writeService.complete(id);
    ProjectView view = readService.getViewById(id);
    return Response.ok(ApiEnvelope.ok(ProjectPresenter.toResponse(view, locale(), i18n))).build();
  }

  @PATCH
  @Path("/{id}/hold")
  public Response putOnHold(@PathParam("id") @UuidV7 UUID id) {
    writeService.putOnHold(id);
    ProjectView view = readService.getViewById(id);
    return Response.ok(ApiEnvelope.ok(ProjectPresenter.toResponse(view, locale(), i18n))).build();
  }

  @PATCH
  @Path("/{id}/retake")
  public Response retake(@PathParam("id") @UuidV7 UUID id) {
    writeService.retake(id);
    ProjectView view = readService.getViewById(id);
    return Response.ok(ApiEnvelope.ok(ProjectPresenter.toResponse(view, locale(), i18n))).build();
  }

  @PATCH
  @Path("/{id}/start")
  public Response start(@PathParam("id") @UuidV7 UUID id) {
    writeService.start(id);
    ProjectView view = readService.getViewById(id);
    return Response.ok(ApiEnvelope.ok(ProjectPresenter.toResponse(view, locale(), i18n))).build();
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
