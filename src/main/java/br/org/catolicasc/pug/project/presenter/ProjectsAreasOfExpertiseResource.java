package br.org.catolicasc.pug.project.presenter;

import br.org.catolicasc.pug.academic.infra.read.dtos.AreaOfExpertiseView;
import br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise.AreaOfExpertiseResponse;
import br.org.catolicasc.pug.academic.presenter.mappers.AreaOfExpertisePresenter;
import br.org.catolicasc.pug.project.presenter.dtos.projects.ProjectAreaOfExpertiseRequest;
import br.org.catolicasc.pug.project.service.ProjectAreaOfExpertiseReadService;
import br.org.catolicasc.pug.project.service.ProjectAreaOfExpertiseService;
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
 *
 * <p>This resource exposes the project-facing view of the association so callers can list, create,
 * remove, or clear every area of expertise linked to a project.
 */
@ApplicationScoped
@Path("/v1/projects/{projectId}/areas-of-expertise")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class ProjectsAreasOfExpertiseResource {

  @Inject ProjectAreaOfExpertiseService writeService;
  @Inject ProjectAreaOfExpertiseReadService readService;

  @Context HttpHeaders headers;

  /** Lists every academic area of expertise associated with the provided project identifier. */
  @GET
  public Response listAreasOfExpertiseByProjectId(@PathParam("projectId") @UuidV7 UUID projectId) {
    Set<AreaOfExpertiseView> views = readService.listAllAreasOfExpertiseByProjectId(projectId);
    List<AreaOfExpertiseResponse> body =
        views.stream().map(view -> AreaOfExpertisePresenter.toResponse(view, locale())).toList();
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  @POST
  @RolesAllowed({"ADMIN", "PARTNER"})
  public Response createAssociations(
      @PathParam("projectId") @UuidV7 UUID projectId, @Valid ProjectAreaOfExpertiseRequest req) {
    writeService.save(projectId, req.areaOfExpertiseIds());
    return Response.noContent().build();
  }

  @DELETE
  @Path("/{areaOfExpertiseId}")
  @RolesAllowed({"ADMIN", "PARTNER"})
  public Response deleteAssociation(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("areaOfExpertiseId") @UuidV7 UUID areaOfExpertiseId) {
    writeService.delete(projectId, areaOfExpertiseId);
    return Response.noContent().build();
  }

  @DELETE
  @RolesAllowed({"ADMIN", "PARTNER"})
  public Response deleteAllByProject(@PathParam("projectId") @UuidV7 UUID projectId) {
    writeService.deleteAllByProjectId(projectId);
    return Response.noContent().build();
  }

  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
