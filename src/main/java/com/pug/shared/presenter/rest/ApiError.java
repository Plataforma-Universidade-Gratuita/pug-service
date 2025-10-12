package com.pug.shared.presenter.rest;

import java.util.LinkedHashMap;
import java.util.Map;

public record ApiError(String code, String message, Map<String, Object> details) {
  public ApiError(String code, String message, Map<String, Object> details) {
    this.code = code;
    this.message = message;
    this.details = (details == null) ? Map.of() : new LinkedHashMap<>(details);
  }

  @Override
  public Map<String, Object> details() {
    return new LinkedHashMap<>(details);
  }

  public static ApiError of(String code, String message, Map<String, Object> details) {
    return new ApiError(code, message, details);
  }
}
