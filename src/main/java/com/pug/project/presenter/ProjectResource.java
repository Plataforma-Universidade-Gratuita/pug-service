package com.pug.project.presenter;

import com.pug.academic.infra.read.dtos.SchoolView;
import com.pug.academic.presenter.dtos.SchoolResponse;
import com.pug.academic.presenter.mappers.SchoolPresenter;
import com.pug.project.infra.read.dtos.ProjectView;
import com.pug.project.infra.read.dtos.SchoolProjectView;
import com.pug.project.presenter.dtos.ProjectCreateRequest;
import com.pug.project.presenter.dtos.ProjectResponse;
import com.pug.project.presenter.dtos.ProjectUpdateRequest;
import com.pug.project.presenter.dtos.ProjectsBySchoolResponse;
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
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the project is not found
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
   * Retrieves a list of schools associated with a specific project.
   *
   * @param projectId the unique identifier (UUIDv7) of the project
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     SchoolResponse}
   */
  @GET
  @Path("/{projectId}/schools")
  @Authenticated
  public Response listSchoolsByProjectId(@PathParam("projectId") @UuidV7 UUID projectId) {
    List<SchoolView> views = readService.listViewsSchoolsByProjectId(projectId);
    List<SchoolResponse> body =
        views.stream()
            .map(v -> SchoolPresenter.toResponse(v, locale()))
            .collect(Collectors.toList());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a consolidated view of a school and its associated projects.
   *
   * @param schoolId the unique identifier (UUIDv7) of the school
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     ProjectsBySchoolResponse}
   */
  @GET
  @Path("/by-school/{schoolId}")
  @Authenticated
  public Response getBySchool(@PathParam("schoolId") @UuidV7 UUID schoolId) {
    SchoolProjectView view = readService.listViewsBySchool(schoolId);
    ProjectsBySchoolResponse body = ProjectPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a collection of projects.
   *
   * <p>If the optional {@code q} parameter is provided, it executes a full-text search against the
   * project names. If the {@code entityId} parameter is provided, it filters the results by partner
   * entity.
   *
   * @param query the optional search query string
   * @param entityId the optional entity filter
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     ProjectResponse}
   */
  @GET
  @Authenticated
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

  /**
   * Retrieves a list of projects created by a specific account.
   *
   * @param accountId the unique identifier (UUIDv7) of the account
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     ProjectResponse}
   */
  @GET
  @Path("/created-by/{accountId}")
  @Authenticated
  public Response listByCreatedBy(@PathParam("accountId") @UuidV7 UUID accountId) {
    List<ProjectView> views = readService.listViewsByCreatedBy(accountId);
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
   * @throws com.pug.shared.exceptions.DuplicateResourceException if a project with the same name
   *     exists for the entity
   */
  @POST
  @RolesAllowed({"ADMIN", "PARTNER"})
  public Response create(@Valid ProjectCreateRequest req) {
    var cmd =
        new ProjectCreateCommand(
            req.name(),
            req.entityId(),
            req.description(),
            req.createdBy(),
            req.maxParticipants(),
            req.offeredHours(),
            req.schoolId());

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
  @RolesAllowed({"ADMIN", "PARTNER"})
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid ProjectUpdateRequest req) {
    var cmd =
        new ProjectUpdateCommand(
            req.name(),
            req.description(),
            req.maxParticipants(),
            req.offeredHours(),
            req.schoolId());

    writeService.update(id, cmd);
    ProjectView view = readService.getViewById(id);
    ProjectResponse body = ProjectPresenter.toResponse(view, locale(), i18n);

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Cancels a project.
   *
   * @param id the unique identifier (UUID) of the project
   * @return an HTTP 200 OK response with the updated {@link ProjectResponse}
   */
  @PATCH
  @Path("/{id}/cancel")
  @RolesAllowed({"ADMIN", "PARTNER"})
  public Response cancel(@PathParam("id") @UuidV7 UUID id) {
    writeService.transitionStatus(id, com.pug.project.domain.enums.ProjectStatus.CANCELED);
    ProjectView view = readService.getViewById(id);
    return Response.ok(ApiEnvelope.ok(ProjectPresenter.toResponse(view, locale(), i18n))).build();
  }

  /**
   * Completes a project.
   *
   * @param id the unique identifier (UUID) of the project
   * @return an HTTP 200 OK response with the updated {@link ProjectResponse}
   */
  @PATCH
  @Path("/{id}/complete")
  @RolesAllowed({"ADMIN", "PARTNER"})
  public Response complete(@PathParam("id") @UuidV7 UUID id) {
    writeService.transitionStatus(id, com.pug.project.domain.enums.ProjectStatus.COMPLETED);
    ProjectView view = readService.getViewById(id);
    return Response.ok(ApiEnvelope.ok(ProjectPresenter.toResponse(view, locale(), i18n))).build();
  }

  /**
   * Puts a project on hold.
   *
   * @param id the unique identifier (UUID) of the project
   * @return an HTTP 200 OK response with the updated {@link ProjectResponse}
   */
  @PATCH
  @Path("/{id}/hold")
  @RolesAllowed({"ADMIN", "PARTNER"})
  public Response putOnHold(@PathParam("id") @UuidV7 UUID id) {
    writeService.transitionStatus(id, com.pug.project.domain.enums.ProjectStatus.ON_HOLD);
    ProjectView view = readService.getViewById(id);
    return Response.ok(ApiEnvelope.ok(ProjectPresenter.toResponse(view, locale(), i18n))).build();
  }

  /**
   * Resumes a project that is on hold.
   *
   * @param id the unique identifier (UUID) of the project
   * @return an HTTP 200 OK response with the updated {@link ProjectResponse}
   */
  @PATCH
  @Path("/{id}/retake")
  @RolesAllowed({"ADMIN", "PARTNER"})
  public Response retake(@PathParam("id") @UuidV7 UUID id) {
    writeService.transitionStatus(id, com.pug.project.domain.enums.ProjectStatus.PLANNED);
    ProjectView view = readService.getViewById(id);
    return Response.ok(ApiEnvelope.ok(ProjectPresenter.toResponse(view, locale(), i18n))).build();
  }

  /**
   * Starts a project, setting its status to IN_PROGRESS.
   *
   * @param id the unique identifier (UUID) of the project
   * @return an HTTP 200 OK response with the updated {@link ProjectResponse}
   */
  @PATCH
  @Path("/{id}/start")
  @RolesAllowed({"ADMIN", "PARTNER"})
  public Response start(@PathParam("id") @UuidV7 UUID id) {
    writeService.transitionStatus(id, com.pug.project.domain.enums.ProjectStatus.IN_PROGRESS);
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
  @RolesAllowed({"ADMIN", "PARTNER"})
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
