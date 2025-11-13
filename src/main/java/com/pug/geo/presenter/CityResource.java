package com.pug.geo.presenter;

import com.pug.geo.domain.City;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.geo.infra.read.dtos.CityView;
import com.pug.geo.presenter.dtos.CityCreateOrUpdateRequest;
import com.pug.geo.presenter.dtos.CityResponse;
import com.pug.geo.presenter.mappers.CityPresenter;
import com.pug.geo.service.CityReadService;
import com.pug.geo.service.CityService;
import com.pug.geo.service.dtos.CreateOrUpdateCityCommand;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.presenter.dtos.BulkCreateRequest;
import com.pug.shared.presenter.dtos.BulkCreateResult;
import com.pug.shared.presenter.dtos.DeleteResult;
import com.pug.shared.presenter.dtos.UuidsRequest;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.utils.StringUtils;
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

/** REST resource for managing cities. */
@ApplicationScoped
@Path("/geo/cities")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CityResource {

  @Inject CityService writeService;
  @Inject CityReadService readService;

  @Context UriInfo uri;

  /**
   * Creates a new city.
   *
   * @param req the city creation request.
   * @return a Response containing the created city.
   */
  @POST
  public Response create(@Valid CityCreateOrUpdateRequest req) {
    City created =
        writeService.save(new CreateOrUpdateCityCommand(req.name(), new IbgeCode(req.ibgeCode())));
    CityResponse body = CityPresenter.toResponse(readService.getView(created.getId()));
    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Creates multiple cities in bulk.
   *
   * @param req the bulk creation request.
   * @return a Response indicating the result of the bulk creation.
   */
  @POST
  @Path("/bulk")
  public Response createBulk(@Valid BulkCreateRequest<CityCreateOrUpdateRequest> req) {
    List<CreateOrUpdateCityCommand> toSave =
        req.entities().stream()
            .map(r -> new CreateOrUpdateCityCommand(r.name(), new IbgeCode(r.ibgeCode())))
            .toList();
    List<City> created = writeService.saveAll(toSave);
    return Response.status(Response.Status.CREATED)
        .entity(ApiEnvelope.created(BulkCreateResult.sizeOnly(created.size())))
        .build();
  }

  /**
   * Updates an existing city.
   *
   * @param id the ID of the city to update.
   * @param req the city update request.
   * @return a Response containing the updated city.
   */
  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") UUID id, @Valid CityCreateOrUpdateRequest req) {
    City updated =
        writeService.update(
            id, new CreateOrUpdateCityCommand(req.name(), new IbgeCode(req.ibgeCode())));
    CityResponse body = CityPresenter.toResponse(readService.getView(updated.getId()));
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Deletes cities by their IDs.
   *
   * @param req the request containing the IDs of the cities to delete.
   * @return a Response indicating the result of the deletion.
   */
  @DELETE
  public Response delete(@Valid UuidsRequest req) {
    Map<DeleteKeys, Long> deleted = writeService.deleteByIds(req.ids());
    return Response.ok(ApiEnvelope.ok(new DeleteResult(deleted))).build();
  }

  /**
   * Retrieves a city by its ID.
   *
   * @param id the ID of the city.
   * @return a Response containing the city.
   */
  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") UUID id) {
    CityResponse body = CityPresenter.toResponse(readService.getView(id));
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a city by its IBGE code.
   *
   * @param ibgeCode the IBGE code of the city.
   * @return a Response containing the city.
   */
  @GET
  @Path("/ibge/{ibgeCode}")
  public Response getByIbgeCode(@PathParam("ibgeCode") String ibgeCode) {
    CityResponse body = CityPresenter.toResponse(readService.getViewByIbgeCode(ibgeCode));
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Lists or searches for cities.
   *
   * @param q the optional search query.
   * @return a Response containing the list of cities.
   */
  @GET
  public Response listOrSearch(@QueryParam("q") String q) {
    List<CityView> views =
        (StringUtils.isEmpty(q)) ? readService.listViews() : readService.search(q);
    List<CityResponse> body = views.stream().map(CityPresenter::toResponse).toList();
    return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(body))).build();
  }
}
