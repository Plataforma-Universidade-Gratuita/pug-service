package com.pug.identity.presenter.rest;

import com.pug.identity.presenter.rest.dto.CreateUserRequest;
import com.pug.identity.presenter.rest.dto.CreateUserResponse;
import com.pug.identity.presenter.rest.dto.UpdateUserRequest;
import com.pug.identity.presenter.rest.dto.UserResponse;
import com.pug.identity.usecase.user.create.CreateUserCommand;
import com.pug.identity.usecase.user.create.CreateUserHandler;
import com.pug.identity.usecase.user.get.byCpf.GetUserByCpfHandler;
import com.pug.identity.usecase.user.get.byCpf.GetUserByCpfQuery;
import com.pug.identity.usecase.user.get.byId.GetUserByIdHandler;
import com.pug.identity.usecase.user.get.byId.GetUserByIdQuery;
import com.pug.identity.usecase.user.update.UpdateUserCommand;
import com.pug.identity.usecase.user.update.UpdateUserHandler;
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
  @Inject CreateUserHandler createUser;
  @Inject UpdateUserHandler updateUser;
  @Inject GetUserByIdHandler getById;
  @Inject GetUserByCpfHandler getByCpf;

  @POST
  public Response create(CreateUserRequest body, @Context UriInfo uri) {
    UUID id = createUser.handle(new CreateUserCommand(body.cpf(), body.name()));
    URI location = uri.getAbsolutePathBuilder().path(id.toString()).build();
    return Response.created(location)
        .entity(ApiResponse.created(new CreateUserResponse(id)))
        .build();
  }

  @PUT
  @Path("{id}")
  public Response update(@PathParam("id") UUID id, UpdateUserRequest body) {
    var u = updateUser.handle(new UpdateUserCommand(id, body.cpf(), body.name()));
    return Response.ok(ApiResponse.ok(UserResponse.from(u))).build();
  }

  @GET
  @Path("{id}")
  public Response get(@PathParam("id") UUID id) {
    var u = getById.handle(new GetUserByIdQuery(id));
    return Response.ok(ApiResponse.ok(UserResponse.from(u))).build();
  }

  @GET
  public Response getByCpf(@QueryParam("cpf") @NotBlank @CPF String cpf) {
    var u = getByCpf.handle(new GetUserByCpfQuery(cpf));
    return Response.ok(ApiResponse.ok(UserResponse.from(u))).build();
  }
}
