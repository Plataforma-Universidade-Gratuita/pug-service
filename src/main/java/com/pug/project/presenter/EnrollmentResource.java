package com.pug.project.presenter;

import com.pug.identity.service.AuthService;
import com.pug.project.domain.Enrollment;
import com.pug.project.domain.vos.EnrollmentIdentifier;
import com.pug.project.infra.read.dtos.EnrollmentView;
import com.pug.project.presenter.dtos.EnrollmentCreateRequest;
import com.pug.project.presenter.dtos.EnrollmentResponse;
import com.pug.project.presenter.mappers.EnrollmentPresenter;
import com.pug.project.service.EnrollmentReadService;
import com.pug.project.service.EnrollmentService;
import com.pug.project.service.dtos.EnrollmentCreateCommand;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.utils.PresenterUtils;
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
 * REST API Resource controller for managing Student Enrollments in Projects.
 *
 * <p>Methods are ordered strictly by HTTP verb (GET, POST, PATCH, DELETE) with single-item
 * endpoints preceding collections. Note that Enrollments use a composite key (projectId and
 * studentId).
 */
@ApplicationScoped
@Path("/projects/enrollments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EnrollmentResource {

  @Inject AuthService authService;
  @Inject EnrollmentService writeService;
  @Inject EnrollmentReadService readService;
  @Inject I18n i18n;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  /**
   * Retrieves a specific enrollment by its composite identifier.
   *
   * @param projectId the unique identifier (UUIDv7) of the project
   * @param studentId the unique identifier (UUIDv7) of the student account
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     EnrollmentResponse}
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the enrollment is not found
   */
  @GET
  @Path("/{projectId}/{studentId}")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response get(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("studentId") @UuidV7 UUID studentId) {
    EnrollmentView view = readService.getViewByIds(projectId, studentId);
    EnrollmentResponse body = EnrollmentPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves the enrollment of the currently authenticated student for the given project.
   *
   * <p>The student account identifier is resolved from the JWT (for example, via the {@code
   * accountId} claim) using {@link AuthService#getCurrentAccountId()}, ensuring that callers can
   * only access their own enrollment record for the specified project.
   *
   * @param projectId the unique identifier (UUIDv7) of the project
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     EnrollmentResponse} for the current student in the given project
   * @throws jakarta.ws.rs.NotAuthorizedException if the token is missing, invalid, or does not
   *     contain the required {@code accountId} claim
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the enrollment is not found
   */
  @GET
  @Path("/{projectId}/me")
  @RolesAllowed("STUDENT")
  public Response getMine(@PathParam("projectId") @UuidV7 UUID projectId) {
    UUID studentAccountId = authService.getCurrentAccountId();

    EnrollmentView view = readService.getViewByIds(projectId, studentAccountId);
    EnrollmentResponse body = EnrollmentPresenter.toResponse(view, locale(), i18n);

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a collection of enrollments.
   *
   * <p>If the optional {@code projectId} parameter is provided, it filters the enrollments by
   * project. If the {@code studentId} is provided, it filters by student. If both are omitted, it
   * returns all enrollments.
   *
   * @param projectId the optional project identifier to filter by
   * @param studentId the optional student identifier to filter by
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     EnrollmentResponse}
   */
  @GET
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
   * Retrieves the enrollments of the currently authenticated student.
   *
   * <p>The student account identifier is resolved from the JWT (for example, via the {@code
   * accountId} claim) using {@link AuthService#getCurrentAccountId()}, ensuring that callers can
   * only access their own enrollment records.
   *
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     EnrollmentResponse} for the current student
   * @throws jakarta.ws.rs.NotAuthorizedException if the token is missing, invalid, or does not
   *     contain the required {@code accountId} claim
   */
  @GET
  @Path("/me")
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
   * Registers a new enrollment for the currently authenticated student in the given project.
   *
   * <p>The student account is resolved from the authentication context (e.g., {@code
   * AuthService.getCurrentAccountId()}) inside the application service. This endpoint only accepts
   * the {@code projectId} in the request payload.
   *
   * @param req the validated {@link EnrollmentCreateRequest} containing the target project
   *     identifier
   * @return an HTTP 201 Created response containing a {@code Location} header and the created
   *     {@link EnrollmentResponse}
   * @throws com.pug.shared.exceptions.DuplicateResourceException if the student is already enrolled
   *     in the project
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the project or student does not
   *     exist
   * @throws com.pug.shared.exceptions.AppValidationException if domain validation fails
   */
  @POST
  @Authenticated
  public Response create(@Valid EnrollmentCreateRequest req) {
    EnrollmentCreateCommand cmd = EnrollmentPresenter.toCommand(req);
    Enrollment created = writeService.save(cmd);

    EnrollmentView view =
        readService.getViewByIds(
            created.getIdentifier().getProjectId(), created.getIdentifier().getStudentId());
    EnrollmentResponse body = EnrollmentPresenter.toResponse(view, locale(), i18n);

    URI location =
        uri.getAbsolutePathBuilder()
            .path(created.getIdentifier().getProjectId().toString())
            .path(created.getIdentifier().getStudentId().toString())
            .build();

    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Approves a pending enrollment.
   *
   * @param projectId the unique identifier (UUIDv7) of the project
   * @param studentId the unique identifier (UUIDv7) of the student account
   * @return an HTTP 200 OK response containing the updated {@link EnrollmentResponse}
   */
  @PATCH
  @Path("/{projectId}/{studentId}/accept")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response accept(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("studentId") @UuidV7 UUID studentId) {

    EnrollmentIdentifier identifier =
        EnrollmentIdentifier.builder().projectId(projectId).studentId(studentId).build();

    writeService.accept(identifier);
    EnrollmentView view = readService.getViewByIds(projectId, studentId);
    return Response.ok(ApiEnvelope.ok(EnrollmentPresenter.toResponse(view, locale(), i18n)))
        .build();
  }

  /**
   * Cancels an active enrollment.
   *
   * @param projectId the unique identifier (UUIDv7) of the project
   * @param studentId the unique identifier (UUIDv7) of the student account
   * @return an HTTP 200 OK response containing the updated {@link EnrollmentResponse}
   */
  @PATCH
  @Path("/{projectId}/{studentId}/cancel")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response cancel(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("studentId") @UuidV7 UUID studentId) {

    EnrollmentIdentifier identifier =
        EnrollmentIdentifier.builder().projectId(projectId).studentId(studentId).build();

    writeService.cancel(identifier);
    EnrollmentView view = readService.getViewByIds(projectId, studentId);
    return Response.ok(ApiEnvelope.ok(EnrollmentPresenter.toResponse(view, locale(), i18n)))
        .build();
  }

  /**
   * Marks a student's enrollment as completed.
   *
   * @param projectId the unique identifier (UUIDv7) of the project
   * @param studentId the unique identifier (UUIDv7) of the student account
   * @return an HTTP 200 OK response containing the updated {@link EnrollmentResponse}
   */
  @PATCH
  @Path("/{projectId}/{studentId}/complete")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response complete(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("studentId") @UuidV7 UUID studentId) {

    EnrollmentIdentifier identifier =
        EnrollmentIdentifier.builder().projectId(projectId).studentId(studentId).build();

    writeService.complete(identifier);
    EnrollmentView view = readService.getViewByIds(projectId, studentId);
    return Response.ok(ApiEnvelope.ok(EnrollmentPresenter.toResponse(view, locale(), i18n)))
        .build();
  }

  /**
   * Records the currently authenticated student's voluntary exit from a project.
   *
   * <p>The student account is resolved from the JWT (for example, via {@code accountId} claim),
   * ensuring that a student can only exit their own enrollment and never someone else's.
   *
   * @param projectId the unique identifier (UUIDv7) of the project
   * @return an HTTP 200 OK response containing the updated {@link EnrollmentResponse}
   */
  @PATCH
  @Path("/{projectId}/exit")
  @RolesAllowed({"STUDENT"})
  public Response exit(@PathParam("projectId") @UuidV7 UUID projectId) {

    UUID studentAccountId = authService.getCurrentAccountId();

    EnrollmentIdentifier identifier =
        EnrollmentIdentifier.builder().projectId(projectId).studentId(studentAccountId).build();

    writeService.exit(identifier);
    EnrollmentView view = readService.getViewByIds(projectId, studentAccountId);

    EnrollmentResponse body = EnrollmentPresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Rejects a pending enrollment request.
   *
   * @param projectId the unique identifier (UUIDv7) of the project
   * @param studentId the unique identifier (UUIDv7) of the student account
   * @return an HTTP 200 OK response containing the updated {@link EnrollmentResponse}
   */
  @PATCH
  @Path("/{projectId}/{studentId}/reject")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response reject(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("studentId") @UuidV7 UUID studentId) {

    EnrollmentIdentifier identifier =
        EnrollmentIdentifier.builder().projectId(projectId).studentId(studentId).build();

    writeService.reject(identifier);
    EnrollmentView view = readService.getViewByIds(projectId, studentId);
    return Response.ok(ApiEnvelope.ok(EnrollmentPresenter.toResponse(view, locale(), i18n)))
        .build();
  }

  /**
   * Administratively removes a student's enrollment from a project.
   *
   * @param projectId the unique identifier (UUIDv7) of the project
   * @param studentId the unique identifier (UUIDv7) of the student account
   * @return an HTTP 200 OK response containing the updated {@link EnrollmentResponse}
   */
  @PATCH
  @Path("/{projectId}/{studentId}/remove")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response remove(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("studentId") @UuidV7 UUID studentId) {

    EnrollmentIdentifier identifier =
        EnrollmentIdentifier.builder().projectId(projectId).studentId(studentId).build();

    writeService.remove(identifier);
    EnrollmentView view = readService.getViewByIds(projectId, studentId);
    return Response.ok(ApiEnvelope.ok(EnrollmentPresenter.toResponse(view, locale(), i18n)))
        .build();
  }

  /**
   * Physically removes an enrollment from the system.
   *
   * <p>This endpoint executes a hard delete using the composite identifier. It is idempotent:
   * deleting a non-existing enrollment simply produces a successful 200 response with an empty
   * payload.
   *
   * @param projectId the unique identifier (UUIDv7) of the project
   * @param studentId the unique identifier (UUIDv7) of the student account
   * @return an HTTP 200 OK response with an empty data payload indicating successful deletion
   */
  @DELETE
  @Path("/{projectId}/{studentId}")
  @RolesAllowed({"ADMIN"})
  public Response delete(
      @PathParam("projectId") @UuidV7 UUID projectId,
      @PathParam("studentId") @UuidV7 UUID studentId) {

    EnrollmentIdentifier identifier =
        EnrollmentIdentifier.builder().projectId(projectId).studentId(studentId).build();

    writeService.delete(identifier);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  /**
   * Helper method to determine the preferred locale from the incoming request headers.
   *
   * @return the resolved {@link Locale}
   */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
