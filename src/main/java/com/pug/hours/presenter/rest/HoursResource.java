package com.pug.hours.presenter.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
@Path("/hours")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HoursResource {
  @GET public String get() { return "{}"; }
  @POST public String define(String body) { return body; }
}
