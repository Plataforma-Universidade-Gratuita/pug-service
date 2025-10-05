package com.pug.partner.presenter.rest;

import com.pug.partner.presenter.rest.dto.AttPartnerEntityRequest;
import com.pug.partner.presenter.rest.dto.PartnerEntityResponse;
import com.pug.partner.presenter.rest.dto.RegisterPartnerEntityRequest;
import com.pug.partner.presenter.rest.dto.RegisterPartnerEntityResponse;
import com.pug.partner.usecase.entity.create.RegisterPartnerEntityCommand;
import com.pug.partner.usecase.entity.create.RegisterPartnerEntityHandler;
import com.pug.partner.usecase.entity.get.RetrievePartnerEntityByCnpjCommand;
import com.pug.partner.usecase.entity.get.RetrievePartnerEntityByIdCommand;
import com.pug.partner.usecase.entity.get.RetrievePartnerEntityHandler;
import com.pug.partner.usecase.entity.update.AttPartnerEntityCommand;
import com.pug.partner.usecase.entity.update.AttPartnerEntityHandler;
import com.pug.shared.dtos.ApiResponse;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.Consumes;
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
import java.util.UUID;
import org.hibernate.validator.constraints.br.CNPJ;

@Path("/partners/entities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PartnerEntityResource {

  @Inject RegisterPartnerEntityHandler createHandler;
  @Inject AttPartnerEntityHandler updateHandler;
  @Inject RetrievePartnerEntityHandler retrieveHandler;

  @POST
  public Response create(RegisterPartnerEntityRequest body, @Context UriInfo uri) {
    UUID id =
        createHandler.handle(
            new RegisterPartnerEntityCommand(
                body.cnpj(), body.name(), body.cityId(), body.address()));
    URI location = uri.getAbsolutePathBuilder().path(id.toString()).build();
    return Response.created(location)
        .entity(ApiResponse.created(new RegisterPartnerEntityResponse(id)))
        .build();
  }

  @PUT
  @Path("{id}")
  public Response update(@PathParam("id") UUID id, AttPartnerEntityRequest body) {
    var e =
        updateHandler.handle(
            new AttPartnerEntityCommand(
                id, body.cnpj(), body.name(), body.cityId(), body.address()));
    return Response.ok(ApiResponse.ok(PartnerEntityResponse.from(e))).build();
  }

  @GET
  @Path("{id}")
  public Response getById(@PathParam("id") UUID id) {
    var e = retrieveHandler.handle(new RetrievePartnerEntityByIdCommand(id));
    return Response.ok(ApiResponse.ok(PartnerEntityResponse.from(e))).build();
  }

  @GET
  public Response getByCnpj(@QueryParam("cnpj") @NotBlank @CNPJ String cnpj) {
    var e = retrieveHandler.handle(new RetrievePartnerEntityByCnpjCommand(cnpj));
    return Response.ok(ApiResponse.ok(PartnerEntityResponse.from(e))).build();
  }
}
