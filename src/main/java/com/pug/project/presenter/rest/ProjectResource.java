package com.pug.project.presenter.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource {
  @GET
  public String list() {
    return "[]";
  }

  @POST
  public String create(String body) {
    return body;
  }
}
