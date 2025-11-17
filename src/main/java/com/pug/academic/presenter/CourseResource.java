package com.pug.academic.presenter;

import com.pug.academic.domain.Course;
import com.pug.academic.infra.read.dtos.CourseView;
import com.pug.academic.presenter.dtos.CourseCreateBulkRequest;
import com.pug.academic.presenter.dtos.CourseResponse;
import com.pug.academic.presenter.mappers.CoursePresenter;
import com.pug.academic.service.CourseReadService;
import com.pug.academic.service.CourseService;
import com.pug.academic.service.dtos.CourseCreateBulkCommand;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.presenter.dtos.BulkCreateResult;
import com.pug.shared.presenter.dtos.DeleteResult;
import com.pug.shared.presenter.dtos.UuidsRequest;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.utils.CollectionUtils;
import com.pug.shared.utils.StringUtils;
import com.pug.shared.validation.UuidV7;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
   * List or search courses.
   *
   * @param q        the search query
   * @param schoolId the school id to filter by
   * @return the response
   */
  @GET
  public Response listOrSearch(@QueryParam("q") String q, @QueryParam("schoolId") UUID schoolId) {
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
   * @param name       the course name
   * @param schoolName the school name
   * @return the response
   */
  @POST
  public Response create(@Valid @NotBlank String name, @Valid @NotBlank String schoolName) {
    Course created = writeService.save(name, schoolName);
    CourseResponse body = CoursePresenter.toResponse(readService.getViewById(created.getId()));
    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Create courses in bulk.
   *
   * @param req the bulk create request
   * @return the response
   */
  @POST
  @Path("/bulk")
  public Response createBulk(@Valid Iterable<CourseCreateBulkRequest> req) {
    var cmds = CollectionUtils.toStream(req).map(r ->
            new CourseCreateBulkCommand(r.name(), r.schoolName())).collect(Collectors.toSet());
    var saved = writeService.saveAll(cmds);
    return Response.status(Response.Status.CREATED)
            .entity(ApiEnvelope.created(BulkCreateResult.sizeOnly(saved.size())))
            .build();
  }

  /**
   * Update an existing course.
   *
   * @param id         the course id to update
   * @param name       the new name
   * @param schoolName the new school name
   * @return the response
   */
  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") UUID id, String name, String schoolName) {
    Course updated = writeService.update(id, name, schoolName);
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
