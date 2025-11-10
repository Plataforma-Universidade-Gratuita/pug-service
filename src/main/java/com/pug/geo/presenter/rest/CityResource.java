package com.pug.geo.presenter.rest;

import com.pug.geo.domain.City;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.geo.presenter.dtos.CityCreateOrUpdateRequest;
import com.pug.geo.presenter.dtos.CityResponse;
import com.pug.geo.service.CityService;
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

/** REST resource for managing cities. */
@ApplicationScoped
@Path("/geo/cities")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CityResource {

  @Inject CityService service;

  @Context UriInfo uri;

  /**
   * Creates a new city.
   *
   * @param req the city creation request.
   * @return a Response containing the created city.
   */
  @POST
  public Response create(@Valid CityCreateOrUpdateRequest req) {
    Objects.requireNonNull(req, "req");
    City created = service.save(req.name(), new IbgeCode(req.ibgeCode()));
    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location)
        .entity(ApiEnvelope.created(CityResponse.from(created)))
        .build();
  }

  /**
   * Creates multiple cities in bulk.
   *
   * @param req the bulk creation request.
   * @return a Response containing the result of the bulk creation.
   */
  @POST
  @Path("/bulk")
  public Response createBulk(@Valid BulkCreateRequest<CityCreateOrUpdateRequest> req) {
    Objects.requireNonNull(req, "req");
    List<City> toSave =
        req.entities().stream()
            .map(r -> City.createNew(r.name(), new IbgeCode(r.ibgeCode())))
            .toList();
    service.saveAll(toSave);
    return Response.status(Response.Status.CREATED)
        .entity(ApiEnvelope.created(BulkCreateResult.sizeOnly(toSave.size())))
        .build();
  }

  /**
   * Updates an existing city.
   *
   * @param id the unique identifier of the city to update.
   * @param req the city update request.
   * @return a Response containing the updated city.
   */
  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") UUID id, @Valid CityCreateOrUpdateRequest req) {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(req, "req");
    City patch = City.createNew(req.name(), new IbgeCode(req.ibgeCode()));
    City updated = service.update(id, patch);
    return Response.ok(ApiEnvelope.ok(CityResponse.from(updated))).build();
  }

  /**
   * Lists all cities or searches for cities by name.
   *
   * @param q the optional search query.
   * @return a Response containing the list of cities.
   */
  @GET
  public Response listOrSearch(@QueryParam("q") String q) {
    List<City> result = (q == null || q.isBlank()) ? service.listAll() : service.search(q);
    List<CityResponse> body = result.stream().map(CityResponse::from).toList();
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a city by its unique identifier.
   *
   * @param id the unique identifier of the city.
   * @return a Response containing the found city.
   */
  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") UUID id) {
    Objects.requireNonNull(id, "id");
    City found = service.getById(id);
    return Response.ok(ApiEnvelope.ok(CityResponse.from(found))).build();
  }

  /**
   * Retrieves a city by its IBGE code.
   *
   * @param ibgeCode the IBGE code of the city.
   * @return a Response containing the found city.
   */
  @GET
  @Path("/ibge/{ibgeCode}")
  public Response getByIbgeCode(@PathParam("ibgeCode") String ibgeCode) {
    Objects.requireNonNull(ibgeCode, "ibgeCode");
    City found = service.getByIbgeCode(ibgeCode);
    return Response.ok(ApiEnvelope.ok(CityResponse.from(found))).build();
  }

  /**
   * Deletes cities by their unique identifiers.
   *
   * @param req the request containing the unique identifiers of the cities to delete.
   * @return a Response containing the result of the deletion.
   */
  @DELETE
  public Response delete(@Valid UuidsRequest req) {
    Objects.requireNonNull(req, "req");
    long deleted = service.deleteByIds(req.ids());
    return Response.ok(ApiEnvelope.ok(new DeleteResult(deleted))).build();
  }
}
