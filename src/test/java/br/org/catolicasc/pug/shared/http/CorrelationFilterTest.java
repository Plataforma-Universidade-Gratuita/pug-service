package br.org.catolicasc.pug.shared.http;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("CorrelationFilter Tests")
class CorrelationFilterTest {

    private CorrelationFilter filter;
    private ContainerRequestContext requestContext;
    private ContainerResponseContext responseContext;

    @BeforeEach
    void setup() {
        filter = new CorrelationFilter();
        requestContext = mock(ContainerRequestContext.class);
        responseContext = mock(ContainerResponseContext.class);
    }

    @AfterEach
    void tearDown() {
        MDC.remove("X-Correlation-Id");
    }

    @Nested
    @DisplayName("Method: filter (Request)")
    class RequestFilterTests {

        @Test
        @DisplayName("Should use existing correlation ID from header")
        void shouldReuseExistingId() {
            when(requestContext.getHeaderString("X-Correlation-Id")).thenReturn("existing-id-123");

            filter.filter(requestContext);

            verify(requestContext).setProperty("X-Correlation-Id", "existing-id-123");
            assertThat(MDC.get("X-Correlation-Id")).isEqualTo("existing-id-123");
        }

        @Test
        @DisplayName("Should generate new correlation ID if none provided")
        void shouldGenerateNewId() {
            when(requestContext.getHeaderString("X-Correlation-Id")).thenReturn(null);

            filter.filter(requestContext);

            // Verify a property was set with a value
            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            verify(requestContext).setProperty(eq("X-Correlation-Id"), captor.capture());

            assertThat(captor.getValue()).isNotBlank();
            assertThat(MDC.get("X-Correlation-Id")).isEqualTo(captor.getValue());
        }
    }

    @Nested
    @DisplayName("Method: filter (Response)")
    class ResponseFilterTests {

        @Test
        @DisplayName("Should add correlation ID to response headers and clear MDC")
        void shouldAddHeaderAndClearMdc() {
            String cid = "test-id-999";
            MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

            when(requestContext.getProperty("X-Correlation-Id")).thenReturn(cid);
            when(responseContext.getHeaders()).thenReturn(headers);

            filter.filter(requestContext, responseContext);

            assertThat(headers.get("X-Correlation-Id")).containsExactly(cid);
            assertThat(MDC.get("X-Correlation-Id")).isNull();
        }
    }
}