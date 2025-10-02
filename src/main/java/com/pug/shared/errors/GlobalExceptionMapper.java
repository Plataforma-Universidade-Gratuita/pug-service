package com.pug.shared.errors;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
  public Response toResponse(Throwable ex) {
    return Response.serverError().entity(ex.getMessage()).build();
  }
}
