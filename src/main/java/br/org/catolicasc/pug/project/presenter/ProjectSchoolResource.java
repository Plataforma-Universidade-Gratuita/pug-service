package br.org.catolicasc.pug.project.presenter;

import br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView;
import br.org.catolicasc.pug.academic.presenter.dtos.SchoolResponse;
import br.org.catolicasc.pug.academic.presenter.mappers.SchoolPresenter;
import br.org.catolicasc.pug.project.constants.ProjectApiPaths;
import br.org.catolicasc.pug.project.presenter.dtos.ProjectSchoolRequest;
import br.org.catolicasc.pug.project.service.ProjectSchoolReadService;
import br.org.catolicasc.pug.project.service.ProjectSchoolService;
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
 * REST API resource controller for managing project-school associations from the project side.
 *
 * <p>This class exposes nested endpoints rooted at {@code /v1/projects/{projectId}/schools} to
 * list, create, and remove the relationship between a project and academic schools. It delegates
 * commands to the {@link ProjectSchoolService} and queries to the {@link ProjectSchoolReadService},
 * adhering to CQRS principles.
 */
@ApplicationScoped
@Path(ProjectApiPaths.PROJECT_SCHOOLS)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class ProjectSchoolResource {

  @Inject ProjectSchoolService writeService;
  @Inject ProjectSchoolReadService readService;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  /**
   * Retrieves the schools associated with a specific project.
   *
   * @param projectId the unique identifier (UUIDv7) of the project
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     SchoolResponse}
   */
  @GET
  public Response listSchoolsByProjectId(@PathParam("projectId") @UuidV7 UUID projectId) {
    Set<SchoolView> views = readService.listAllSchoolsByProjectId(projectId);
    List<SchoolResponse> body =
        views.stream()
            .map(v -> SchoolPresenter.toResponse(v, locale()))
            .collect(Collectors.toList());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Creates project-school associations for the specified project.
   *
   * <p>The project identifier is supplied in the route and the request payload contains only the
   * target school identifiers.
   *
   * @param projectId the unique identifier (UUIDv7) of the project
   * @param req the validated {@link ProjectSchoolRequest} containing the school identifiers
   * @return an HTTP 201 Created response containing a {@code Location} header and the resulting
   *     list of associated {@link SchoolResponse}
   */
  @POST
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response createAssociations(
      @PathParam("projectId") @UuidV7 UUID projectId, @Valid ProjectSchoolRequest req) {
    writeService.save(projectId, req.schoolIds());

    Set<SchoolView> views = readService.listAllSchoolsByProjectId(projectId);
    List<SchoolResponse> body =
        views.stream()
            .map(v -> SchoolPresenter.toResponse(v, locale()))
            .collect(Collectors.toList());

    URI location =
        uri.getBaseUriBuilder()
            .path(ProjectApiPaths.VERSION.substring(1))
            .path("projects")
            .path(projectId.toString())
            .path("schools")
            .build();

    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Removes a specific association between a project and a school.
   *
   * @param projectId the unique identifier (UUIDv7) of the project
   * @param schoolId the unique identifier (UUIDv7) of the school
   * @return an HTTP 204 No Content response when deletion succeeds
   */
  @DELETE
  @Path(ProjectApiPaths.PROJECT_SCHOOL_ITEM_SEGMENT)
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response deleteAssociation(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("schoolId") @UuidV7 UUID schoolId) {
    writeService.delete(projectId, schoolId);
    return Response.noContent().build();
  }

  /**
   * Removes all school associations for the specified project.
   *
   * @param projectId the unique identifier (UUIDv7) of the project
   * @return an HTTP 204 No Content response when deletion succeeds
   */
  @DELETE
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response deleteAllByProject(@PathParam("projectId") @UuidV7 UUID projectId) {
    writeService.deleteAllByProjectId(projectId);
    return Response.noContent().build();
  }

  /**
   * Determines the preferred locale from the incoming request headers.
   *
   * @return the resolved {@link Locale}
   */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
