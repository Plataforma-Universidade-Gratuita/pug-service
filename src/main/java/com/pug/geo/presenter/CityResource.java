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
 * REST API Resource controller for managing geographic Cities.
 * <p>
 * This class exposes endpoints to create, retrieve, update, and delete cities.
 * It acts as the HTTP entry point, orchestrating requests by delegating commands to the
 * {@link CityService} (writes) and queries to the {@link CityReadService} (reads),
 * strictly adhering to CQRS architectural principles. All responses are wrapped in a
 * standard {@link ApiEnvelope}.
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
   * Retrieves a specific city by its unique UUID identifier.
   *
   * @param id the unique identifier (UUIDv7) of the requested city
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link CityResponse}
   */
  @GET
  @Path("{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    CityView cityView = readService.getViewById(id);
    CityResponse responseBody = CityPresenter.toResponse(cityView);
    return Response.ok(ApiEnvelope.ok(responseBody)).build();
  }

  /**
   * Retrieves a specific city using its natural key (IBGE code).
   *
   * @param ibgeCode the exact 7-digit IBGE code of the city
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link CityResponse}
   */
  @GET
  @Path("by-ibge/{ibgeCode}")
  public Response getByIbgeCode(@PathParam("ibgeCode") String ibgeCode) {
    CityView cityView = readService.getViewByIbgeCode(ibgeCode);
    CityResponse responseBody = CityPresenter.toResponse(cityView);
    return Response.ok(ApiEnvelope.ok(responseBody)).build();
  }

  /**
   * Retrieves a collection of cities.
   * <p>
   * If the optional {@code q} parameter is provided, it executes a full-text search against
   * the cities' names. If omitted, it returns an unfiltered list of all available cities.
   *
   * @param q the optional search query string used to filter cities by name
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link CityResponse}
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
   * Registers a new geographic city within the platform.
   * <p>
   * Applies Bean Validation to the incoming payload before delegating to the application service.
   *
   * @param req the validated {@link CityCreateRequest} containing the city's details
   * @return an HTTP 201 Created response containing a {@code Location} header and the created {@link CityResponse}
   */
  @POST
  public Response create(@Valid CityCreateRequest req) {
    CityCreateCommand cmd = new CityCreateCommand(req.name(), req.ibgeCodeString());
    City createdCityDomain = writeService.save(cmd);

    CityView cityView = readService.getViewById(createdCityDomain.getId());
    CityResponse responseBody = CityPresenter.toResponse(cityView);

    URI location = uri.getAbsolutePathBuilder().path(responseBody.id().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(responseBody)).build();
  }

  /**
   * Partially updates an existing city's details.
   * <p>
   * Omitting fields in the request payload will result in those fields retaining their
   * current state in the database.
   *
   * @param id  the unique identifier (UUIDv7) of the city to update
   * @param req the validated {@link CityUpdateRequest} containing the modified data
   * @return an HTTP 200 OK response containing the updated {@link CityResponse}
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
   * Permanently removes a city from the system.
   * <p>
   * <i>Note:</i> Protected default cities cannot be deleted and will trigger a 422 Unprocessable Entity.
   *
   * @param id the unique identifier (UUIDv7) of the city to delete
   * @return an HTTP 200 OK response with an empty data payload indicating successful deletion
   */
  @DELETE
  @Path("{id}")
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }
}