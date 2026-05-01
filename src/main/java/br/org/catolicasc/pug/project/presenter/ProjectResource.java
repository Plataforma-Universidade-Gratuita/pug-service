package br.org.catolicasc.pug.project.presenter;

import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import br.org.catolicasc.pug.project.presenter.dtos.ProjectCreateRequest;
import br.org.catolicasc.pug.project.presenter.dtos.ProjectResponse;
import br.org.catolicasc.pug.project.presenter.dtos.ProjectUpdateRequest;
import br.org.catolicasc.pug.project.presenter.mappers.ProjectPresenter;
import br.org.catolicasc.pug.project.service.ProjectReadService;
import br.org.catolicasc.pug.project.service.ProjectService;
import br.org.catolicasc.pug.project.service.dtos.ProjectCreateCommand;
import br.org.catolicasc.pug.project.service.dtos.ProjectUpdateCommand;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
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

  /**
   * Retrieves a specific project by its unique UUID identifier.
   *
   * @param id the unique identifier (UUIDv7) of the project
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     ProjectResponse}
   * @throws ResourceNotFoundException if the project is not found
   */
  @GET
  @Path("/{id}")
  @Authenticated
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    ProjectView view = readService.getViewById(id);
    ProjectResponse body = ProjectPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves projects, optionally filtered by query parameters.
   *
   * <p>When {@code createdBy} is provided, this endpoint returns the projects created by that
   * account. Otherwise, it filters by {@code entityId}, falls back to full-text search with {@code
   * q}, or lists all projects when no filters are supplied.
   *
   * @param query the optional search query string
   * @param entityId the optional entity filter
   * @param createdBy the optional creator account identifier
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     ProjectResponse}
   */
  @GET
  @Authenticated
  public Response list(
      @QueryParam("q") String query,
      @QueryParam("entityId") @UuidV7 UUID entityId,
      @QueryParam("createdBy") @UuidV7 UUID createdBy) {
    List<ProjectView> views;

    if (createdBy != null) {
      views = readService.listViewsByCreatedBy(createdBy);
    } else if (entityId != null) {
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

  /**
   * Registers a new project within the platform.
   *
   * @param req the validated {@link ProjectCreateRequest}
   * @return an HTTP 201 Created response containing the created {@link ProjectResponse}
   * @throws DuplicateResourceException if a project with the same name exists for the entity
   */
  @POST
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response create(@Valid ProjectCreateRequest req) {
    ProjectCreateCommand cmd = ProjectPresenter.toCommand(req);
    var created = writeService.save(cmd);
    ProjectView view = readService.getViewById(created.getId());
    ProjectResponse body = ProjectPresenter.toResponse(view, locale(), i18n);

    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Updates an existing project's details.
   *
   * @param id the unique identifier (UUID) of the project
   * @param req the validated {@link ProjectUpdateRequest}
   * @return an HTTP 200 OK response containing the updated {@link ProjectResponse}
   */
  @PUT
  @Path("/{id}")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid ProjectUpdateRequest req) {
    ProjectUpdateCommand cmd = ProjectPresenter.toCommand(req);
    writeService.update(id, cmd);
    ProjectView view = readService.getViewById(id);
    ProjectResponse body = ProjectPresenter.toResponse(view, locale(), i18n);

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Applies a partial update to an existing project.
   *
   * <p>Non-null descriptive fields are forwarded to the standard update flow. When {@code status}
   * is provided, the endpoint executes the corresponding project lifecycle transition through the
   * application service.
   *
   * @param id the unique identifier (UUIDv7) of the project
   * @param req the validated {@link ProjectUpdateRequest} containing the fields to change
   * @return an HTTP 200 OK response containing the updated {@link ProjectResponse}
   * @throws ResourceNotFoundException if the project is not found
   */
  @PATCH
  @Path("/{id}")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response patch(@PathParam("id") @UuidV7 UUID id, @Valid ProjectUpdateRequest req) {
    if (req.name() != null
        || req.description() != null
        || req.maxParticipants() != null
        || req.offeredHours() != null) {
      ProjectUpdateCommand cmd = ProjectPresenter.toCommand(req);
      writeService.update(id, cmd);
    }

    if (req.status() != null) {
      writeService.transitionStatus(id, req.status());
    }

    ProjectView view = readService.getViewById(id);
    return Response.ok(ApiEnvelope.ok(ProjectPresenter.toResponse(view, locale(), i18n))).build();
  }

  /**
   * Permanently removes a project from the system.
   *
   * @param id the unique identifier (UUID) of the project
   * @return an HTTP 200 OK response
   */
  @DELETE
  @Path("/{id}")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
