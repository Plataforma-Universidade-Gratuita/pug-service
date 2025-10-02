package com.pug.identity.presenter.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
@Path("/identity/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
  @GET public String list() { return "[]"; }
  @POST public String create(String body) { return body; }
}
