package br.org.catolicasc.pug.project.presenter;

import br.org.catolicasc.pug.academic.constants.AcademicApiPaths;
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
import java.util.stream.Collectors;

/**
 * REST API resource controller for managing project-school associations from the school side.
 *
 * <p>This class exposes nested endpoints rooted at {@code /v1/academic/schools/{schoolId}/projects}
 * to list and remove the relationship between a school and its associated projects. It delegates
 * commands to the {@link ProjectSchoolService} and queries to the {@link ProjectSchoolReadService},
 * adhering to CQRS principles.
 */
@ApplicationScoped
@Path(AcademicApiPaths.SCHOOL_PROJECTS)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class SchoolProjectResource {

  @Inject ProjectSchoolService writeService;
  @Inject ProjectSchoolReadService readService;
  @Inject I18n i18n;

  @Context HttpHeaders headers;

  /**
   * Retrieves the projects associated with a specific school.
   *
   * @param schoolId the unique identifier (UUIDv7) of the school
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     ProjectResponse}
   */
  @GET
  public Response listProjectsBySchoolId(@PathParam("schoolId") @UuidV7 UUID schoolId) {
    Set<ProjectView> views = readService.listAllProjectsBySchoolId(schoolId);
    List<ProjectResponse> body =
        views.stream()
            .map(v -> ProjectPresenter.toResponse(v, locale(), i18n))
            .collect(Collectors.toList());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Removes all project associations for the specified school.
   *
   * @param schoolId the unique identifier (UUIDv7) of the school
   * @return an HTTP 204 No Content response when deletion succeeds
   */
  @DELETE
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response deleteAllBySchool(@PathParam("schoolId") @UuidV7 UUID schoolId) {
    writeService.deleteAllBySchoolId(schoolId);
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
