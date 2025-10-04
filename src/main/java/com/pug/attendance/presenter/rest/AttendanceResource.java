package com.pug.attendance.presenter.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/attendances")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AttendanceResource {
  @GET
  public String list() {
    return "[]";
  }

  @POST
  public String create(String body) {
    return body;
  }
}
