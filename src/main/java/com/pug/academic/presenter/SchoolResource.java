package com.pug.academic.presenter;

import com.pug.academic.domain.School;
import com.pug.academic.presenter.dtos.SchoolCreateOrUpdateRequest;
import com.pug.academic.presenter.dtos.SchoolResponse;
import com.pug.academic.presenter.mappers.SchoolPresenter;
import com.pug.academic.service.SchoolReadService;
import com.pug.academic.service.SchoolService;
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

/**
 * REST resource for managing schools.
 */
@ApplicationScoped
@Path("/academic/schools")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SchoolResource {

  @Inject
  SchoolService writeService;
  @Inject
  SchoolReadService readService;
  @Context
  UriInfo uri;

  /**
   * Creates a new school.
   *
   * @param req the school creation request
   * @return the response containing the created school
   */
  @POST
  public Response create(@Valid SchoolCreateOrUpdateRequest req) {
    School created = writeService.save(req.name());
    var view = readService.getViewById(created.getId());
    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location)
            .entity(ApiEnvelope.created(SchoolPresenter.toResponse(view)))
            .build();
  }

  /**
   * Creates multiple schools in bulk.
   *
   * @param req the bulk creation request
   * @return the response containing the result of the bulk creation
   */
  @POST
  @Path("/bulk")
  public Response createBulk(@Valid Iterable<SchoolCreateOrUpdateRequest> req) {
    var saved = writeService.saveAll(CollectionUtils.toStream(req).map(SchoolCreateOrUpdateRequest::name).toList());
    return Response.status(Response.Status.CREATED)
            .entity(ApiEnvelope.created(BulkCreateResult.sizeOnly(saved.size())))
            .build();
  }

  /**
   * Updates an existing school.
   *
   * @param id  the ID of the school to update
   * @param req the school update request
   * @return the response containing the updated school
   */
  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid SchoolCreateOrUpdateRequest req) {
    writeService.update(id, req.name());
    var view = readService.getViewById(id);
    return Response.ok(ApiEnvelope.ok(SchoolPresenter.toResponse(view))).build();
  }

  /**
   * Lists all schools or searches schools by name.
   *
   * @param q the optional search query
   * @return the response containing the list of schools
   */
  @GET
  public Response listOrSearch(@QueryParam("q") String q) {
    var views = (StringUtils.isEmpty(q)) ? readService.listAll() : readService.searchByName(q);
    List<SchoolResponse> body = views.stream().map(SchoolPresenter::toResponse).toList();
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a school by its ID.
   *
   * @param id the ID of the school
   * @return the response containing the school
   */
  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    var view = readService.getViewById(id);
    return Response.ok(ApiEnvelope.ok(SchoolPresenter.toResponse(view))).build();
  }

  /**
   * Deletes schools by their IDs.
   *
   * @param req the request containing the IDs of schools to delete
   * @return the response containing the result of the deletion
   */
  @DELETE
  public Response delete(@Valid UuidsRequest req) {
    Map<DeleteKeys, Long> deleted = writeService.deleteAll(req.ids());
    return Response.ok(ApiEnvelope.ok(new DeleteResult(deleted))).build();
  }
}
