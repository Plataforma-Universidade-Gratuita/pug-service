package com.pug.shared.presenter.rest;

import java.time.Instant;

/**
 * API response envelope.
 *
 * @param success   indicates if the request was successful
 * @param data      the response data when success is true
 * @param error     the error details when success is false
 * @param timestamp the time the response was created
 * @param <T>       the type of the response data
 */
public record ApiEnvelope<T>(boolean success, T data, ApiError error, Instant timestamp) {

  /**
   * Success response (200).
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
}
