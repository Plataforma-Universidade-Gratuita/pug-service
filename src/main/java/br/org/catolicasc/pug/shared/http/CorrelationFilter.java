/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.shared.http;

import br.org.catolicasc.pug.shared.utils.StringUtils;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.MDC;

/**
 * A JAX-RS filter that manages correlation IDs for incoming requests and outgoing responses. It
 * extracts an existing correlation ID from the "X-Correlation-Id" header or generates a new one.
 * The ID is then stored in the request properties and the MDC (Mapped Diagnostic Context) for
 * logging, and finally added to the response header.
 */
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class CorrelationFilter implements ContainerRequestFilter, ContainerResponseFilter {
  private static final String HDR = "X-Correlation-Id";

  /** {@inheritDoc} */
  @Override
  public void filter(ContainerRequestContext req) {
    String cid = req.getHeaderString(HDR);
    if (StringUtils.isEmpty(cid)) {
      cid = UuidCreator.getTimeOrderedEpoch().toString();
    }
    req.setProperty(HDR, cid);
    MDC.put(HDR, cid);
  }

  /** {@inheritDoc} */
  @Override
  public void filter(ContainerRequestContext req, ContainerResponseContext res) {
    String cid = (String) req.getProperty(HDR);
    if (cid != null) {
      res.getHeaders().putSingle(HDR, cid);
    }
    MDC.remove(HDR);
  }
}
