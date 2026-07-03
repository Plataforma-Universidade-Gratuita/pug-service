/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.presenter;

import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import br.org.catolicasc.pug.project.presenter.dtos.projects.ProjectResponse;
import br.org.catolicasc.pug.project.presenter.mappers.ProjectPresenter;
import br.org.catolicasc.pug.project.service.ProjectAreaOfExpertiseReadService;
import br.org.catolicasc.pug.project.service.ProjectAreaOfExpertiseService;
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
 * REST API resource controller for managing project associations from the academic
 * area-of-expertise side.
 *
 * <p>This resource exposes the academic-facing view of the project-to-area-of-expertise association
 * so callers can list or clear every project linked to a specific area of expertise.
 */
@ApplicationScoped
@Path("/v1/academic/areas-of-expertise/{areaOfExpertiseId}/projects")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class AreasOfExpertiseProjectsResource {

  @Inject ProjectAreaOfExpertiseService writeService;
  @Inject ProjectAreaOfExpertiseReadService readService;
  @Inject I18n i18n;

  @Context HttpHeaders headers;

  /**
   * Lists every project currently associated with the provided academic area-of-expertise
   * identifier.
   */
  @GET
  public Response listProjectsByAreaOfExpertiseId(
      @PathParam("areaOfExpertiseId") @UuidV7 UUID areaOfExpertiseId) {
    Set<ProjectView> views = readService.listAllProjectsByAreaOfExpertiseId(areaOfExpertiseId);
    List<ProjectResponse> body =
        views.stream().map(view -> ProjectPresenter.toResponse(view, locale(), i18n)).toList();
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /** Deletes every project association linked to the provided academic area-of-expertise. */
  @DELETE
  @RolesAllowed({"ADMIN", "PARTNER"})
  public Response deleteAllByAreaOfExpertise(
      @PathParam("areaOfExpertiseId") @UuidV7 UUID areaOfExpertiseId) {
    writeService.deleteAllByAreaOfExpertiseId(areaOfExpertiseId);
    return Response.noContent().build();
  }

  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
