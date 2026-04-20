package br.org.catolicasc.pug.project.presenter;

import br.org.catolicasc.pug.project.infra.read.dtos.AttendanceView;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceCreateRequest;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceResponse;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceValidateRequest;
import br.org.catolicasc.pug.project.presenter.mappers.AttendancePresenter;
import br.org.catolicasc.pug.project.service.AttendanceReadService;
import br.org.catolicasc.pug.project.service.AttendanceService;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import io.quarkus.security.Authenticated;
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
 * REST API Resource controller for managing student Attendances.
 *
 * <p>Methods are ordered strictly by HTTP verb (GET, POST, PATCH, DELETE) with single-item
 * endpoints preceding collections.
 */
@ApplicationScoped
@Path("/projects/attendances")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class AttendanceResource {

  @Inject AttendanceService writeService;
  @Inject AttendanceReadService readService;
  @Inject I18n i18n;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  /**
   * Retrieves a specific attendance record by its unique identifier.
   *
   * @param id the unique identifier (UUIDv7) of the attendance
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     AttendanceResponse}
   * @throws ResourceNotFoundException if the attendance is not found
   */
  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    AttendanceView view = readService.getViewById(id);
    AttendanceResponse body = AttendancePresenter.toResponse(view, locale(), i18n);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a collection of attendance records.
   *
   * <p>If both {@code projectId} and {@code studentId} are provided, it filters by enrollment.
   * Otherwise, it filters by project or student individually. If all are omitted, returns all
   * attendance records.
   *
   * @param projectId the optional project identifier to filter by
   * @param studentId the optional student identifier to filter by
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     AttendanceResponse}
   */
  @GET
  public Response list(
      @QueryParam("projectId") @UuidV7 UUID projectId,
      @QueryParam("studentId") @UuidV7 UUID studentId) {
    List<AttendanceView> views;

    if (projectId != null && studentId != null) {
      views = readService.listByEnrollmentId(projectId, studentId);
    } else if (projectId != null) {
      views = readService.listByProjectId(projectId);
    } else if (studentId != null) {
      views = readService.listByStudentId(studentId);
    } else {
      views = readService.listViews();
    }

    List<AttendanceResponse> body =
        views.stream()
            .map(v -> AttendancePresenter.toResponse(v, locale(), i18n))
            .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Registers a new attendance record.
   *
   * @param req the validated {@link AttendanceCreateRequest} payload
   * @return an HTTP 201 Created response containing a {@code Location} header and the created
   *     {@link AttendanceResponse}
   */
  @POST
  public Response create(@Valid AttendanceCreateRequest req) {
    var cmd = AttendancePresenter.toCommand(req);
    var created = writeService.save(cmd);

    AttendanceView view = readService.getViewById(created.getId());
    AttendanceResponse body = AttendancePresenter.toResponse(view, locale(), i18n);

    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Validates an attendance record via QR code scan.
   *
   * @param id the unique identifier (UUIDv7) of the attendance to validate
   * @param req the validated {@link AttendanceValidateRequest} payload
   * @return an HTTP 200 OK response containing the updated {@link AttendanceResponse}
   */
  @PATCH
  @Path("/{id}/validate")
  public Response validate(@PathParam("id") @UuidV7 UUID id, @Valid AttendanceValidateRequest req) {
    var cmd = AttendancePresenter.toCommand(req);

    writeService.validate(id, cmd);
    AttendanceView view = readService.getViewById(id);
    AttendanceResponse body = AttendancePresenter.toResponse(view, locale(), i18n);

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Permanently removes an attendance record from the system.
   *
   * @param id the unique identifier (UUIDv7) of the attendance to delete
   * @return an HTTP 200 OK response with an empty data payload indicating successful deletion
   */
  @DELETE
  @Path("/{id}")
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  /**
   * Determines the appropriate {@link Locale} based on the request headers.
   *
   * @return the resolved {@link Locale}
   */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
