package br.org.catolicasc.pug.partner.presenter;

import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.partner.infra.read.dtos.EntityView;
import br.org.catolicasc.pug.partner.presenter.dtos.entities.EntityComplexSearchRequest;
import br.org.catolicasc.pug.partner.presenter.dtos.entities.EntityComplexSearchResponse;
import br.org.catolicasc.pug.partner.presenter.dtos.entities.EntityCreateRequest;
import br.org.catolicasc.pug.partner.presenter.dtos.entities.EntityResponse;
import br.org.catolicasc.pug.partner.presenter.dtos.entities.EntityUpdateRequest;
import br.org.catolicasc.pug.partner.presenter.mappers.EntityPresenter;
import br.org.catolicasc.pug.partner.service.EntitiesReadService;
import br.org.catolicasc.pug.partner.service.EntitiesService;
import br.org.catolicasc.pug.partner.service.dtos.entities.EntityComplexSearchCriteria;
import br.org.catolicasc.pug.shared.presenter.dtos.PageResponse;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
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

/**
 * REST API resource controller for managing partner entities.
 *
 * <p>This class exposes endpoints to create, retrieve, update, delete, and complex-search partner
 * organizations. It delegates commands to the {@link EntitiesService} and queries to the {@link
 * EntitiesReadService}, adhering to CQRS principles.
 */
@ApplicationScoped
@Path("/v1/partners/entities")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EntitiesResource {

  @Inject EntitiesService writeService;
  @Inject EntitiesReadService readService;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  /**
   * Retrieves a specific partner entity by its unique UUID identifier.
   *
   * @param id the unique identifier (UUIDv7) of the partner entity
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     EntityResponse}
   */
  @GET
  @Path("/{id}")
  @Authenticated
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    EntityResponse body = EntityPresenter.toResponse(readService.getViewById(id), locale());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves partner entities, optionally restricted to the provided identifiers.
   *
   * @param ids the optional identifiers used to restrict the returned partner entities
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the matching {@link
   *     EntityResponse} list
   */
  @GET
  @Authenticated
  public Response list(@QueryParam("ids") List<UUID> ids) {
    List<EntityView> views =
        CollectionUtils.isEmpty(ids) ? readService.listViews() : readService.listViewsByIds(ids);

    List<EntityResponse> body =
        views.stream().map(v -> EntityPresenter.toResponse(v, locale())).toList();

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Executes paginated partner-entity search using the complex-search contract.
   *
   * @param page the requested zero-based page index
   * @param size the requested page size; {@code 1} triggers the shared fetch-all behavior
   * @param request the optional complex-search payload
   * @return an HTTP 200 OK response containing a paginated complex-search result set
   */
  @POST
  @Path("/search")
  @Authenticated
  public Response search(
      @QueryParam("page") @DefaultValue("0") @Min(0) int page,
      @QueryParam("size") @DefaultValue("25") @Min(1) int size,
      @Valid EntityComplexSearchRequest request) {
    EntityComplexSearchCriteria criteria =
        request == null
            ? new EntityComplexSearchCriteria(null, null, null, null, null, null)
            : new EntityComplexSearchCriteria(
                request.name(),
                request.cnpj(),
                request.address(),
                request.cityIds(),
                request.dateFrom(),
                request.dateTo());

    var result = readService.search(new PageQuery(page, size), criteria);
    PageResponse<EntityComplexSearchResponse> responseBody =
        new PageResponse<>(
            result.content().stream()
                .map(v -> EntityPresenter.toComplexSearchResponse(v, locale()))
                .toList(),
            result.page(),
            result.size(),
            result.totalElements(),
            result.totalPages());

    return Response.ok(ApiEnvelope.ok(responseBody)).build();
  }

  /**
   * Registers a new partner entity within the platform.
   *
   * @param req the validated {@link EntityCreateRequest} containing the organization's details
   * @return an HTTP 201 Created response containing the created {@link EntityResponse}
   */
  @POST
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response create(@Valid EntityCreateRequest req) {
    Entity created = writeService.save(EntityPresenter.toCommand(req));
    EntityResponse body = EntityPresenter.toResponse(readService.getViewById(created.getId()), locale());
    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Updates an existing partner entity's details.
   *
   * @param id the unique identifier (UUIDv7) of the entity to update
   * @param req the validated {@link EntityUpdateRequest} containing the modified data
   * @return an HTTP 200 OK response containing the updated {@link EntityResponse}
   */
  @PUT
  @Path("/{id}")
  @RolesAllowed({"ADMIN", "STAFF"})
  public Response update(@PathParam("id") @UuidV7 UUID id, @Valid EntityUpdateRequest req) {
    Entity updated = writeService.update(id, EntityPresenter.toCommand(req));
    EntityResponse body = EntityPresenter.toResponse(readService.getViewById(updated.getId()), locale());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Permanently removes a partner entity from the system.
   *
   * @param id the unique identifier (UUIDv7) of the entity to delete
   * @return an HTTP 204 No Content response when deletion succeeds
   */
  @DELETE
  @Path("/{id}")
  @RolesAllowed("ADMIN")
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.noContent().build();
  }

  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
