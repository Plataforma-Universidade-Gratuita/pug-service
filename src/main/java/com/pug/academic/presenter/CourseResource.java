package com.pug.academic.presenter;

import com.pug.academic.domain.Course;
import com.pug.academic.infra.read.dtos.CourseView;
import com.pug.academic.presenter.dtos.CourseCreateRequest;
import com.pug.academic.presenter.dtos.CourseResponse;
import com.pug.academic.presenter.dtos.CourseUpdateRequest;
import com.pug.academic.presenter.mappers.CoursePresenter;
import com.pug.academic.service.CourseReadService;
import com.pug.academic.service.CourseService;
import com.pug.academic.service.dtos.CourseCreateCommand;
import com.pug.academic.service.dtos.CourseUpdateCommand;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.utils.PresenterUtils;
import com.pug.shared.utils.StringUtils;
import com.pug.shared.validation.UuidV7;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
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
 * REST API Resource controller for managing Academic Courses.
 *
 * <p>This class exposes endpoints to create, retrieve, update, and delete courses. It delegates
 * commands to the {@link CourseService} (writes) and queries to the {@link CourseReadService}
 * (reads), strictly adhering to CQRS principles.
 */
@ApplicationScoped
@Path("/academic/courses")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {

  @Inject CourseService writeService;
  @Inject CourseReadService readService;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  /**
   * Retrieves a specific course by its unique UUID identifier.
   *
   * @param id the unique identifier (UUIDv7) of the course
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     CourseResponse}
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the course is not found
   */
  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    CourseView view = readService.getViewById(id);
    CourseResponse body = CoursePresenter.toResponse(view, locale());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a collection of courses.
   *
   * <p>If the optional {@code q} parameter is provided, it executes a full-text search against the
   * courses' names. If the {@code schoolId} is provided, it filters the courses by that school. If
   * omitted, it returns an unfiltered list of all courses.
   *
   * @param q the optional search query string
   * @param schoolId the optional school identifier to filter by
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     CourseResponse}
   */
  @GET
  public Response listOrSearch(
      @QueryParam("q") String q, @QueryParam("schoolId") @UuidV7 UUID schoolId) {

    List<CourseView> views;

    if (schoolId != null) {
      views = readService.listViewsBySchoolId(schoolId);
    } else if (StringUtils.isNotEmpty(q)) {
      views = readService.searchByName(q);
    } else {
      views = readService.listViews();
    }

    List<CourseResponse> body =
        views.stream()
            .map(v -> CoursePresenter.toResponse(v, locale()))
            .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Registers a new academic course within the platform.
   *
   * @param req the validated {@link CourseCreateRequest} payload
   * @return an HTTP 201 Created response containing a {@code Location} header and the created
   *     {@link CourseResponse}
   * @throws com.pug.shared.exceptions.DuplicateResourceException if a course with the same name
   *     already exists
   */
  @POST
  public Response create(@Valid CourseCreateRequest req) {
    CourseCreateCommand cmd = new CourseCreateCommand(req.name(), req.schoolId());
    Course created = writeService.save(cmd);

    CourseView view = readService.getViewById(created.getId());
    CourseResponse body = CoursePresenter.toResponse(view, locale());

    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Partially updates an existing course's details.
   *
   * @param id the unique identifier (UUIDv7) of the course to update
   * @param req the validated {@link CourseUpdateRequest} containing the modified data
   * @return an HTTP 200 OK response containing the updated {@link CourseResponse}
   */
  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid CourseUpdateRequest req) {
    CourseUpdateCommand cmd = new CourseUpdateCommand(req.name(), req.schoolId());
    writeService.update(id, cmd);

    CourseView view = readService.getViewById(id);
    CourseResponse body = CoursePresenter.toResponse(view, locale());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Permanently removes an academic course from the system.
   *
   * @param id the unique identifier (UUIDv7) of the course to delete
   * @return an HTTP 200 OK response with an empty data payload indicating successful deletion
   */
  @DELETE
  @Path("/{id}")
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  /** Helper method to determine the preferred locale from the incoming request headers. */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
