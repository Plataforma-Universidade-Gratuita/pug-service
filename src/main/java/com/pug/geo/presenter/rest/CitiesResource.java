package com.pug.geo.presenter.rest;

import com.pug.geo.domain.City;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.geo.presenter.dtos.CityCreateOrUpdateRequest;
import com.pug.geo.presenter.dtos.CityResponse;
import com.pug.geo.service.CitiesService;
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
import java.util.UUID;

/** REST resource for managing cities. */
@ApplicationScoped
@Path("/geo/cities")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CitiesResource {

  @Inject CitiesService service;

  @Context UriInfo uri;

  /**
   * Create a new city.
   *
   * @param req the city creation request.
   * @return a Response with the created city.
   */
  @POST
  public Response create(@Valid CityCreateOrUpdateRequest req) {
    City created =
        service.save(
            City.builder().name(req.name()).ibgeCode(new IbgeCode(req.ibgeCode())).build());
    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location)
        .entity(ApiEnvelope.created(CityResponse.from(created)))
        .build();
  }

  /**
   * Create multiple cities in bulk.
   *
   * @param req the bulk creation request.
   * @return a Response with the bulk creation result.
   */
  @POST
  @Path("/bulk")
  public Response createBulk(@Valid BulkCreateRequest<CityCreateOrUpdateRequest> req) {
    List<City> toSave =
        req.entities().stream()
            .map(r -> City.builder().name(r.name()).ibgeCode(new IbgeCode(r.ibgeCode())).build())
            .toList();
    service.saveAll(toSave);
    return Response.status(Response.Status.CREATED)
        .entity(ApiEnvelope.created(BulkCreateResult.sizeOnly(toSave.size())))
        .build();
  }

  /**
   * Update an existing city.
   *
   * @param id the ID of the city to update.
   * @param req the city update request.
   * @return a Response with the updated city.
   */
  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") UUID id, @Valid CityCreateOrUpdateRequest req) {
    City patch = City.builder().name(req.name()).ibgeCode(new IbgeCode(req.ibgeCode())).build();
    City updated = service.update(id, patch);
    return Response.ok(ApiEnvelope.ok(CityResponse.from(updated))).build();
  }

  /**
   * List all cities or search by query.
   *
   * @param q the search query (optional).
   * @return a Response with the list of cities.
   */
  @GET
  public Response listOrSearch(@QueryParam("q") String q) {
    List<City> result = (q == null || q.isBlank()) ? service.listAll() : service.search(q);
    List<CityResponse> body = result.stream().map(CityResponse::from).toList();
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Get a city by its ID.
   *
   * @param id the ID of the city.
   * @return a Response with the city.
   */
  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") UUID id) {
    City found = service.getById(id);
    return Response.ok(ApiEnvelope.ok(CityResponse.from(found))).build();
  }

  /**
   * Delete cities by their IDs.
   *
   * @param req the request containing the IDs to delete.
   * @return a Response with the deletion result.
   */
  @DELETE
  public Response delete(@Valid UuidsRequest req) {
    long deleted = service.deleteByIds(req.ids());
    return Response.ok(ApiEnvelope.ok(new DeleteResult(deleted))).build();
  }
}
