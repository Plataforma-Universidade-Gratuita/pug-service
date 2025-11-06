package com.pug.shared.presenter.rest;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * API Error Record.
 *
 * @param code
 * @param message
 * @param details
 */
public record ApiError(String code, String message, Map<String, Object> details) {
  /**
   * Constructor.
   *
   * @param code    Error code.
   * @param message Error message.
   * @param details Error details.
   */
  public ApiError(String code, String message, Map<String, Object> details) {
    this.code = code;
    this.message = message;
    this.details = (details == null) ? Map.of() : new LinkedHashMap<>(details);
  }

  /**
   * Get details as a new map.
   *
   * @return details map.
   */
  @Override
  public Map<String, Object> details() {
    return new LinkedHashMap<>(details);
  }

  /**
   * Factory method.
   *
   * @param code    Error code.
   * @param message Error message.
   * @param details Error details.
   * @return ApiError instance.
   */
  public static ApiError of(String code, String message, Map<String, Object> details) {
    return new ApiError(code, message, details);
  }
}
