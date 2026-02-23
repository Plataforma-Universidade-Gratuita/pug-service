package com.pug.partner.presenter;

import com.pug.partner.infra.read.dtos.EntityView;
import com.pug.partner.presenter.dtos.EntityCreateRequest;
import com.pug.partner.presenter.dtos.EntityResponse;
import com.pug.partner.presenter.dtos.EntityUpdateRequest;
import com.pug.partner.presenter.mappers.EntityPresenter;
import com.pug.partner.service.EntityReadService;
import com.pug.partner.service.EntityService;
import com.pug.partner.service.dtos.EntityCreateCommand;
import com.pug.partner.service.dtos.EntityUpdateCommand;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.utils.PresenterUtils;
import com.pug.shared.utils.StringUtils;
import com.pug.shared.validation.UuidV7;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

/** REST resource for managing partner entities. */
@ApplicationScoped
@Path("/partners/entities")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EntityResource {

  @Inject EntityService writeService;
  @Inject EntityReadService readService;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  /**
   * Get entityId by ID.
   *
   * @param id the entityId ID
   * @return the response containing the entityId view
   * @throws ResourceNotFoundException if the Entity is not found.
   */
  @GET
  @Path("{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    EntityResponse body = EntityPresenter.toResponse(readService.getViewById(id), locale());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * List or search entities.
   *
   * @param q optional search query (by name)
   * @param cityId optional city ID to filter by
   * @return the response containing the list of entityId views
   */
  @GET
  public Response list(@QueryParam("q") String q, @QueryParam("cityId") @UuidV7 UUID cityId) {

    List<EntityView> views;

    if (cityId != null) {
      views = readService.listViewsByCityId(cityId);
    } else if (StringUtils.isNotEmpty(q)) {
      views = readService.searchViews(q);
    } else {
      views = readService.listViews();
    }

    List<EntityResponse> body =
        views.stream()
            .map(v -> EntityPresenter.toResponse(v, locale()))
            .collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Get entityId by CNPJ.
   *
   * @param cnpjRaw the raw CNPJ string
   * @return the response containing the entityId view
   * @throws AppValidationException if the provided CNPJ is malformed.
   * @throws ResourceNotFoundException if the Entity is not found.
   */
  @GET
  @Path("by-cnpj/{cnpj}")
  public Response getByCnpj(@PathParam("cnpj") @NotNull String cnpjRaw) {
    EntityResponse body = EntityPresenter.toResponse(readService.getViewByCnpj(cnpjRaw), locale());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Create a new entityId.
   *
   * @param req the entityId creation request
   * @return the response containing the created entityId view
   * @throws DuplicateResourceException if an entityId with the same CNPJ already exists.
   * @throws AppValidationException if input validation fails.
   * @throws ResourceNotFoundException if the specified city does not exist.
   */
  @POST
  public Response create(@Valid EntityCreateRequest req) {
    var cmd = new EntityCreateCommand(req.cnpjString(), req.name(), req.cityId(), req.address());
    var createdEntityDomain = writeService.save(cmd);

    EntityResponse body =
        EntityPresenter.toResponse(readService.getViewById(createdEntityDomain.getId()), locale());

    URI location =
        uri.getAbsolutePathBuilder().path(createdEntityDomain.getId().toString()).build();

    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Update an existing entityId.
   *
   * @param id the entityId ID
   * @param req the entityId update request
   * @return the response containing the updated entityId view
   * @throws ResourceNotFoundException if the Entity is not found.
   * @throws DuplicateResourceException if updated details conflict with existing records.
   * @throws AppValidationException if input validation fails.
   */
  @PUT
  @Path("{id}")
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid EntityUpdateRequest req) {
    var cmd = new EntityUpdateCommand(req.cnpjString(), req.name(), req.cityId(), req.address());
    var updatedEntityDomain = writeService.update(id, cmd);

    EntityResponse body =
        EntityPresenter.toResponse(readService.getViewById(updatedEntityDomain.getId()), locale());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Delete an entityId by ID.
   *
   * @param id the ID of the entityId to delete
   * @return 200 OK with empty data (idempotent).
   */
  @DELETE
  @Path("{id}")
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  /** Picks the best locale from the request headers. */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
