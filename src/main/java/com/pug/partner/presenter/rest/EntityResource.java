package com.pug.partner.presenter.rest;

import com.pug.partner.domain.Entity;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.partner.infra.read.dtos.EntityView;
import com.pug.partner.presenter.dtos.EntityCreateOrUpdateRequest;
import com.pug.partner.presenter.dtos.EntityResponse;
import com.pug.partner.presenter.mappers.EntityPresenter;
import com.pug.partner.service.EntityReadService;
import com.pug.partner.service.EntityService;
import com.pug.shared.presenter.dtos.DeleteResult;
import com.pug.shared.presenter.dtos.UuidsRequest;
import com.pug.shared.presenter.rest.ApiEnvelope;
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
import java.util.Objects;
import java.util.UUID;

/** REST resource for managing partner entities. */
@ApplicationScoped
@Path("/partners/entities")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EntityResource {

  @Inject EntityService writeService;
  @Inject EntityReadService readService;

  @Context UriInfo uri;

  /**
   * Create a new entity.
   *
   * @param req the entity creation request
   * @return the response containing the created entity view
   */
  @POST
  public Response create(@Valid EntityCreateOrUpdateRequest req) {
    Objects.requireNonNull(req, "req");
    var created = writeService.save(new Cnpj(req.cnpj()), req.name(), req.cityId(), req.address());
    EntityResponse body = EntityPresenter.toResponse(readService.getView(created.getId()));
    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Update an existing entity.
   *
   * @param id the entity ID
   * @param req the entity update request
   * @return the response containing the updated entity view
   */
  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") UUID id, @Valid EntityCreateOrUpdateRequest req) {
    Objects.requireNonNull(req, "req");
    Objects.requireNonNull(id, "id");
    writeService.update(
        id, Entity.createNew(new Cnpj(req.cnpj()), req.name(), req.cityId(), req.address()));
    EntityResponse body = EntityPresenter.toResponse(readService.getView(id));
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Delete entities by their IDs.
   *
   * @param req the request containing the IDs to delete
   * @return the response containing the deletion result
   */
  @DELETE
  public Response delete(@Valid UuidsRequest req) {
    Objects.requireNonNull(req, "req");
    Map<String, Long> deleted = writeService.deleteByIds(req.ids());
    return Response.ok(ApiEnvelope.ok(new DeleteResult(deleted))).build();
  }

  /**
   * Get entity by ID.
   *
   * @param id the entity ID
   * @return the response containing the entity view
   */
  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    Objects.requireNonNull(id, "id");
    EntityResponse body = EntityPresenter.toResponse(readService.getView(id));
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Get entity by CNPJ.
   *
   * @param cnpjRaw the raw CNPJ string
   * @return the response containing the entity view
   */
  @GET
  @Path("/by-cnpj/{cnpj}")
  public Response getByCnpj(@PathParam("cnpj") String cnpjRaw) {
    Objects.requireNonNull(cnpjRaw, "cnpj");
    EntityResponse body = EntityPresenter.toResponse(readService.getViewByCnpj(cnpjRaw));
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * List or search entities.
   *
   * @param q optional search query
   * @param cityId optional city ID to filter by
   * @return the response containing the list of entity views
   */
  @GET
  public Response listOrSearch(@QueryParam("q") String q, @QueryParam("cityId") UUID cityId) {
    if (cityId != null) {
      List<EntityView> views = readService.listViewsByCityId(cityId);
      List<EntityResponse> body = views.stream().map(EntityPresenter::toResponse).toList();
      return Response.ok(ApiEnvelope.ok(body)).build();
    }
    if (q != null && !q.isBlank()) {
      List<EntityView> views = readService.searchViews(q);
      List<EntityResponse> body = views.stream().map(EntityPresenter::toResponse).toList();
      return Response.ok(ApiEnvelope.ok(body)).build();
    }
    List<EntityView> all = readService.listViews();
    List<EntityResponse> body = all.stream().map(EntityPresenter::toResponse).toList();
    return Response.ok(ApiEnvelope.ok(body)).build();
  }
}
