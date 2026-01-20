package com.pug.partner.presenter;

import com.pug.partner.domain.vos.Cnpj;
import com.pug.partner.infra.read.dtos.EntityView;
import com.pug.partner.presenter.dtos.EntityCreateRequest;
import com.pug.partner.presenter.dtos.EntityResponse;
import com.pug.partner.presenter.dtos.EntityUpdateRequest;
import com.pug.partner.presenter.mappers.EntityPresenter;
import com.pug.partner.service.EntityReadService;
import com.pug.partner.service.EntityService;
import com.pug.partner.service.dtos.EntityCreateOrUpdateCommand;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.presenter.dtos.DeleteResult;
import com.pug.shared.presenter.dtos.UuidsRequest;
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
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST resource for managing partner entities.
 */
@ApplicationScoped
@Path("/partners/entities")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EntityResource {

  @Inject
  EntityService writeService;
  @Inject
  EntityReadService readService;

  @Context
  UriInfo uri;

  /**
   * Create a new entity.
   *
   * @param req the entity creation request
   * @return the response containing the created entity view
   * @throws com.pug.shared.exceptions.DuplicateResourceException if an entity with the same CNPJ already exists.
   * @throws AppValidationException                               if input validation fails (e.g., blank name, invalid CNPJ).
   * @throws com.pug.shared.exceptions.ResourceNotFoundException  if the specified city does not exist.
   */
  @POST
  public Response create(@Valid EntityCreateRequest req) {
    var cmd =
            new EntityCreateOrUpdateCommand(
                    req.cnpjString(), req.name(), req.cityIbgeString(), req.address());
    var createdEntityDomain = writeService.save(cmd);
    EntityResponse body = EntityPresenter.toResponse(readService.getViewById(createdEntityDomain.getId()));
    URI location = uri.getAbsolutePathBuilder().path(createdEntityDomain.getId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Update an existing entity.
   *
   * @param id  the entity ID
   * @param req the entity update request
   * @return the response containing the updated entity view
   * @throws com.pug.shared.exceptions.ResourceNotFoundException  if the Entity is not found.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if an entity with the same CNPJ already exists.
   * @throws AppValidationException                               if input validation fails.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException  if the specified city does not exist.
   */
  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid EntityUpdateRequest req) {
    var cmd =
            new EntityCreateOrUpdateCommand(
                    req.cnpjString(), req.name(), req.cityIbgeString(), req.address());
    var updatedEntityDomain = writeService.update(id, cmd);
    EntityResponse body = EntityPresenter.toResponse(readService.getViewById(updatedEntityDomain.getId()));
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Delete entities by their IDs.
   *
   * @param req the request containing the IDs to delete
   * @return the response containing the deletion result
   * @throws com.pug.shared.exceptions.ReferencedEntityException if any entity is still referenced.
   */
  @DELETE
  public Response delete(@Valid UuidsRequest req) {
    Map<DeleteKeys, Long> deleted = writeService.deleteAll(req.ids());
    return Response.ok(ApiEnvelope.ok(new DeleteResult(deleted))).build();
  }

  /**
   * Get entity by ID.
   *
   * @param id the entity ID
   * @return the response containing the entity view
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the Entity is not found.
   */
  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    EntityResponse body = EntityPresenter.toResponse(readService.getViewById(id));
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Get entity by CNPJ.
   *
   * @param cnpjRaw the raw CNPJ string
   * @return the response containing the entity view
   * @throws AppValidationException                              if the provided CNPJ is malformed.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the Entity is not found.
   */
  @GET
  @Path("/by-cnpj/{cnpj}")
  public Response getByCnpj(@PathParam("cnpj") String cnpjRaw) {
    Cnpj cnpjVO;
    cnpjVO = new Cnpj(cnpjRaw);
    EntityResponse body = EntityPresenter.toResponse(readService.getViewByCnpj(cnpjVO.toString()));
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * List or search entities.
   *
   * @param q      optional search query
   * @param cityId optional city ID to filter by
   * @return the response containing the list of entity views
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if any associated city or other data is missing.
   */
  @GET
  public Response listOrSearch(@QueryParam("q") String q, @QueryParam("cityId") @UuidV7 UUID cityId) { // Adicionado @UuidV7
    List<EntityView> views;
    if (cityId != null) {
      views = readService.listViewsByCityId(cityId);
    } else if (!StringUtils.isEmpty(q)) {
      views = readService.searchViews(q);
    } else {
      views = readService.listViews();
    }

    List<EntityResponse> body = views.stream().map(EntityPresenter::toResponse).collect(Collectors.toList());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }
}