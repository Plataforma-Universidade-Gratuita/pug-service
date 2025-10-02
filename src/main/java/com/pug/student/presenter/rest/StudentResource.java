package com.pug.student.presenter.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
@Path("/students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudentResource {
  @GET public String list() { return "[]"; }
  @POST public String create(String body) { return body; }
}
