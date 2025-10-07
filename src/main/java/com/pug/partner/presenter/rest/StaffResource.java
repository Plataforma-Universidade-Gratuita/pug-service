package com.pug.partner.presenter.rest;

import com.pug.partner.presenter.rest.dto.RegisterStaffRequest;
import com.pug.partner.presenter.rest.dto.RegisterStaffResponse;
import com.pug.partner.usecase.staff.create.RegisterStaffCommand;
import com.pug.partner.usecase.staff.create.RegisterStaffHandler;
import com.pug.partner.usecase.staff.read.ReadStaffByUserRoleIdQuery;
import com.pug.partner.usecase.staff.read.ReadStaffHandler;
import com.pug.shared.dtos.ApiResponse;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.UUID;

@Path("/partners/staff")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StaffResource {

  @Inject RegisterStaffHandler createHandler;
  @Inject
  ReadStaffHandler retrieveHandler;

  @POST
  public Response create(RegisterStaffRequest body, @Context UriInfo uri) {
    var out = createHandler.handle(new RegisterStaffCommand(body.userRoleId(), body.entityId()));
    UUID id = out.getId();
    URI location = uri.getAbsolutePathBuilder().path(id.toString()).build();
    return Response.created(location)
        .entity(ApiResponse.created(new RegisterStaffResponse(id)))
        .build();
  }

  @GET
  public Response getByUserRoleId(@QueryParam("userRoleId") @NotBlank String userRoleId) {
    var s = retrieveHandler.handle(new ReadStaffByUserRoleIdQuery(UUID.fromString(userRoleId)));
    return Response.ok(
            com.pug.shared.dtos.ApiResponse.ok(
                com.pug.partner.presenter.rest.dto.StaffResponse.from(s)))
        .build();
  }
}
