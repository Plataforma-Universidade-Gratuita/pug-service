package com.pug.academic.presenter.rest;

import com.pug.academic.domain.Course;
import com.pug.academic.infra.read.dtos.CourseView;
import com.pug.academic.presenter.dtos.CourseCreateOrUpdateRequest;
import com.pug.academic.presenter.dtos.CourseResponse;
import com.pug.academic.presenter.mappers.CoursePresenter;
import com.pug.academic.service.CourseReadService;
import com.pug.academic.service.CourseService;
import com.pug.shared.presenter.dtos.BulkCreateRequest;
import com.pug.shared.presenter.dtos.BulkCreateResult;
import com.pug.shared.presenter.dtos.DeleteResult;
import com.pug.shared.presenter.dtos.UuidsRequest;
import com.pug.shared.presenter.rest.ApiEnvelope;
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
import java.util.Objects;
import java.util.UUID;

/** REST resource for managing courses. */
@ApplicationScoped
@Path("/academic/courses")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {

  @Inject CourseService writeService;
  @Inject CourseReadService readService;

  @Context UriInfo uri;

  /**
   * Get course by id.
   *
   * @param id the course id
   * @return the response
   */
  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") UUID id) {
    Objects.requireNonNull(id, "id");
    CourseView v = readService.getView(id);
    CourseResponse body = CoursePresenter.toResponse(v);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Get courses in bulk by ids.
   *
   * @param req the uuids request
   * @return the response
   */
  @GET
  @Path("/bulk")
  public Response getBulk(@Valid UuidsRequest req) {
    Objects.requireNonNull(req, "req");
    List<CourseView> views = readService.listViewsByIds(req.ids());
    List<CourseResponse> body = views.stream().map(CoursePresenter::toResponse).toList();
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
  public Response listOrSearch(@QueryParam("q") String q, @QueryParam("schoolId") UUID schoolId) {
    List<CourseView> views;
    if (schoolId != null) {
      views = readService.listViewsBySchoolId(schoolId);
    } else if (q != null && !q.isBlank()) {
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
   * @param req the create request
   * @return the response
   */
  @POST
  public Response create(@Valid CourseCreateOrUpdateRequest req) {
    Objects.requireNonNull(req, "req");
    Course created = writeService.save(req.name(), req.schoolId());
    CourseResponse body = CoursePresenter.toResponse(readService.getView(created.getId()));
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
  public Response createBulk(@Valid BulkCreateRequest<CourseCreateOrUpdateRequest> req) {
    Objects.requireNonNull(req, "req");
    var toSave =
        req.entities().stream().map(r -> Course.createNew(r.name(), r.schoolId())).toList();
    writeService.saveAll(toSave);
    return Response.status(Response.Status.CREATED)
        .entity(ApiEnvelope.created(BulkCreateResult.sizeOnly(toSave.size())))
        .build();
  }

  /**
   * Update an existing course.
   *
   * @param id the course id
   * @param req the update request
   * @return the response
   */
  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") UUID id, @Valid CourseCreateOrUpdateRequest req) {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(req, "req");
    Course patch = Course.createNew(req.name(), req.schoolId());
    Course updated = writeService.update(id, patch);
    CourseResponse body = CoursePresenter.toResponse(readService.getView(updated.getId()));
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
    Objects.requireNonNull(req, "req");
    Map<String, Long> deleted = writeService.deleteByIds(req.ids());
    return Response.ok(ApiEnvelope.ok(new DeleteResult(deleted))).build();
  }
}
