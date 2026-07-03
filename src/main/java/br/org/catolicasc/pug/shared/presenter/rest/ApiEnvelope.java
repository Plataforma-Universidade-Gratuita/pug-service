/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.shared.presenter.rest;

import java.time.Instant;
import org.jboss.logging.MDC;

/**
 * Standardized generic envelope for all REST API responses.
 *
 * <p>This envelope ensures a consistent JSON structure across the entire application, whether a
 * request succeeds or fails. It provides clients with a predictable schema containing a success
 * flag, the primary payload (or error details), a timestamp, and a correlation ID for distributed
 * tracing and debugging.
 *
 * @param <T> the type of the successful response data payload
 * @param success {@code true} if the request was successfully processed (e.g., HTTP 2xx); {@code
 *     false} if an error occurred (e.g., HTTP 4xx, 5xx)
 * @param data the response payload when {@code success} is {@code true}; {@code null} otherwise
 * @param error the {@link ApiError} details when {@code success} is {@code false}; {@code null}
 *     otherwise
 * @param timestamp the exact UTC time the response envelope was generated
 * @param correlationId the unique identifier used to trace the request across logs and external
 *     systems
 */
public record ApiEnvelope<T>(
    boolean success, T data, ApiError error, Instant timestamp, String correlationId) {

  /**
   * The key used to store and retrieve the correlation ID within the Mapped Diagnostic Context
   * (MDC).
   */
  private static final String CID_KEY = "X-Correlation-Id";

  private static String getCorrelationId() {
    Object cid = MDC.get(CID_KEY);
    return cid != null ? cid.toString() : null;
  }

  /**
   * Creates a successful API envelope (typically mapped to HTTP 200 OK).
   *
   * @param data the primary payload to return to the client
   * @param <T> the type of the payload
   * @return a fully populated {@link ApiEnvelope} indicating success
   */
  public static <T> ApiEnvelope<T> ok(T data) {
    return new ApiEnvelope<>(true, data, null, Instant.now(), getCorrelationId());
  }

  /**
   * Creates a successful API envelope representing resource creation (typically mapped to HTTP 201
   * Created).
   *
   * @param data the newly created resource payload to return to the client
   * @param <T> the type of the payload
   * @return a fully populated {@link ApiEnvelope} indicating successful creation
   */
  public static <T> ApiEnvelope<T> created(T data) {
    return ok(data);
  }

  /**
   * Creates an error API envelope (typically mapped to HTTP 4xx or 5xx statuses).
   *
   * @param err the structured {@link ApiError} containing the failure code, localized message, and
   *     details
   * @return a fully populated {@link ApiEnvelope} indicating failure, with a {@code Void} data
   *     payload
   */
  public static ApiEnvelope<Void> error(ApiError err) {
    return new ApiEnvelope<>(false, null, err, Instant.now(), getCorrelationId());
  }
}
