package br.org.catolicasc.pug.project.presenter;

import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.project.constants.ProjectApiPaths;
import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentIdentifier;
import br.org.catolicasc.pug.project.infra.read.dtos.EnrollmentView;
import br.org.catolicasc.pug.project.presenter.dtos.EnrollmentResponse;
import br.org.catolicasc.pug.project.presenter.dtos.EnrollmentUpdateRequest;
import br.org.catolicasc.pug.project.presenter.mappers.EnrollmentPresenter;
import br.org.catolicasc.pug.project.service.EnrollmentReadService;
import br.org.catolicasc.pug.project.service.EnrollmentService;
import br.org.catolicasc.pug.project.service.dtos.EnrollmentCreateCommand;
import br.org.catolicasc.pug.shared.constants.ApiVersions;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
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
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST API resource controller for managing project enrollments.
 *
 * <p>This class exposes nested project enrollment endpoints rooted at {@code /v1/projects} for
 * reads, creation, status transitions, and removal. It delegates commands to the {@link
 * EnrollmentService} and queries to the {@link EnrollmentReadService}, adhering to CQRS principles.
 */
@ApplicationScoped
@Path(ProjectApiPaths.PROJECTS)
@Produces(MediaType.APPLICATION_JSON)
public class EnrollmentResource {

  @Inject AuthService authService;
  @Inject EnrollmentService writeService;
  @Inject EnrollmentReadService readService;
  @Inject I18n i18n;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  /**
   * Retrieves a specific enrollment by project and student identifiers.
   *
   * @param projectId the unique identifier (UUIDv7) of the project
   * @param studentId the unique identifier (UUIDv7) of the enrolled student account
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     EnrollmentResponse}
   */
  @GET
  @Path("/{projectId}/enrollments/{studentId}")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response get(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("studentId") @UuidV7 UUID studentId) {
    EnrollmentView view = readService.getViewByIds(projectId, studentId);
    EnrollmentResponse body = EnrollmentPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves the current authenticated student's enrollment for the specified project.
   *
   * @param projectId the unique identifier (UUIDv7) of the project
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     EnrollmentResponse}
   */
  @GET
  @Path("/{projectId}/enrollments/me")
  @RolesAllowed("STUDENT")
  public Response getMine(@PathParam("projectId") @UuidV7 UUID projectId) {
    UUID studentAccountId = authService.getCurrentAccountId();
    EnrollmentView view = readService.getViewByIds(projectId, studentAccountId);
    EnrollmentResponse body = EnrollmentPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves enrollments, optionally filtered by project or student identifiers.
   *
   * <p>When {@code projectId} is provided, it returns the enrollments for that project. When {@code
   * studentId} is provided, it returns the enrollments for that student. If both are omitted, it
   * returns all enrollments visible to the caller.
   *
   * @param projectId the optional project identifier filter
   * @param studentId the optional student account identifier filter
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     EnrollmentResponse}
   */
  @GET
  @Path("/enrollments")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response list(
      @QueryParam("projectId") @UuidV7 UUID projectId,
      @QueryParam("studentId") @UuidV7 UUID studentId) {
    List<EnrollmentView> views;

    if (projectId != null) {
      views = readService.listViewsByProjectId(projectId);
    } else if (studentId != null) {
      views = readService.listViewsByStudentId(studentId);
    } else {
      views = readService.listViews();
    }

    List<EnrollmentResponse> body =
        views.stream()
            .map(v -> EnrollmentPresenter.toResponse(v, locale(), i18n))
            .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves all enrollments belonging to the currently authenticated student.
   *
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     EnrollmentResponse}
   */
  @GET
  @Path("/enrollments/me")
  @RolesAllowed("STUDENT")
  public Response listMine() {
    UUID studentAccountId = authService.getCurrentAccountId();

    var views = readService.listViewsByStudentId(studentAccountId);
    List<EnrollmentResponse> body =
        views.stream()
            .map(v -> EnrollmentPresenter.toResponse(v, locale(), i18n))
            .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Creates a new enrollment for the current authenticated account in the specified project.
   *
   * <p>The project identifier is supplied in the route, so this endpoint does not require a JSON
   * request body.
   *
   * @param projectId the unique identifier (UUIDv7) of the target project
   * @return an HTTP 201 Created response containing a {@code Location} header and the created
   *     {@link EnrollmentResponse}
   */
  @POST
  @Path("/{projectId}/enrollments")
  @Authenticated
  public Response create(@PathParam("projectId") @UuidV7 UUID projectId) {
    EnrollmentCreateCommand cmd = new EnrollmentCreateCommand(projectId);
    Enrollment created = writeService.save(cmd);

    EnrollmentView view =
        readService.getViewByIds(
            created.getIdentifier().getProjectId(), created.getIdentifier().getStudentId());
    EnrollmentResponse body = EnrollmentPresenter.toResponse(view, locale(), i18n);

    URI location =
        uri.getBaseUriBuilder()
            .path(ApiVersions.V1.substring(1))
            .path("projects")
            .path(created.getIdentifier().getProjectId().toString())
            .path("enrollments")
            .path(created.getIdentifier().getStudentId().toString())
            .build();

    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Transitions a specific enrollment to a new status.
   *
   * @param projectId the unique identifier (UUIDv7) of the project
   * @param studentId the unique identifier (UUIDv7) of the enrolled student account
   * @param req the validated {@link EnrollmentUpdateRequest} containing the target status
   * @return an HTTP 200 OK response containing the updated {@link EnrollmentResponse}
   */
  @PATCH
  @Path("/{projectId}/enrollments/{studentId}")
  @RolesAllowed({"ADMIN", "STAFF"})
  @jakarta.ws.rs.Consumes(MediaType.APPLICATION_JSON)
  public Response patch(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("studentId") @UuidV7 UUID studentId,
      @Valid EnrollmentUpdateRequest req) {
    transition(identifier(projectId, studentId), req.status());
    EnrollmentView view = readService.getViewByIds(projectId, studentId);
    return Response.ok(ApiEnvelope.ok(EnrollmentPresenter.toResponse(view, locale(), i18n)))
        .build();
  }

  /**
   * Transitions the current authenticated student's enrollment in the specified project to a new
   * status.
   *
   * @param projectId the unique identifier (UUIDv7) of the project
   * @param req the validated {@link EnrollmentUpdateRequest} containing the target status
   * @return an HTTP 200 OK response containing the updated {@link EnrollmentResponse}
   */
  @PATCH
  @Path("/{projectId}/enrollments/me")
  @RolesAllowed("STUDENT")
  @jakarta.ws.rs.Consumes(MediaType.APPLICATION_JSON)
  public Response patchMine(
      @PathParam("projectId") @UuidV7 UUID projectId, @Valid EnrollmentUpdateRequest req) {
    UUID studentAccountId = authService.getCurrentAccountId();
    transition(identifier(projectId, studentAccountId), req.status());
    EnrollmentView view = readService.getViewByIds(projectId, studentAccountId);
    return Response.ok(ApiEnvelope.ok(EnrollmentPresenter.toResponse(view, locale(), i18n)))
        .build();
  }

  /**
   * Permanently removes an enrollment association from the system.
   *
   * @param projectId the unique identifier (UUIDv7) of the project
   * @param studentId the unique identifier (UUIDv7) of the enrolled student account
   * @return an HTTP 204 No Content response when deletion succeeds
   */
  @DELETE
  @Path("/{projectId}/enrollments/{studentId}")
  @RolesAllowed({"ADMIN"})
  public Response delete(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("studentId") @UuidV7 UUID studentId) {
    writeService.delete(identifier(projectId, studentId));
    return Response.noContent().build();
  }

  /**
   * Builds the enrollment composite identifier from route parameters.
   *
   * @param projectId the unique identifier of the project
   * @param studentId the unique identifier of the student account
   * @return the composed {@link EnrollmentIdentifier}
   */
  private EnrollmentIdentifier identifier(UUID projectId, UUID studentId) {
    return EnrollmentIdentifier.builder().projectId(projectId).studentId(studentId).build();
  }

  /**
   * Executes the enrollment status transition associated with the requested target status.
   *
   * @param identifier the composite enrollment identifier
   * @param status the requested target status
   * @throws IllegalArgumentException when the requested status is not supported by the patch
   *     contract
   */
  private void transition(EnrollmentIdentifier identifier, EnrollmentStatus status) {
    switch (status) {
      case APPROVED -> writeService.accept(identifier);
      case CANCELED -> writeService.cancel(identifier);
      case COMPLETED -> writeService.complete(identifier);
      case EXITED -> writeService.exit(identifier);
      case REJECTED -> writeService.reject(identifier);
      case REMOVED -> writeService.remove(identifier);
      case PENDING -> throw new IllegalArgumentException("Unsupported status: " + status);
      default -> throw new IllegalArgumentException("Unknown status: " + status);
    }
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
