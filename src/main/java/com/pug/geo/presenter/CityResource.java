package com.pug.geo.presenter;

import com.pug.geo.domain.City;
import com.pug.geo.infra.read.dtos.CityView;
import com.pug.geo.presenter.dtos.CityCreateRequest;
import com.pug.geo.presenter.dtos.CityResponse;
import com.pug.geo.presenter.dtos.CityUpdateRequest;
import com.pug.geo.presenter.mappers.CityPresenter;
import com.pug.geo.service.CityReadService;
import com.pug.geo.service.CityService;
import com.pug.geo.service.dtos.CityCreateCommand;
import com.pug.geo.service.dtos.CityUpdateCommand;
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
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST resource for managing cities.
 */
@ApplicationScoped
@Path("/geo/cities")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CityResource {

  @Inject
  CityService writeService;
  @Inject
  CityReadService readService;

  @Context
  UriInfo uri;

  /**
   * Retrieves a city by its ID.
   *
   * @param id the ID of the city.
   * @return a Response containing the city.
   */
  @GET
  @Path("{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    CityView cityView = readService.getViewById(id);
    CityResponse responseBody = CityPresenter.toResponse(cityView);
    return Response.ok(ApiEnvelope.ok(responseBody)).build();
  }

  /**
   * Lists or searches for cities.
   *
   * @param q the optional search query.
   * @return a Response containing the list of cities.
   */
  @GET
  public Response list(@QueryParam("q") String q) {
    List<CityView> views;

    if (StringUtils.isNotEmpty(q)) {
      views = readService.search(q);
    } else {
      views = readService.listViews();
    }

    List<CityResponse> responseBody =
            views.stream().map(CityPresenter::toResponse).collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(responseBody)).build();
  }

  /**
   * Retrieves a city by its IBGE code.
   *
   * @param ibgeCode the IBGE code of the city.
   * @return a Response containing the city.
   */
  @GET
  @Path("by-ibge/{ibgeCode}")
  public Response getByIbgeCode(@PathParam("ibgeCode") String ibgeCode) {
    CityView cityView = readService.getViewByIbgeCode(ibgeCode);
    CityResponse responseBody = CityPresenter.toResponse(cityView);
    return Response.ok(ApiEnvelope.ok(responseBody)).build();
  }

  /**
   * Creates a new city.
   *
   * @param req the city creation request.
   * @return a Response containing the created city.
   */
  @POST
  public Response create(@Valid CityCreateRequest req) {
    CityCreateCommand cmd = new CityCreateCommand(req.name(), req.ibgeCodeString());
    City createdCityDomain = writeService.save(cmd);

    // Re-fetch view to ensure consistent response format
    CityView cityView = readService.getViewById(createdCityDomain.getId());
    CityResponse responseBody = CityPresenter.toResponse(cityView);

    URI location = uri.getAbsolutePathBuilder().path(responseBody.id().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(responseBody)).build();
  }

  /**
   * Updates an existing city.
   *
   * @param id  the ID of the city to update.
   * @param req the city update request.
   * @return a Response containing the updated city.
   */
  @PUT
  @Path("{id}")
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid CityUpdateRequest req) {
    CityUpdateCommand cmd = new CityUpdateCommand(req.name(), req.ibgeCodeString());
    City updatedCityDomain = writeService.update(id, cmd);

    CityView cityView = readService.getViewById(updatedCityDomain.getId());
    CityResponse responseBody = CityPresenter.toResponse(cityView);

    return Response.ok(ApiEnvelope.ok(responseBody)).build();
  }

  /**
   * Deletes a city by its ID.
   *
   * @param id the ID of the city to delete.
   * @return a Response indicating the result of the deletion.
   */
  @DELETE
  @Path("{id}")
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }
}