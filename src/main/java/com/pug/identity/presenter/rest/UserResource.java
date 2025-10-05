package com.pug.identity.presenter.rest;

import com.pug.identity.presenter.rest.dto.AttUserRequest;
import com.pug.identity.presenter.rest.dto.RegisterUserRequest;
import com.pug.identity.presenter.rest.dto.RegisterUserResponse;
import com.pug.identity.presenter.rest.dto.UserResponse;
import com.pug.identity.usecase.user.create.RegisterUserCommand;
import com.pug.identity.usecase.user.create.RegisterUserHandler;
import com.pug.identity.usecase.user.get.RetrieveUserByCpfQuery;
import com.pug.identity.usecase.user.get.RetrieveUserByIdQuery;
import com.pug.identity.usecase.user.get.RetrieveUserHandler;
import com.pug.identity.usecase.user.update.AttUserCommand;
import com.pug.identity.usecase.user.update.AttUserHandler;
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
import org.hibernate.validator.constraints.br.CPF;

@Path("/identity/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
  @Inject RegisterUserHandler createUser;
  @Inject AttUserHandler updateUser;
  @Inject RetrieveUserHandler handler;

  @POST
  public Response create(RegisterUserRequest body, @Context UriInfo uri) {
    UUID id = createUser.handle(new RegisterUserCommand(body.cpf(), body.name()));
    URI location = uri.getAbsolutePathBuilder().path(id.toString()).build();
    return Response.created(location)
        .entity(ApiResponse.created(new RegisterUserResponse(id)))
        .build();
  }

  @PUT
  @Path("{id}")
  public Response update(@PathParam("id") UUID id, AttUserRequest body) {
    var u = updateUser.handle(new AttUserCommand(id, body.cpf(), body.name()));
    return Response.ok(ApiResponse.ok(UserResponse.from(u))).build();
  }

  @GET
  @Path("{id}")
  public Response get(@PathParam("id") UUID id) {
    var u = handler.handle(new RetrieveUserByIdQuery(id));
    return Response.ok(ApiResponse.ok(UserResponse.from(u))).build();
  }

  @GET
  public Response getByCpf(@QueryParam("cpf") @NotBlank @CPF String cpf) {
    var u = handler.handle(new RetrieveUserByCpfQuery(cpf));
    return Response.ok(ApiResponse.ok(UserResponse.from(u))).build();
  }
}
