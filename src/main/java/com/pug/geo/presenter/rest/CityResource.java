package com.pug.geo.presenter.rest;

import com.pug.geo.domain.City;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.geo.infra.read.dtos.CityView;
import com.pug.geo.presenter.dtos.CityCreateOrUpdateRequest;
import com.pug.geo.presenter.dtos.CityResponse;
import com.pug.geo.presenter.mappers.CityPresenter;
import com.pug.geo.service.CityReadService;
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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
@Path("/geo/cities")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CityResource {

  @Inject CityService service;
  @Inject CityReadService readService;

  @Context UriInfo uri;

  @POST
  public Response create(@Valid CityCreateOrUpdateRequest req) {
    Objects.requireNonNull(req, "req");
    City created = service.save(req.name(), new IbgeCode(req.ibgeCode()));
    CityView view = readService.getView(created.getId());
    CityResponse body = CityPresenter.toResponse(view);
    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

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

  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") UUID id, @Valid CityCreateOrUpdateRequest req) {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(req, "req");
    City patch = City.createNew(req.name(), new IbgeCode(req.ibgeCode()));
    City updated = service.update(id, patch);
    CityView view = readService.getView(updated.getId());
    CityResponse body = CityPresenter.toResponse(view);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  @GET
  public Response listOrSearch(@QueryParam("q") String q) {
    List<CityView> views =
        (q == null || q.isBlank()) ? readService.listViews() : readService.search(q);
    List<CityResponse> body = views.stream().map(CityPresenter::toResponse).toList();
    return Response.ok(ApiEnvelope.ok(BulkCreateResult.of(body))).build();
  }

  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") UUID id) {
    Objects.requireNonNull(id, "id");
    CityView view = readService.getView(id);
    CityResponse body = CityPresenter.toResponse(view);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  @GET
  @Path("/ibge/{ibgeCode}")
  public Response getByIbgeCode(@PathParam("ibgeCode") String ibgeCode) {
    Objects.requireNonNull(ibgeCode, "ibgeCode");
    CityView view = readService.getByIbgeCode(ibgeCode);
    CityResponse body = CityPresenter.toResponse(view);
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  @DELETE
  public Response delete(@Valid UuidsRequest req) {
    Objects.requireNonNull(req, "req");
    Map<String, Long> deleted = service.deleteByIds(req.ids());
    return Response.ok(ApiEnvelope.ok(new DeleteResult(deleted))).build();
  }
}
