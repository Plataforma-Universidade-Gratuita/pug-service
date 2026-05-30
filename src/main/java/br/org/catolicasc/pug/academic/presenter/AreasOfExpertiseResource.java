package br.org.catolicasc.pug.academic.presenter;

import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.infra.read.dtos.AreaOfExpertiseView;
import br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise.AreaOfExpertiseComplexSearchRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise.AreaOfExpertiseCreateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise.AreaOfExpertiseResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise.AreaOfExpertiseUpdateRequest;
import br.org.catolicasc.pug.academic.presenter.mappers.AreaOfExpertisePresenter;
import br.org.catolicasc.pug.academic.service.AreasOfExpertiseReadService;
import br.org.catolicasc.pug.academic.service.AreasOfExpertiseService;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseComplexSearchCriteria;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseUpdateCommand;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
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
 * REST API resource controller for managing academic areas of expertise.
 *
 * <p>This class exposes endpoints to create, retrieve, update, delete, and search academic areas of
 * expertise. It delegates commands to the {@link AreasOfExpertiseService} and queries to the {@link
 * AreasOfExpertiseReadService}, adhering to CQRS principles.
 */
@ApplicationScoped
@Path("/v1/academic/areas-of-expertise")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AreasOfExpertiseResource {

  @Inject AreasOfExpertiseService writeService;
  @Inject AreasOfExpertiseReadService readService;

  @Context UriInfo uri;
  @Context HttpHeaders headers;

  /**
   * Retrieves a specific area of expertise by its unique UUID identifier.
   *
   * @param id the unique identifier (UUIDv7) of the area of expertise
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the {@link
   *     AreaOfExpertiseResponse}
   * @throws ResourceNotFoundException if the area of expertise is not found
   */
  @GET
  @Path("/{id}")
  @Authenticated
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    AreaOfExpertiseView view = readService.getViewById(id);
    AreaOfExpertiseResponse body = AreaOfExpertisePresenter.toResponse(view, locale());
    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Retrieves areas of expertise, optionally restricted to a provided collection of identifiers.
   *
   * <p>When one or more {@code ids} query parameters are present, this endpoint returns only the
   * matching areas of expertise. Otherwise, it returns the complete list ordered according to the
   * underlying query implementation.
   *
   * @param ids the optional area-of-expertise identifiers used to restrict the returned collection
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with a list of {@link
   *     AreaOfExpertiseResponse}
   */
  @GET
  @Authenticated
  public Response list(@QueryParam("ids") List<UUID> ids) {
    List<AreaOfExpertiseView> views =
        CollectionUtils.isEmpty(ids) ? readService.listViews() : readService.listViewsByIds(ids);
    List<AreaOfExpertiseResponse> body =
        views.stream().map(view -> AreaOfExpertisePresenter.toResponse(view, locale())).toList();

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Executes paginated area-of-expertise search using the academic complex-search contract.
   *
   * @param page the zero-based page index
   * @param size the requested page size; {@code 1} returns the full result set in a single page
   * @param request the optional complex-search filters
   * @return an HTTP 200 OK response containing an {@link ApiEnvelope} with the paginated search
   *     result
   */
  @POST
  @Path("search")
  @Authenticated
  public Response search(
      @QueryParam("page") @DefaultValue("0") @Min(0) int page,
      @QueryParam("size") @DefaultValue("25") @Min(1) int size,
      @Valid AreaOfExpertiseComplexSearchRequest request) {
    AreaOfExpertiseComplexSearchCriteria criteria =
        request == null
            ? new AreaOfExpertiseComplexSearchCriteria(null)
            : new AreaOfExpertiseComplexSearchCriteria(request.name());
    var result = readService.search(new PageQuery(page, size), criteria);
    var responseBody =
        new PageResponse<>(
            result.content().stream()
                .map(view -> AreaOfExpertisePresenter.toResponse(view, locale()))
                .toList(),
            result.page(),
            result.size(),
            result.totalElements(),
            result.totalPages());

    return Response.ok(ApiEnvelope.ok(responseBody)).build();
  }

  /**
   * Registers a new academic area of expertise within the platform.
   *
   * @param req the validated {@link AreaOfExpertiseCreateRequest} payload
   * @return an HTTP 201 Created response containing a {@code Location} header and the created
   *     {@link AreaOfExpertiseResponse}
   * @throws DuplicateResourceException if an area of expertise with the exact name already exists
   */
  @POST
  @RolesAllowed("ADMIN")
  public Response create(@Valid AreaOfExpertiseCreateRequest req) {
    AreaOfExpertiseCreateCommand cmd = AreaOfExpertisePresenter.toCommand(req);
    School created = writeService.save(cmd);

    AreaOfExpertiseView view = readService.getViewById(created.getId());
    AreaOfExpertiseResponse body = AreaOfExpertisePresenter.toResponse(view, locale());

    URI location = uri.getAbsolutePathBuilder().path(created.getId().toString()).build();
    return Response.created(location).entity(ApiEnvelope.created(body)).build();
  }

  /**
   * Updates an existing area of expertise's details.
   *
   * @param id the unique identifier (UUIDv7) of the area of expertise to update
   * @param req the validated {@link AreaOfExpertiseUpdateRequest} containing the modified data
   * @return an HTTP 200 OK response containing the updated {@link AreaOfExpertiseResponse}
   */
  @PUT
  @Path("/{id}")
  @RolesAllowed("ADMIN")
  public Response update(
      @PathParam("id") @UuidV7 UUID id, @Valid AreaOfExpertiseUpdateRequest req) {
    AreaOfExpertiseUpdateCommand cmd = AreaOfExpertisePresenter.toCommand(req);
    writeService.update(id, cmd);

    AreaOfExpertiseView view = readService.getViewById(id);
    AreaOfExpertiseResponse body = AreaOfExpertisePresenter.toResponse(view, locale());

    return Response.ok(ApiEnvelope.ok(body)).build();
  }

  /**
   * Permanently removes an academic area of expertise from the system.
   *
   * @param id the unique identifier (UUIDv7) of the area of expertise to delete
   * @return an HTTP 204 No Content response when deletion succeeds
   */
  @DELETE
  @Path("/{id}")
  @RolesAllowed("ADMIN")
  public Response delete(@PathParam("id") @UuidV7 UUID id) {
    writeService.delete(id);
    return Response.noContent().build();
  }

  /** Helper method to determine the preferred locale from the incoming request headers. */
  private Locale locale() {
    return PresenterUtils.pickLocale(headers.getAcceptableLanguages());
  }
}
