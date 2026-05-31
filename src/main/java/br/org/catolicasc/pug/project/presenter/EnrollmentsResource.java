package br.org.catolicasc.pug.project.presenter;

import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.domain.enums.ProjectsErrorCodes;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentIdentifier;
import br.org.catolicasc.pug.project.infra.read.dtos.EnrollmentView;
import br.org.catolicasc.pug.project.presenter.dtos.enrollments.EnrollmentComplexSearchRequest;
import br.org.catolicasc.pug.project.presenter.dtos.enrollments.EnrollmentComplexSearchResponse;
import br.org.catolicasc.pug.project.presenter.dtos.enrollments.EnrollmentResponse;
import br.org.catolicasc.pug.project.presenter.dtos.enrollments.EnrollmentUpdateStatusRequest;
import br.org.catolicasc.pug.project.presenter.mappers.EnrollmentPresenter;
import br.org.catolicasc.pug.project.service.EnrollmentsReadService;
import br.org.catolicasc.pug.project.service.EnrollmentsService;
import br.org.catolicasc.pug.project.service.dtos.enrollments.EnrollmentComplexSearchCriteria;
import br.org.catolicasc.pug.project.service.dtos.enrollments.EnrollmentCreateCommand;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.dtos.PageResponse;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
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
import java.util.Set;
import java.util.UUID;

/**
 * REST API resource controller for enrollment endpoints.
 *
 * <p>This resource exposes the enrollment flows used by administrators, staff members, and former
 * students, including direct lookup, self-service lookup, collection reads, status transitions,
 * creation, deletion, and paginated complex search. Methods are ordered strictly by HTTP verb with
 * single-item endpoints preceding collection endpoints inside each verb group.
 */
@ApplicationScoped
@Path("/v1/projects")
@Produces(MediaType.APPLICATION_JSON)
public class EnrollmentsResource {

  private static final Set<EnrollmentStatus> ADMIN_ALLOWED_STATUSES =
      Set.of(
          EnrollmentStatus.REJECTED,
          EnrollmentStatus.APPROVED,
          EnrollmentStatus.REMOVED,
          EnrollmentStatus.COMPLETED);

  @Inject AuthService authService;
  @Inject EnrollmentsService writeService;
  @Inject EnrollmentsReadService readService;
  @Inject I18n i18n;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  /** Returns a single enrollment identified by project and former-student identifiers. */
  @GET
  @Path("/{projectId}/enrollments/{formerStudentId}")
  @RolesAllowed({"ADMIN", "PARTNER"})
  public Response get(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("formerStudentId") @UuidV7 UUID formerStudentId) {
    EnrollmentView view = readService.getViewByIds(projectId, formerStudentId);
    return Response.ok(ApiEnvelope.ok(EnrollmentPresenter.toResponse(view, locale(), i18n)))
        .build();
  }

  /** Returns the current former student's enrollment for the provided project. */
  @GET
  @Path("/{projectId}/enrollments/me")
  @RolesAllowed("FORMER_STUDENT")
  public Response getMine(@PathParam("projectId") @UuidV7 UUID projectId) {
    UUID formerStudentAccountId = authService.getCurrentAccountId();
    EnrollmentView view = readService.getViewByIds(projectId, formerStudentAccountId);
    return Response.ok(ApiEnvelope.ok(EnrollmentPresenter.toResponse(view, locale(), i18n)))
        .build();
  }

  /** Lists enrollments, optionally filtering by project or former-student identifier. */
  @GET
  @Path("/enrollments")
  @RolesAllowed({"ADMIN", "PARTNER"})
  public Response list(
      @QueryParam("projectId") @UuidV7 UUID projectId,
      @QueryParam("formerStudentId") @UuidV7 UUID formerStudentId) {
    List<EnrollmentView> views =
        projectId != null
            ? readService.listViewsByProjectId(projectId)
            : formerStudentId != null
                ? readService.listViewsByFormerStudentId(formerStudentId)
                : readService.listViews();

    List<EnrollmentResponse> body =
        views.stream().map(view -> EnrollmentPresenter.toResponse(view, locale(), i18n)).toList();
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /** Lists every enrollment belonging to the authenticated former student. */
  @GET
  @Path("/enrollments/me")
  @RolesAllowed("FORMER_STUDENT")
  public Response listMine() {
    UUID formerStudentAccountId = authService.getCurrentAccountId();
    List<EnrollmentResponse> body =
        readService.listViewsByFormerStudentId(formerStudentAccountId).stream()
            .map(view -> EnrollmentPresenter.toResponse(view, locale(), i18n))
            .toList();
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /** Creates an enrollment for the authenticated former student or an explicit admin target. */
  @POST
  @Path("/{projectId}/enrollments")
  @RolesAllowed({"ADMIN", "FORMER_STUDENT"})
  public Response create(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @QueryParam("formerStudentId") @UuidV7 UUID formerStudentId) {
    EnrollmentCreateCommand cmd = EnrollmentPresenter.toCommand(projectId, formerStudentId);
    Enrollment created = writeService.save(cmd);

    EnrollmentView view =
        readService.getViewByIds(
            created.getIdentifier().getProjectId(), created.getIdentifier().getFormerStudentId());

    URI location =
        uri.getAbsolutePathBuilder()
            .path(created.getIdentifier().getFormerStudentId().toString())
            .build();

    return Response.created(location)
        .entity(ApiEnvelope.created(EnrollmentPresenter.toResponse(view, locale(), i18n)))
        .build();
  }

  /**
   * Executes the paginated enrollment complex-search flow using the optional presenter-level
   * filters accepted by the project module contract.
   */
  @POST
  @Path("/enrollments/search")
  @RolesAllowed({"ADMIN", "PARTNER"})
  @Consumes(MediaType.APPLICATION_JSON)
  public Response search(
      @Valid EnrollmentComplexSearchRequest req,
      @QueryParam("page") Integer page,
      @QueryParam("size") Integer size) {
    EnrollmentComplexSearchCriteria criteria =
        req == null
            ? new EnrollmentComplexSearchCriteria(
                List.of(), List.of(), List.of(), null, null, null, null)
            : new EnrollmentComplexSearchCriteria(
                req.projectIds(),
                req.formerStudentIds(),
                req.statuses(),
                req.dateFrom(),
                req.dateTo(),
                req.periodFrom(),
                req.periodTo());

    PageResult<EnrollmentView> result =
        readService.search(
            criteria, new PageQuery(page == null ? 0 : page, size == null ? 25 : size));

    PageResponse<EnrollmentComplexSearchResponse> body =
        new PageResponse<>(
            result.content().stream()
                .map(view -> EnrollmentPresenter.toComplexSearchResponse(view, locale(), i18n))
                .toList(),
            result.page(),
            result.size(),
            result.totalElements(),
            result.totalPages());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /** Updates an enrollment status using the administrative transition rules. */
  @PATCH
  @Path("/{projectId}/enrollments/{formerStudentId}")
  @RolesAllowed({"ADMIN", "PARTNER"})
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateStatus(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("formerStudentId") @UuidV7 UUID formerStudentId,
      @Valid EnrollmentUpdateStatusRequest req) {
    if (!ADMIN_ALLOWED_STATUSES.contains(req.status())) {
      throw new BusinessRuleException(ProjectsErrorCodes.INVALID_ENROLLMENT_STATUS_UPDATE);
    }

    writeService.changeStatus(
        EnrollmentIdentifier.builder()
            .projectId(projectId)
            .formerStudentId(formerStudentId)
            .build(),
        req.status());

    EnrollmentView view = readService.getViewByIds(projectId, formerStudentId);
    return Response.ok(ApiEnvelope.ok(EnrollmentPresenter.toResponse(view, locale(), i18n)))
        .build();
  }

  /** Updates the authenticated former student's enrollment status using self-service rules. */
  @PATCH
  @Path("/{projectId}/enrollments/me")
  @RolesAllowed("FORMER_STUDENT")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateStatusMine(
      @PathParam("projectId") @UuidV7 UUID projectId, @Valid EnrollmentUpdateStatusRequest req) {
    if (req.status() != EnrollmentStatus.EXITED) {
      throw new BusinessRuleException(ProjectsErrorCodes.INVALID_ENROLLMENT_STATUS_UPDATE);
    }

    UUID formerStudentAccountId = authService.getCurrentAccountId();
    writeService.changeStatus(
        EnrollmentIdentifier.builder()
            .projectId(projectId)
            .formerStudentId(formerStudentAccountId)
            .build(),
        req.status());

    EnrollmentView view = readService.getViewByIds(projectId, formerStudentAccountId);
    return Response.ok(ApiEnvelope.ok(EnrollmentPresenter.toResponse(view, locale(), i18n)))
        .build();
  }

  /** Deletes a single enrollment identified by project and former-student identifiers. */
  @DELETE
  @Path("/{projectId}/enrollments/{formerStudentId}")
  @RolesAllowed("ADMIN")
  public Response delete(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("formerStudentId") @UuidV7 UUID formerStudentId) {
    writeService.delete(
        EnrollmentIdentifier.builder()
            .projectId(projectId)
            .formerStudentId(formerStudentId)
            .build());
    return Response.noContent().build();
  }

  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
