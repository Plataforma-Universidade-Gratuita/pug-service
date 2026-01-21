package com.pug.academic.domain.enums;

import lombok.Getter;

/**
 * Enum representing the different campus locations.
 */
@Getter
public enum Campi {
  JARAGUA("academic.campus.jaragua"),
  JOINVILLE("academic.campus.joinville");

  private final String bundleKey;

  /**
   * Constructor for Campi enum.
   *
   * @param bundleKey The internationalization resource key for the campus description.
   */
  Campi(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}