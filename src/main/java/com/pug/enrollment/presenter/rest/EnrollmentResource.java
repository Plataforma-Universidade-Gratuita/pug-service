package com.pug.enrollment.presenter.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/enrollments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EnrollmentResource {
  @GET
  public String list() {
    return "[]";
  }

  @POST
  public String create(String body) {
    return body;
  }
}
