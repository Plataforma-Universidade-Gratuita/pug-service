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
 * REST resource for managing courses.
 */
@ApplicationScoped
@Path("/academic/courses")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {

  @Inject
  CourseService writeService;
  @Inject
  CourseReadService readService;

  @Context
  UriInfo uri;
  @Context
  HttpHeaders headers;

  /**
   * Retrieves a course by its ID.
   *
   * @param id the course id
   * @return the response containing the course
   */
  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    CourseView view = readService.getViewById(id);
    CourseResponse body = CoursePresenter.toResponse(view, locale());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Lists courses or searches by name.
   *
   * @param q        the search query
   * @param schoolId the school id to filter by
   * @return the response containing the list of courses
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

    List<CourseResponse> body = views.stream()
            .map(v -> CoursePresenter.toResponse(v, locale()))
            .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Creates a new course.
   *
   * @param req the CourseCreateRequest DTO
   * @return the response containing the created course
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
   * Updates an existing course.
   *
   * @param id  the course id to update
   * @param req the CourseUpdateRequest DTO
   * @return the response containing the updated course
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
   * Deletes a course by its ID.
   *
   * @param id the course id to delete
   * @return the response indicating success
   */
  @DELETE
  @Path("/{id}")
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  /**
   * Picks the best locale from the Accept-Language header.
   */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}