package com.pug.shared.presenter.rest;

import java.time.Instant;
import java.util.Map;

public record ApiEnvelope<T>(boolean success, T data, ApiError error, Instant timestamp) {

  public static <T> ApiEnvelope<T> ok(T data) {
    return new ApiEnvelope<>(true, data, null, Instant.now());
  }

  public static <T> ApiEnvelope<T> created(T data) {
    return ok(data);
  }

  public static ApiEnvelope<Void> error(ApiError err) {
    return new ApiEnvelope<>(false, null, err, Instant.now());
  }

  public static ApiEnvelope<Void> error(String code, String message) {
    return error(ApiError.of(code, message, Map.of()));
  }

  public static ApiEnvelope<Void> error(String code, String message, Map<String, Object> details) {
    return error(ApiError.of(code, message, details));
  }
}
