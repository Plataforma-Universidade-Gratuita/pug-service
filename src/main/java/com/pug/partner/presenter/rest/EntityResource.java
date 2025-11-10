package com.pug.partner.presenter.rest;

import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.infra.read.dtos.EntityView;
import com.pug.partner.presenter.dtos.EntityCreateOrUpdateRequest;
import com.pug.partner.service.EntityReadService;
import com.pug.partner.service.EntityService;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.presenter.dtos.BulkCreateRequest;
import com.pug.shared.presenter.dtos.BulkCreateResult;
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

  @Inject EntityService service;
  @Inject EntityReadService readService;

  @Context UriInfo uri;

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
    EntityView v = readService.getView(id);
    if (v == null) {
      throw new ResourceNotFoundException(PartnerErrorCodes.ENTITY_NOT_FOUND);
    }
    return Response.ok(ApiEnvelope.ok(v)).build();
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
    EntityView v = readService.getViewByCnpj(cnpjRaw);
    if (v == null) {
      throw new ResourceNotFoundException(PartnerErrorCodes.ENTITY_NOT_FOUND);
    }
    return Response.ok(ApiEnvelope.ok(v)).build();
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
      return Response.ok(ApiEnvelope.ok(views)).build();
    }
    if (q != null && !q.isBlank()) {
      List<EntityView> views = readService.searchViews(q);
      return Response.ok(ApiEnvelope.ok(views)).build();
    }
    List<EntityView> all = readService.listViews();
    return Response.ok(ApiEnvelope.ok(all)).build();
  }

  /**
   * Create a new entity.
   *
   * @param req the entity creation request
   * @return the response containing the created entity view
   */
  @POST
  public Response create(@Valid EntityCreateOrUpdateRequest req) {
    Objects.requireNonNull(req, "req");
    var created =
        service.save(
            new com.pug.partner.domain.vos.Cnpj(req.cnpj()),
            req.name(),
            req.cityId(),
            req.address());
    EntityView v = readService.getView(created.getId());
    if (v == null) {
      throw new ResourceNotFoundException(PartnerErrorCodes.ENTITY_NOT_FOUND);
    }
    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(v)).build();
  }

  /**
   * Create multiple entities in bulk.
   *
   * @param req the bulk creation request
   * @return the response containing the bulk creation result
   */
  @POST
  @Path("/bulk")
  public Response createBulk(@Valid BulkCreateRequest<EntityCreateOrUpdateRequest> req) {
    Objects.requireNonNull(req, "req");
    var toSave =
        req.entities().stream()
            .map(
                r ->
                    com.pug.partner.domain.Entity.createNew(
                        new com.pug.partner.domain.vos.Cnpj(r.cnpj()),
                        r.name(),
                        r.cityId(),
                        r.address()))
            .toList();
    service.saveAll(toSave);
    return Response.status(Response.Status.CREATED)
        .entity(ApiEnvelope.created(BulkCreateResult.sizeOnly(toSave.size())))
        .build();
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
    Map<String, Long> deleted = service.deleteByIds(req.ids());
    return Response.ok(ApiEnvelope.ok(new DeleteResult(deleted))).build();
  }
}
