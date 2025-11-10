package com.pug.academic.presenter.rest;

import com.pug.academic.domain.School;
import com.pug.academic.presenter.dtos.SchoolCreateOrUpdateRequest;
import com.pug.academic.presenter.dtos.SchoolResponse;
import com.pug.academic.service.SchoolService;
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

/** REST resource for managing schools. */
@ApplicationScoped
@Path("/academic/schools")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SchoolResource {

  @Inject SchoolService service;
  @Context UriInfo uri;

  /**
   * Create a new school.
   *
   * @param req the create request.
   * @return the response with location header.
   */
  @POST
  public Response create(@Valid SchoolCreateOrUpdateRequest req) {
    Objects.requireNonNull(req, "req");
    School created = service.save(req.name());
    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location)
        .entity(ApiEnvelope.created(SchoolResponse.from(created)))
        .build();
  }

  /**
   * Create multiple schools in bulk.
   *
   * @param req the bulk create request.
   * @return the response with number of created entities.
   */
  @POST
  @Path("/bulk")
  public Response createBulk(@Valid BulkCreateRequest<SchoolCreateOrUpdateRequest> req) {
    Objects.requireNonNull(req, "req");
    List<School> toSave = req.entities().stream().map(r -> School.createNew(r.name())).toList();
    service.saveAll(toSave);
    return Response.status(Response.Status.CREATED)
        .entity(ApiEnvelope.created(BulkCreateResult.sizeOnly(toSave.size())))
        .build();
  }

  /**
   * Update an existing school.
   *
   * @param id the id of the school to update.
   * @param req the update request.
   * @return the response with updated school.
   */
  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") UUID id, @Valid SchoolCreateOrUpdateRequest req) {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(req, "req");
    School patch = School.createNew(req.name());
    School updated = service.update(id, patch);
    return Response.ok(ApiEnvelope.ok(SchoolResponse.from(updated))).build();
  }

  /**
   * List all schools or search by query.
   *
   * @param q the optional search query.
   * @return the response with list of schools.
   */
  @GET
  public Response listOrSearch(@QueryParam("q") String q) {
    List<School> result = (q == null || q.isBlank()) ? service.listAll() : service.search(q);
    List<SchoolResponse> body = result.stream().map(SchoolResponse::from).toList();
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Get a school by id.
   *
   * @param id the id of the school.
   * @return the response with the school.
   */
  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") UUID id) {
    Objects.requireNonNull(id, "id");
    School found = service.getById(id);
    return Response.ok(ApiEnvelope.ok(SchoolResponse.from(found))).build();
  }

  /**
   * Delete schools by their ids.
   *
   * @param req the request containing ids to delete.
   * @return the response with number of deleted entities.
   */
  @DELETE
  public Response delete(@Valid UuidsRequest req) {
    Objects.requireNonNull(req, "req");
    long deleted = service.deleteByIds(req.ids());
    return Response.ok(ApiEnvelope.ok(new DeleteResult(deleted))).build();
  }
}
