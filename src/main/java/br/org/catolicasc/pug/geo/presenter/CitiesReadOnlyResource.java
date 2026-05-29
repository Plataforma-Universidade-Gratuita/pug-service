package br.org.catolicasc.pug.geo.presenter;

import br.org.catolicasc.pug.geo.constants.GeoApiPaths;
import br.org.catolicasc.pug.geo.infra.read.dtos.CityView;
import br.org.catolicasc.pug.geo.presenter.dtos.CityComplexSearchRequest;
import br.org.catolicasc.pug.geo.presenter.dtos.CityResponse;
import br.org.catolicasc.pug.geo.presenter.mappers.CityPresenter;
import br.org.catolicasc.pug.geo.service.CitiesReadService;
import br.org.catolicasc.pug.geo.service.dtos.CityComplexSearchCriteria;
import br.org.catolicasc.pug.shared.presenter.dtos.PageResponse;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST API resource controller responsible for handling read-only operations on geographic cities.
 *
 * <p>This controller exposes endpoints to retrieve city details, list cities, and execute paginated
 * complex-search operations. It delegates data retrieval to the {@link CitiesReadService} and
 * ensures that all responses are consistently wrapped in a standardized {@link ApiEnvelope}.
 */
@ApplicationScoped
@Path(GeoApiPaths.CITIES)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class CitiesReadOnlyResource {

  @Inject CitiesReadService readService;

  /**
   * Retrieves the details of a specific city based on its unique identifier.
   *
   * <p>This endpoint looks up a single geographical city using its UUID version 7. If the city is
   * found, it is mapped to a {@link CityResponse} and returned within a standard API envelope.
   *
   * @param id the {@link UUID} (version 7) representing the unique internal identifier of the city
   * @return a {@link Response} containing an {@link ApiEnvelope} with the retrieved city details
   */
  @GET
  @Path("{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    CityView cityView = readService.getViewById(id);
    CityResponse responseBody = CityPresenter.toResponse(cityView);
    return Response.ok(ApiEnvelope.ok(responseBody)).build();
  }

  /**
   * Retrieves the complete city list ordered alphabetically.
   *
   * <p>This endpoint returns the full city collection exposed by the geo module without applying
   * pagination or complex-search filters. Each city projection is mapped to a {@link CityResponse}
   * and wrapped in the standard API envelope used by the service.
   *
   * @return a {@link Response} containing an {@link ApiEnvelope} with the complete city list
   */
  @GET
  public Response list() {
    List<CityView> views = readService.listViews();
    List<CityResponse> responseBody =
        views.stream().map(CityPresenter::toResponse).collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(responseBody)).build();
  }

  /**
   * Executes paginated city search using complex-search contract.
   *
   * @param page the zero-based page index
   * @param size the requested page size; {@code 1} returns the full result set in a single page
   * @param request the optional complex-search filters
   * @return a {@link Response} containing an {@link ApiEnvelope} with the paginated city result
   */
  @POST
  @Path("/search")
  public Response search(
      @QueryParam("page") @DefaultValue("0") @Min(0) int page,
      @QueryParam("size") @DefaultValue("25") @Min(1) int size,
      @Valid CityComplexSearchRequest request) {
    String name = request != null ? request.name() : null;
    var result = readService.search(new PageQuery(page, size), new CityComplexSearchCriteria(name));

    var responseBody =
        new PageResponse<>(
            result.content().stream().map(CityPresenter::toResponse).toList(),
            result.page(),
            result.size(),
            result.totalElements(),
            result.totalPages());

    return Response.ok(ApiEnvelope.ok(responseBody)).build();
  }
}
