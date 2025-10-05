package com.pug.shared.dtos;

import java.time.Instant;

public record ApiResponse<T>(boolean success, T data, ApiError error, Instant timestamp) {
  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>(true, data, null, Instant.now());
  }

  public static <T> ApiResponse<T> created(T data) {
    return ok(data);
  }

  public static ApiResponse<Void> error(ApiError err) {
    return new ApiResponse<>(false, null, err, Instant.now());
  }
}
