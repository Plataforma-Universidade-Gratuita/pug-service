package com.pug.partner.presenter;

import com.pug.geo.presenter.mappers.CityPresenter;
import com.pug.partner.infra.read.dtos.EntityView;
import com.pug.partner.presenter.dtos.EntityCreateRequest;
import com.pug.partner.presenter.dtos.EntityResponse;
import com.pug.partner.presenter.dtos.EntityUpdateRequest;
import com.pug.partner.presenter.mappers.EntityPresenter;
import com.pug.partner.service.EntityReadService;
import com.pug.partner.service.EntityService;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.utils.PresenterUtils;
import com.pug.shared.utils.StringUtils;
import com.pug.shared.validation.UuidV7;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
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

/**
 * REST API Resource controller for managing Partner Entities.
 *
 * <p>This class exposes endpoints to create, retrieve, update, and delete partner organizations. It
 * delegates commands to the {@link EntityService} (writes) and queries to the {@link
 * EntityReadService} (reads), strictly adhering to CQRS architectural principles.
 */
@ApplicationScoped
@Path("/partner/entities")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EntityResource {

  @Inject EntityService writeService;
  @Inject EntityReadService readService;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  /**
   * Retrieves a specific partner entity by its unique UUID identifier.
   *
   * @param id the unique identifier (UUIDv7) of the partner entity
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     EntityResponse}
   * @throws ResourceNotFoundException if the entity is not found
   */
  @GET
  @Path("/{id}")
  @Authenticated
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    EntityResponse body = EntityPresenter.toResponse(readService.getViewById(id), locale());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a specific partner entity by its unique corporate identification (CNPJ).
   *
   * @param cnpjRaw the exact 14-digit numeric CNPJ string
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     EntityResponse}
   * @throws AppValidationException if the provided CNPJ is malformed
   * @throws ResourceNotFoundException if the entity is not found
   */
  @GET
  @Path("/by-cnpj/{cnpj}")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response getByCnpj(@PathParam("cnpj") @NotNull String cnpjRaw) {
    EntityResponse body = EntityPresenter.toResponse(readService.getViewByCnpj(cnpjRaw), locale());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves a collection of partner entities.
   *
   * <p>If the optional {@code q} parameter is provided, it executes a full-text search against the
   * entities' names. If the {@code cityId} parameter is provided, it filters the results
   * geographically. If both are omitted, it returns all entities.
   *
   * @param q the optional search query string
   * @param cityId the optional geographic filter
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     EntityResponse}
   */
  @GET
  @Authenticated
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
   * Retrieves a list of geographical cities currently associated with at least one partner entity.
   *
   * <p>This endpoint scans the active partner entities, extracts their unique geographical
   * locations, and returns the corresponding city details. It is optimized to only return cities
   * actively in use by the platform's partner network, filtering the results in-memory.
   *
   * @return a {@link Response} containing an {@link ApiEnvelope} with the list of used cities.
   */
  @GET
  @Path("/cities")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response listCities() {
    var body =
        readService.listCityViews().stream()
            .map(CityPresenter::toResponse)
            .collect(Collectors.toList());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Registers a new partner entity within the platform.
   *
   * @param req the validated {@link EntityCreateRequest} containing the organization's details
   * @return an HTTP 201 Created response containing a {@code Location} header and the created
   *     {@link EntityResponse}
   * @throws DuplicateResourceException if an entity with the same CNPJ already exists
   * @throws AppValidationException if input validation fails at the domain level
   * @throws ResourceNotFoundException if the specified city does not exist
   */
  @POST
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response create(@Valid EntityCreateRequest req) {
    var cmd = EntityPresenter.toCommand(req);
    var createdEntityDomain = writeService.save(cmd);

    EntityResponse body =
        EntityPresenter.toResponse(readService.getViewById(createdEntityDomain.getId()), locale());

    URI location =
        uri.getAbsolutePathBuilder().path(createdEntityDomain.getId().toString()).build();

    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Partially updates an existing partner entity's details.
   *
   * <p>Omitting fields in the request payload will result in those fields retaining their current
   * state in the database.
   *
   * @param id the unique identifier (UUIDv7) of the entity to update
   * @param req the validated {@link EntityUpdateRequest} containing the modified data
   * @return an HTTP 200 OK response containing the updated {@link EntityResponse}
   * @throws ResourceNotFoundException if the entity or referenced city does not exist
   * @throws AppValidationException if input validation fails
   */
  @PUT
  @Path("/{id}")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid EntityUpdateRequest req) {
    var cmd = EntityPresenter.toCommand(req);
    var updatedEntityDomain = writeService.update(id, cmd);

    EntityResponse body =
        EntityPresenter.toResponse(readService.getViewById(updatedEntityDomain.getId()), locale());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Permanently removes a partner entity from the system.
   *
   * @param id the unique identifier (UUIDv7) of the entity to delete
   * @return an HTTP 200 OK response with an empty data payload indicating successful deletion
   */
  @DELETE
  @Path("/{id}")
  @RolesAllowed({"ADMIN"})
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.ok(ApiEnvelope.ok(null)).build();
  }

  /** Helper method to determine the preferred locale from the incoming request headers. */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
