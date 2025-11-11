package com.pug.projects.domain.enums;

import com.pug.shared.domain.enums.GenericErrorCodes;
import lombok.Getter;

/**
 * Enum representing error codes specific to the projects domain.
 *
 * <p>Each error code is associated with a specific validation failure scenario and has a {@code
 * bundleKey} that results into a located error message
 */
@Getter
public enum ProjectsErrorCodes implements GenericErrorCodes {
  PLACEHOLDER("partner.error.placeholder");

  private final String bundleKey;

  ProjectsErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
