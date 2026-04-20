package br.org.catolicasc.pug.project.presenter;

import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView;
import br.org.catolicasc.pug.academic.presenter.dtos.SchoolResponse;
import br.org.catolicasc.pug.academic.presenter.mappers.SchoolPresenter;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.ProjectBySchool;
import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import br.org.catolicasc.pug.project.presenter.dtos.ProjectBySchoolRequest;
import br.org.catolicasc.pug.project.presenter.dtos.ProjectResponse;
import br.org.catolicasc.pug.project.presenter.mappers.ProjectPresenter;
import br.org.catolicasc.pug.project.service.ProjectBySchoolReadService;
import br.org.catolicasc.pug.project.service.ProjectBySchoolService;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST API Resource controller for managing Project–School associations.
 *
 * <p>This resource is dedicated exclusively to linking existing {@link
 * Project} instances to existing {@link School}
 * instances via the {@link ProjectBySchool} aggregate.
 *
 * <p>Following CQRS principles:
 *
 * <ul>
 *   <li>Write operations delegate to {@link ProjectBySchoolService}.
 *   <li>Read operations delegate to {@link ProjectBySchoolReadService} and use existing read models
 *       ({@link ProjectView}, {@link SchoolView}).
 * </ul>
 */
@ApplicationScoped
@Path("/projects/by-school")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class ProjectBySchoolResource {

  @Inject ProjectBySchoolService writeService;

  @Inject ProjectBySchoolReadService readService;

  @Inject I18n i18n;

  @Context UriInfo uri;

  @Context HttpHeaders headers;

  /**
   * Retrieves all schools associated with a specific project.
   *
   * @param projectId the unique identifier (UUIDv7) of the project
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     SchoolResponse}
   */
  @GET
  @Path("/projects/{projectId}/schools")
  @Authenticated
  public Response listSchoolsByProjectId(@PathParam("projectId") @UuidV7 UUID projectId) {
    Set<SchoolView> views = readService.listAllSchoolsByProjectId(projectId);

    List<SchoolResponse> body =
        views.stream()
            .map(v -> SchoolPresenter.toResponse(v, locale()))
            .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves all projects associated with a specific school.
   *
   * @param schoolId the unique identifier (UUIDv7) of the school
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     ProjectResponse}
   */
  @GET
  @Path("/schools/{schoolId}/projects")
  @Authenticated
  public Response listProjectsBySchoolId(@PathParam("schoolId") @UuidV7 UUID schoolId) {
    Set<ProjectView> views = readService.listAllProjectsBySchoolId(schoolId);

    List<ProjectResponse> body =
        views.stream()
            .map(v -> ProjectPresenter.toResponse(v, locale(), i18n))
            .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Registers associations between a project and one or more schools.
   *
   * <p>This operation is idempotent for existing links: attempting to reassign a school that is
   * already linked to the project will be silently ignored.
   *
   * @param req the validated {@link ProjectBySchoolRequest} payload containing the projectId and
   *     the list of schoolIds to associate
   * @return an HTTP 201 Created response containing the updated list of {@link SchoolResponse}
   *     associated with the project
   */
  @POST
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response createAssociations(@Valid ProjectBySchoolRequest req) {
    writeService.save(req.projectId(), req.schoolIds());

    Set<SchoolView> views = readService.listAllSchoolsByProjectId(req.projectId());
    List<SchoolResponse> body =
        views.stream()
            .map(v -> SchoolPresenter.toResponse(v, locale()))
            .collect(Collectors.toList());

    URI location =
        uri.getAbsolutePathBuilder()
            .path("projects")
            .path(req.projectId().toString())
            .path("schools")
            .build();

    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Removes a specific association between a project and a school.
   *
   * <p>This operation is idempotent: attempting to delete a non-existing association will result in
   * a successful 200 response with an empty payload.
   *
   * @param projectId the unique identifier (UUIDv7) of the project
   * @param schoolId the unique identifier (UUIDv7) of the school
   * @return an HTTP 200 OK response with an empty data payload indicating successful deletion
   */
  @DELETE
  @Path("/projects/{projectId}/schools/{schoolId}")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response deleteAssociation(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("schoolId") @UuidV7 UUID schoolId) {

    writeService.delete(projectId, schoolId);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  /**
   * Removes all school associations for a specific project.
   *
   * <p>This operation is idempotent: if no associations exist, it still returns 200 OK.
   *
   * @param projectId the unique identifier (UUIDv7) of the project
   * @return an HTTP 200 OK response with an empty data payload indicating successful deletion
   */
  @DELETE
  @Path("/projects/{projectId}")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response deleteAllByProject(@PathParam("projectId") @UuidV7 UUID projectId) {
    writeService.deleteAllByProjectId(projectId);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  /**
   * Removes all project associations for a specific school.
   *
   * <p>This operation is idempotent: if no associations exist, it still returns 200 OK.
   *
   * @param schoolId the unique identifier (UUIDv7) of the school
   * @return an HTTP 200 OK response with an empty data payload indicating successful deletion
   */
  @DELETE
  @Path("/schools/{schoolId}")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response deleteAllBySchool(@PathParam("schoolId") @UuidV7 UUID schoolId) {
    writeService.deleteAllBySchoolId(schoolId);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
