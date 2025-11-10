package com.pug.partner.presenter.rest;

import com.pug.partner.domain.Entity;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.partner.presenter.dtos.EntityCreateOrUpdateRequest;
import com.pug.partner.infra.read.dtos.EntityView;
import com.pug.partner.service.EntityReadService;
import com.pug.partner.service.EntityService;
import com.pug.shared.exceptions.ResourceNotFoundException;
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

/** REST resource for managing partner entities. */
@ApplicationScoped
@Path("/partners/entities")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EntityResource {

  @Inject EntityService service;
  @Inject EntityReadService readService;

  @Context UriInfo uri;

  /**
   * Retrieves an entity by its unique identifier.
   *
   * @param id the UUID of the entity to retrieve.
   * @return a Response containing the entity view if found, or a 404 error if not found.
   */
  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") UUID id) {
    Objects.requireNonNull(id, "id");
    EntityView v = readService.getView(id);
    if (v == null) {
      throw new ResourceNotFoundException(PartnerErrorCodes.ENTITY_NOT_FOUND);
    }
    return Response.ok(ApiEnvelope.ok(v)).build();
  }

  /**
   * Retrieves an entity by its CNPJ.
   *
   * @param cnpjRaw the CNPJ of the entity to retrieve.
   * @return a Response containing the entity view if found, or a 404 error if not found.
   */
  @GET
  @Path("/by-cnpj/{cnpj}")
  public Response getByCnpj(@PathParam("cnpj") String cnpjRaw) {
    Objects.requireNonNull(cnpjRaw, "cnpj");
    var entity = service.getByCnpj(new Cnpj(cnpjRaw));
    EntityView v = readService.getView(entity.getId());
    if (v == null) {
      throw new ResourceNotFoundException(PartnerErrorCodes.ENTITY_NOT_FOUND);
    }
    return Response.ok(ApiEnvelope.ok(v)).build();
  }

  /**
   * Lists all entities or searches for entities based on query parameters.
   *
   * @param q optional search query string.
   * @param cityId optional city UUID to filter entities by city.
   * @return a Response containing a list of entity views.
   */
  @GET
  public Response listOrSearch(@QueryParam("q") String q, @QueryParam("cityId") UUID cityId) {
    if (cityId != null) {
      List<EntityView> views = readService.listViewsByCityId(cityId);
      return Response.ok(ApiEnvelope.ok(views)).build();
    }
    if (q != null && !q.isBlank()) {
      List<Entity> found = service.search(q);
      List<EntityView> views =
          found.stream().map(e -> readService.getView(e.getId())).filter(Objects::nonNull).toList();
      return Response.ok(ApiEnvelope.ok(views)).build();
    }
    List<EntityView> all = readService.listViews();
    return Response.ok(ApiEnvelope.ok(all)).build();
  }

  /**
   * Creates a new entity.
   *
   * @param req the request containing entity creation details.
   * @return a Response containing the created entity view and location header.
   */
  @POST
  public Response create(@Valid EntityCreateOrUpdateRequest req) {
    Objects.requireNonNull(req, "req");
    var created = service.save(new Cnpj(req.cnpj()), req.name(), req.cityId(), req.address());
    EntityView v = readService.getView(created.getId());
    if (v == null) {
      throw new ResourceNotFoundException(PartnerErrorCodes.ENTITY_NOT_FOUND);
    }
    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(v)).build();
  }

  /**
   * Creates multiple entities in bulk.
   *
   * @param req the request containing a list of entity creation details.
   * @return a Response containing the result of the bulk creation.
   */
  @POST
  @Path("/bulk")
  public Response createBulk(@Valid BulkCreateRequest<EntityCreateOrUpdateRequest> req) {
    Objects.requireNonNull(req, "req");
    var toSave =
        req.entities().stream()
            .map(r -> Entity.createNew(new Cnpj(r.cnpj()), r.name(), r.cityId(), r.address()))
            .toList();
    service.saveAll(toSave);
    return Response.status(Response.Status.CREATED)
        .entity(ApiEnvelope.created(BulkCreateResult.sizeOnly(toSave.size())))
        .build();
  }

  /**
   * Deletes entities by their unique identifiers.
   *
   * @param req the request containing a list of entity UUIDs to delete.
   * @return a Response containing the result of the deletion.
   */
  @DELETE
  public Response delete(@Valid UuidsRequest req) {
    Objects.requireNonNull(req, "req");
    long deleted = service.deleteByIds(req.ids());
    return Response.ok(ApiEnvelope.ok(new DeleteResult(deleted))).build();
  }
}
