package com.pug.partner.presenter.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
@Path("/entities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EntityResource {
  @GET public String list() { return "[]"; }
  @POST public String create(String body) { return body; }
}
