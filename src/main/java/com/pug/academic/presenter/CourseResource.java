package com.pug.academic.presenter;

import com.pug.academic.domain.Course;
import com.pug.academic.infra.read.dtos.CourseView;
import com.pug.academic.presenter.dtos.CourseCreateRequest;
import com.pug.academic.presenter.dtos.CourseResponse;
import com.pug.academic.presenter.dtos.CourseUpdateRequest;
import com.pug.academic.presenter.mappers.CoursePresenter;
import com.pug.academic.service.ICourseReadService;
import com.pug.academic.service.ICourseService;
import com.pug.academic.service.dtos.CourseCreateCommand;
import com.pug.academic.service.dtos.CourseUpdateCommand;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.presenter.dtos.BulkCreateResult;
import com.pug.shared.presenter.dtos.DeleteResult;
import com.pug.shared.presenter.dtos.UuidsRequest;
import com.pug.shared.presenter.rest.ApiEnvelope;
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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/** REST resource for managing courses. */
@ApplicationScoped
@Path("/academic/courses")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {

  @Inject ICourseService writeService;
  @Inject ICourseReadService readService;

  @Context UriInfo uri;

  /**
   * Get course by id.
   *
   * @param id the course id
   * @return the response
   */
  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    CourseView v = readService.getViewById(id);
    CourseResponse body = CoursePresenter.toResponse(v);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Get course by name.
   *
   * @param name the course name
   * @return the response
   */
  @GET
  @Path("/by-name/{name}")
  public Response getByName(@PathParam("name") String name) {
    CourseView v = readService.getByName(name);
    CourseResponse body = CoursePresenter.toResponse(v);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * List or search courses.
   *
   * @param q the search query
   * @param schoolId the school id to filter by
   * @return the response
   */
  @GET
  public Response listOrSearch(
      @QueryParam("q") String q, @QueryParam("schoolId") @UuidV7 UUID schoolId) {
    List<CourseView> views;
    if (schoolId != null) {
      views = readService.listViewsBySchoolId(schoolId);
    } else if (!StringUtils.isEmpty(q)) {
      views = readService.searchByName(q);
    } else {
      views = readService.listViews();
    }
    List<CourseResponse> body = views.stream().map(CoursePresenter::toResponse).toList();
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Create a new course.
   *
   * @param req the CourseCreateRequest DTO
   * @return the response
   */
  @POST
  public Response create(@Valid CourseCreateRequest req) {
    Course created = writeService.save(new CourseCreateCommand(req.name(), req.schoolId()));
    CourseResponse body = CoursePresenter.toResponse(readService.getViewById(created.getId()));
    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Create courses in bulk.
   *
   * @param reqs the list of CourseCreateRequest DTOs for bulk creation
   * @return the response
   */
  @POST
  @Path("/bulk")
  public Response createBulk(@Valid List<CourseCreateRequest> reqs) {
    var cmds =
        reqs.stream()
            .map(r -> new CourseCreateCommand(r.name(), r.schoolId()))
            .collect(Collectors.toList());
    var saved = writeService.saveAll(cmds);
    return Response.status(Response.Status.CREATED)
        .entity(ApiEnvelope.created(BulkCreateResult.sizeOnly(saved.size())))
        .build();
  }

  /**
   * Update an existing course.
   *
   * @param id the course id to update
   * @param req the CourseUpdateRequest DTO
   * @return the response
   */
  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid CourseUpdateRequest req) {
    Course updated = writeService.update(id, new CourseUpdateCommand(req.name(), req.schoolId()));
    CourseResponse body = CoursePresenter.toResponse(readService.getViewById(updated.getId()));
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Delete courses by ids.
   *
   * @param req the uuids request
   * @return the response
   */
  @DELETE
  public Response delete(@Valid UuidsRequest req) {
    Map<DeleteKeys, Long> deleted = writeService.deleteAll(req.ids());
    return Response.ok(ApiEnvelope.ok(new DeleteResult(deleted))).build();
  }
}
