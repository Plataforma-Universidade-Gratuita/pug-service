package br.org.catolicasc.pug.project.presenter;

import br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView;
import br.org.catolicasc.pug.academic.presenter.dtos.AreaOfExpertiseResponse;
import br.org.catolicasc.pug.academic.presenter.mappers.AreaOfExpertisePresenter;
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
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * REST API resource controller for managing project-to-area-of-expertise associations from the
 * project side.
 */
@ApplicationScoped
@Path(ProjectApiPaths.PROJECTS_SCHOOLS)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class ProjectsSchoolsResource {

  @Inject ProjectSchoolService writeService;
  @Inject ProjectSchoolReadService readService;

  @Context HttpHeaders headers;

  @GET
  public Response listAreasOfExpertiseByProjectId(@PathParam("projectId") @UuidV7 UUID projectId) {
    Set<SchoolView> views = readService.listAllSchoolsByProjectId(projectId);
    List<AreaOfExpertiseResponse> body =
        views.stream().map(view -> AreaOfExpertisePresenter.toResponse(view, locale())).toList();
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  @POST
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response createAssociations(
      @PathParam("projectId") @UuidV7 UUID projectId, @Valid ProjectSchoolRequest req) {
    writeService.save(projectId, req.areaOfExpertiseIds());
    return Response.noContent().build();
  }

  @DELETE
  @Path("/{schoolId}")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response deleteAssociation(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("schoolId") @UuidV7 UUID schoolId) {
    writeService.delete(projectId, schoolId);
    return Response.noContent().build();
  }

  @DELETE
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response deleteAllByProject(@PathParam("projectId") @UuidV7 UUID projectId) {
    writeService.deleteAllByProjectId(projectId);
    return Response.noContent().build();
  }

  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
