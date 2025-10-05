package com.pug.shared.http;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class CorrelationFilter implements ContainerRequestFilter, ContainerResponseFilter {
  private static final String HDR = "X-Correlation-Id";

  @Override
  public void filter(ContainerRequestContext req) {
    String cid = req.getHeaderString(HDR);
    if (cid == null || cid.isBlank()) cid = java.util.UUID.randomUUID().toString();
    req.setProperty(HDR, cid);
    org.jboss.logging.MDC.put(HDR, cid);
  }

  @Override
  public void filter(ContainerRequestContext req, ContainerResponseContext res) {
    String cid = (String) req.getProperty(HDR);
    if (cid != null) res.getHeaders().putSingle(HDR, cid);
    org.jboss.logging.MDC.remove(HDR);
  }
}
