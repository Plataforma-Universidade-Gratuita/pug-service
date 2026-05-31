package br.org.catolicasc.pug.project.presenter;

import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import br.org.catolicasc.pug.project.presenter.dtos.projects.ProjectComplexSearchRequest;
import br.org.catolicasc.pug.project.presenter.dtos.projects.ProjectComplexSearchResponse;
import br.org.catolicasc.pug.project.presenter.dtos.projects.ProjectCreateRequest;
import br.org.catolicasc.pug.project.presenter.dtos.projects.ProjectResponse;
import br.org.catolicasc.pug.project.presenter.dtos.projects.ProjectUpdateRequest;
import br.org.catolicasc.pug.project.presenter.mappers.ProjectPresenter;
import br.org.catolicasc.pug.project.service.ProjectReadService;
import br.org.catolicasc.pug.project.service.ProjectService;
import br.org.catolicasc.pug.project.service.dtos.projects.ProjectComplexSearchCriteria;
import br.org.catolicasc.pug.project.service.dtos.projects.ProjectCreateCommand;
import br.org.catolicasc.pug.project.service.dtos.projects.ProjectUpdateCommand;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.dtos.PageResponse;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

/**
 * REST API resource controller for managing project endpoints.
 *
 * <p>This resource exposes project lookup, collection listing, creator/entity filters,
 * complex-search, creation, updates, status transitions, and deletion. Methods are ordered strictly
 * by HTTP verb with single-item endpoints preceding collection endpoints within each verb group.
 */
@ApplicationScoped
@Path("/v1/projects")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProjectsResource {

  @Inject ProjectService writeService;
  @Inject ProjectReadService readService;
  @Inject I18n i18n;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  /** Returns a single project by identifier. */
  @GET
  @Path("/{id}")
  @Authenticated
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    ProjectView view = readService.getViewById(id);
    return Response.ok(ApiEnvelope.ok(ProjectPresenter.toResponse(view, locale(), i18n))).build();
  }

  /** Lists every project or restricts the result to the provided identifiers. */
  @GET
  @Authenticated
  public Response list(@QueryParam("ids") List<@UuidV7 UUID> ids) {
    List<ProjectView> views =
        CollectionUtils.isEmpty(ids) ? readService.listViews() : readService.listViewsByIds(ids);

    List<ProjectResponse> body =
        views.stream().map(view -> ProjectPresenter.toResponse(view, locale(), i18n)).toList();

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /** Lists projects linked to the provided partner entity identifier. */
  @GET
  @Path("/entities/{entityId}")
  @Authenticated
  public Response listByEntity(@PathParam("entityId") @UuidV7 UUID entityId) {
    List<ProjectResponse> body =
        readService.listViewsByEntityId(entityId).stream()
            .map(view -> ProjectPresenter.toResponse(view, locale(), i18n))
            .toList();

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /** Lists projects created by the provided account identifier. */
  @GET
  @Path("/creators/{createdById}")
  @Authenticated
  public Response listByCreator(@PathParam("createdById") @UuidV7 UUID createdById) {
    List<ProjectResponse> body =
        readService.listViewsByCreatedBy(createdById).stream()
            .map(view -> ProjectPresenter.toResponse(view, locale(), i18n))
            .toList();

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Executes the paginated project complex-search flow using the optional filters defined by the
   * presenter contract.
   */
  @POST
  @Path("/search")
  @Authenticated
  public Response search(
      @Valid ProjectComplexSearchRequest req,
      @QueryParam("page") Integer page,
      @QueryParam("size") Integer size) {
    ProjectComplexSearchCriteria criteria =
        new ProjectComplexSearchCriteria(
            req == null ? null : req.name(),
            req == null ? List.of() : req.entityIds(),
            req == null ? null : req.description(),
            req == null ? List.of() : req.createdByIds(),
            req == null ? null : req.dateFrom(),
            req == null ? null : req.dateTo(),
            req == null ? List.of() : req.statuses(),
            req == null ? null : req.maxOfferedHours(),
            req == null ? null : req.minOfferedHours());

    PageResult<ProjectView> result =
        readService.search(
            new ProjectComplexSearchCriteria(
                criteria.name(),
                criteria.entityIds(),
                criteria.description(),
                criteria.createdByIds(),
                criteria.dateFrom(),
                criteria.dateTo(),
                criteria.statuses(),
                criteria.maxOfferedHours(),
                criteria.minOfferedHours()),
            new PageQuery(page == null ? 0 : page, size == null ? 25 : size));

    PageResponse<ProjectComplexSearchResponse> body =
        new PageResponse<>(
            result.content().stream()
                .map(view -> ProjectPresenter.toComplexSearchResponse(view, locale(), i18n))
                .toList(),
            result.page(),
            result.size(),
            result.totalElements(),
            result.totalPages());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /** Creates a new project and returns the canonical response payload. */
  @POST
  @RolesAllowed({"ADMIN", "PARTNER"})
  public Response create(@Valid ProjectCreateRequest req) {
    ProjectCreateCommand cmd = ProjectPresenter.toCommand(req);
    var created = writeService.save(cmd);
    ProjectView view = readService.getViewById(created.getId());
    ProjectResponse body = ProjectPresenter.toResponse(view, locale(), i18n);

    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /** Updates a project without changing its lifecycle status. */
  @PUT
  @Path("/{id}")
  @RolesAllowed({"ADMIN", "PARTNER"})
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid ProjectUpdateRequest req) {
    ProjectUpdateCommand cmd = ProjectPresenter.toCommand(req);
    writeService.update(id, cmd);
    ProjectView view = readService.getViewById(id);
    return Response.ok(ApiEnvelope.ok(ProjectPresenter.toResponse(view, locale(), i18n))).build();
  }

  /** Updates only the lifecycle status of an existing project. */
  @PATCH
  @Path("/{id}/status")
  @RolesAllowed({"ADMIN", "PARTNER"})
  public Response updateStatus(@PathParam("id") @UuidV7 UUID id, @NotNull ProjectStatus status) {
    writeService.transitionStatus(id, status);
    ProjectView view = readService.getViewById(id);
    return Response.ok(ApiEnvelope.ok(ProjectPresenter.toResponse(view, locale(), i18n))).build();
  }

  /** Deletes a project by identifier. */
  @DELETE
  @Path("/{id}")
  @RolesAllowed({"ADMIN", "PARTNER"})
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.noContent().build();
  }

  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
