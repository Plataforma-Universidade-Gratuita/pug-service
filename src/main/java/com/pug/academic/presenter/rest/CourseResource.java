package com.pug.academic.presenter.rest;

import com.pug.academic.domain.Course;
import com.pug.academic.presenter.dtos.CourseCreateOrUpdateRequest;
import com.pug.academic.presenter.dtos.CourseView;
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
import java.util.Objects;
import java.util.UUID;

/** REST resource for managing courses. */
@ApplicationScoped
@Path("/academic/courses")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {

  @Inject CourseService service;
  @Inject CourseReadService readService;

  @Context UriInfo uri;

  /**
   * Retrieves a course by its ID.
   *
   * @param id the course ID
   * @return the course view wrapped in an ApiEnvelope
   */
  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") UUID id) {
    Objects.requireNonNull(id, "id");
    CourseView v = readService.getView(id);
    return Response.ok(ApiEnvelope.ok(v)).build();
  }

  /**
   * Lists or searches courses based on query parameters.
   *
   * @param q optional search query
   * @param schoolId optional school ID to filter courses
   * @return the list of course views wrapped in an ApiEnvelope
   */
  @GET
  public Response listOrSearch(@QueryParam("q") String q, @QueryParam("schoolId") UUID schoolId) {
    if (schoolId != null) {
      List<CourseView> views = readService.listViewsBySchoolId(schoolId);
      return Response.ok(ApiEnvelope.ok(views)).build();
    }
    if (q != null && !q.isBlank()) {
      List<Course> found = service.search(q);
      List<CourseView> views =
          found.stream().map(c -> readService.getView(c.getId())).filter(Objects::nonNull).toList();
      return Response.ok(ApiEnvelope.ok(views)).build();
    }
    List<CourseView> all = readService.listViews();
    return Response.ok(ApiEnvelope.ok(all)).build();
  }

  /**
   * Creates a new course.
   *
   * @param req the course creation request
   * @return the created course view wrapped in an ApiEnvelope with location header
   */
  @POST
  public Response create(@Valid CourseCreateOrUpdateRequest req) {
    Objects.requireNonNull(req, "req");
    Course created = service.save(req.name(), req.schoolId());
    CourseView v = readService.getView(created.getId());
    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(v)).build();
  }

  /**
   * Creates multiple courses in bulk.
   *
   * @param req the bulk course creation request
   * @return the bulk creation result wrapped in an ApiEnvelope
   */
  @POST
  @Path("/bulk")
  public Response createBulk(@Valid BulkCreateRequest<CourseCreateOrUpdateRequest> req) {
    Objects.requireNonNull(req, "req");
    var toSave =
        req.entities().stream().map(r -> Course.createNew(r.name(), r.schoolId())).toList();
    service.saveAll(toSave);
    return Response.status(Response.Status.CREATED)
        .entity(ApiEnvelope.created(BulkCreateResult.sizeOnly(toSave.size())))
        .build();
  }

  /**
   * Updates an existing course.
   *
   * @param id the course ID
   * @param req the course update request
   * @return the updated course view wrapped in an ApiEnvelope
   */
  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") UUID id, @Valid CourseCreateOrUpdateRequest req) {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(req, "req");
    Course patch = Course.createNew(req.name(), req.schoolId());
    Course updated = service.update(id, patch);
    CourseView v = readService.getView(updated.getId());
    return Response.ok(ApiEnvelope.ok(v)).build();
  }

  /**
   * Deletes courses by their IDs.
   *
   * @param req the request containing IDs of courses to delete
   * @return the deletion result wrapped in an ApiEnvelope
   */
  @DELETE
  public Response delete(@Valid UuidsRequest req) {
    Objects.requireNonNull(req, "req");
    long deleted = service.deleteByIds(req.ids());
    return Response.ok(ApiEnvelope.ok(new DeleteResult(deleted))).build();
  }
}
