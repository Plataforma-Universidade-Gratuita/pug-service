package com.pug.geo.presenter;

import com.pug.geo.infra.read.dtos.CityView;
import com.pug.geo.presenter.dtos.CityResponse;
import com.pug.geo.presenter.mappers.CityPresenter;
import com.pug.geo.service.CityReadService;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.utils.StringUtils;
import com.pug.shared.validation.UuidV7;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
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
 * <p>This controller exposes endpoints to retrieve city details by their unique identifiers, IBGE
 * codes, or through text-based search queries. It acts as the presentation layer for the
 * geographical module, delegating data retrieval to the {@link CityReadService} and ensuring that
 * all responses are consistently wrapped in a standardized {@link ApiEnvelope}.
 */
@ApplicationScoped
@Path("/geo/cities")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CityReadOnlyResource {

  @Inject CityReadService readService;

  /**
   * Retrieves the details of a specific city based on its unique identifier.
   *
   * <p>This endpoint looks up a single geographical city using its UUID version 7. If the city is
   * found, it is mapped to a {@link CityResponse} and returned within a standard API envelope.
   *
   * @param id the {@link UUID} (version 7) representing the unique internal identifier of the city.
   * @return a {@link Response} containing an {@link ApiEnvelope} with the retrieved city details.
   */
  @GET
  @Path("{id}")
  public Response get(@PathParam("id") @UuidV7 UUID id) {
    CityView cityView = readService.getViewById(id);
    CityResponse responseBody = CityPresenter.toResponse(cityView);
    return Response.ok(ApiEnvelope.ok(responseBody)).build();
  }

  /**
   * Retrieves the details of a specific city based on its unique IBGE code.
   *
   * <p>The IBGE code serves as a natural key for Brazilian municipalities. This endpoint queries
   * the persistence layer to find the exact match for the provided 7-digit geographic code.
   *
   * @param ibgeCode a {@link String} representing the 7-digit IBGE code of the city.
   * @return a {@link Response} containing an {@link ApiEnvelope} with the retrieved city details.
   */
  @GET
  @Path("by-ibge/{ibgeCode}")
  public Response getByIbgeCode(@PathParam("ibgeCode") String ibgeCode) {
    CityView cityView = readService.getViewByIbgeCode(ibgeCode);
    CityResponse responseBody = CityPresenter.toResponse(cityView);
    return Response.ok(ApiEnvelope.ok(responseBody)).build();
  }

  /**
   * Retrieves a comprehensive list of cities, optionally filtered by a text-based search query.
   *
   * <p>If a search query ({@code q}) is provided, this endpoint delegates to the underlying search
   * service (e.g., Hibernate Search) to perform a full-text, fuzzy, or normalized search against
   * the city names. If the query is omitted or empty, it bypasses the search index and returns a
   * complete list of all available cities, ordered alphabetically.
   *
   * @param q an optional {@link String} representing the search query used to filter cities by
   *     name.
   * @return a {@link Response} containing an {@link ApiEnvelope} with a collection of matching
   *     cities.
   */
  @GET
  public Response list(@QueryParam("q") String q) {

    List<CityView> views;

    if (StringUtils.isNotEmpty(q)) {
      views = readService.search(q);
    } else {
      views = readService.listViews();
    }

    List<CityResponse> responseBody =
        views.stream().map(CityPresenter::toResponse).collect(Collectors.toList());

    return Response.ok(ApiEnvelope.ok(responseBody)).build();
  }
}
