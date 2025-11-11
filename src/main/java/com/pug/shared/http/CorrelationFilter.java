package com.pug.shared.http;

import com.pug.shared.utils.StringUtils;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

/** A JAX-RS filter that manages correlation IDs for incoming requests and outgoing responses. */
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class CorrelationFilter implements ContainerRequestFilter, ContainerResponseFilter {
  private static final String HDR = "X-Correlation-Id";

  /**
   * Handles the incoming request to extract or generate a correlation ID.
   *
   * @param req The container request context.
   */
  @Override
  public void filter(ContainerRequestContext req) {
    String cid = req.getHeaderString(HDR);
    if (StringUtils.isEmpty(cid)) {
      cid = java.util.UUID.randomUUID().toString();
    }
    req.setProperty(HDR, cid);
    org.jboss.logging.MDC.put(HDR, cid);
  }

  /**
   * Handles the outgoing response to include the correlation ID in the headers.
   *
   * @param req The container request context.
   * @param res The container response context.
   */
  @Override
  public void filter(ContainerRequestContext req, ContainerResponseContext res) {
    String cid = (String) req.getProperty(HDR);
    if (cid != null) {
      res.getHeaders().putSingle(HDR, cid);
    }
    org.jboss.logging.MDC.remove(HDR);
  }
}
