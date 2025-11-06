package com.pug.shared.presenter.rest;

import java.time.Instant;
import java.util.Map;

/**
 * API response envelope.
 *
 * @param success
 * @param data
 * @param error
 * @param timestamp
 * @param <T>
 */
public record ApiEnvelope<T>(boolean success, T data, ApiError error, Instant timestamp) {

  /**
   * Success response.
   *
   * @param data Data.
   * @param <T>  Type of data.
   * @return ApiEnvelope.
   */
  public static <T> ApiEnvelope<T> ok(T data) {
    return new ApiEnvelope<>(true, data, null, Instant.now());
  }

  /**
   * Created response (201).
   *
   * @param data Data.
   * @param <T>  Type of data.
   * @return ApiEnvelope.
   */
  public static <T> ApiEnvelope<T> created(T data) {
    return ok(data);
  }

  /**
   * Error response.
   *
   * @param err Error.
   * @return ApiEnvelope.
   */
  public static ApiEnvelope<Void> error(ApiError err) {
    return new ApiEnvelope<>(false, null, err, Instant.now());
  }

  /**
   * Error response.
   *
   * @param code    Error code.
   * @param message Error message.
   * @return ApiEnvelope.
   */
  public static ApiEnvelope<Void> error(String code, String message) {
    return error(ApiError.of(code, message, Map.of()));
  }

  /**
   * Error response.
   *
   * @param code    Error code.
   * @param message Error message.
   * @param details Error details.
   * @return ApiEnvelope.
   */
  public static ApiEnvelope<Void> error(String code, String message, Map<String, Object> details) {
    return error(ApiError.of(code, message, details));
  }
}
