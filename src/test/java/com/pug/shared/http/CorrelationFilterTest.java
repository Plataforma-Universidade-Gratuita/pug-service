package com.pug.shared.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import java.util.HashMap;
import java.util.Map;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.Test;

class CorrelationFilterTest {

  private static ContainerRequestContext reqWithHeader(
      String headerValue, Map<String, Object> props) {
    var req = mock(ContainerRequestContext.class);
    when(req.getHeaderString("X-Correlation-Id")).thenReturn(headerValue);
    // emulate property bag
    doAnswer(
            inv -> {
              props.put((String) inv.getArgument(0), inv.getArgument(1));
              return null;
            })
        .when(req)
        .setProperty(anyString(), any());
    when(req.getProperty(anyString())).thenAnswer(inv -> props.get(inv.getArgument(0)));
    return req;
  }

  private static ContainerResponseContext resWithHeaders(
      MultivaluedHashMap<String, Object> headers) {
    var res = mock(ContainerResponseContext.class);
    when(res.getHeaders()).thenReturn(headers);
    return res;
  }

  @Test
  void generatesCorrelationIdWhenMissingAndEchoesInResponse() {
    var filter = new CorrelationFilter();
    var props = new HashMap<String, Object>();
    var req = reqWithHeader(null, props);
    var headers = new MultivaluedHashMap<String, Object>();
    var res = resWithHeaders(headers);

    assertNull(MDC.get("X-Correlation-Id"));
    filter.filter(req);
    Object cidProp = props.get("X-Correlation-Id");
    assertNotNull(cidProp);
    assertNotNull(MDC.get("X-Correlation-Id"));

    filter.filter(req, res);
    assertEquals(cidProp, headers.getFirst("X-Correlation-Id"));
    assertNull(MDC.get("X-Correlation-Id"));
  }

  @Test
  void preservesProvidedCorrelationId() {
    var filter = new CorrelationFilter();
    var props = new HashMap<String, Object>();
    var req = reqWithHeader("abc-123", props);
    var headers = new MultivaluedHashMap<String, Object>();
    var res = resWithHeaders(headers);

    filter.filter(req);
    assertEquals("abc-123", props.get("X-Correlation-Id"));

    filter.filter(req, res);
    assertEquals("abc-123", headers.getFirst("X-Correlation-Id"));
  }
}
