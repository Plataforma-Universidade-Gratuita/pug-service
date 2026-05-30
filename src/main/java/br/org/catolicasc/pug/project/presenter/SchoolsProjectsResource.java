package br.org.catolicasc.pug.project.presenter;

import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import br.org.catolicasc.pug.project.presenter.dtos.ProjectResponse;
import br.org.catolicasc.pug.project.presenter.mappers.ProjectPresenter;
import br.org.catolicasc.pug.project.service.ProjectSchoolReadService;
import br.org.catolicasc.pug.project.service.ProjectSchoolService;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
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
 * REST API resource controller for managing project associations from the academic area-of-
 * expertise side.
 */
@ApplicationScoped
@Path("/v1/academic/areas-of-expertise/{areaOfExpertiseId}/projects")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class SchoolsProjectsResource {

  @Inject ProjectSchoolService writeService;
  @Inject ProjectSchoolReadService readService;
  @Inject I18n i18n;

  @Context HttpHeaders headers;

  @GET
  public Response listProjectsByAreaOfExpertiseId(
      @PathParam("areaOfExpertiseId") @UuidV7 UUID areaOfExpertiseId) {
    Set<ProjectView> views = readService.listAllProjectsByAreaOfExpertiseId(areaOfExpertiseId);
    List<ProjectResponse> body =
        views.stream().map(view -> ProjectPresenter.toResponse(view, locale(), i18n)).toList();
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  @DELETE
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response deleteAllByAreaOfExpertise(
      @PathParam("areaOfExpertiseId") @UuidV7 UUID areaOfExpertiseId) {
    writeService.deleteAllByAreaOfExpertiseId(areaOfExpertiseId);
    return Response.noContent().build();
  }

  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
