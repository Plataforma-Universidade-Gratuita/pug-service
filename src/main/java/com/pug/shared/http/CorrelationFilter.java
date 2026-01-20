package com.pug.shared.http;

import com.pug.shared.utils.StringUtils;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.MDC;

/**
 * A JAX-RS filter that manages correlation IDs for incoming requests and outgoing responses.
 * It extracts an existing correlation ID from the "X-Correlation-Id" header or generates a new one.
 * The ID is then stored in the request properties and the MDC (Mapped Diagnostic Context) for logging,
 * and finally added to the response header.
 */
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class CorrelationFilter implements ContainerRequestFilter, ContainerResponseFilter {
  private static final String HDR = "X-Correlation-Id";

  /**
   * Handles the incoming request to extract or generate a correlation ID.
   * If an "X-Correlation-Id" header is present, it is reused. Otherwise, a new UUID is generated.
   * The ID is stored in request properties and MDC for logging purposes.
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
    MDC.put(HDR, cid); // Using org.jboss.logging.MDC
  }

  /**
   * Handles the outgoing response to include the correlation ID in the headers.
   * The correlation ID is retrieved from the request properties and added to the response header.
   * The ID is then removed from the MDC.
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
    MDC.remove(HDR); // Using org.jboss.logging.MDC
  }
}