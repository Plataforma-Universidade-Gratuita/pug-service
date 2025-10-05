package com.pug.geo.presenter.rest;

import com.pug.geo.presenter.rest.dto.CityResponse;
import com.pug.geo.usecase.get.RetrieveCitiesByPatternQuery;
import com.pug.geo.usecase.get.RetrieveCityByIbgeCodeQuery;
import com.pug.geo.usecase.get.RetrieveCityHandler;
import com.pug.shared.dtos.ApiResponse;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.stream.Collectors;

@Path("/cities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CityResource {
  @Inject RetrieveCityHandler handler;

  @GET
  public Response list(@QueryParam("q") String q, @QueryParam("limit") @Min(1) Integer limit) {
    var items =
        handler.handle(new RetrieveCitiesByPatternQuery(q, limit)).stream()
            .map(CityResponse::from)
            .collect(Collectors.toList());
    return Response.ok(ApiResponse.ok(items)).build();
  }

  @GET
  @Path("{ibgeCode}")
  public Response getByIbgeCode(@PathParam("ibgeCode") Integer ibgeCode) {
    var item = handler.handle(new RetrieveCityByIbgeCodeQuery(ibgeCode.toString()));
    return Response.ok(ApiResponse.ok(CityResponse.from(item))).build();
  }
}
