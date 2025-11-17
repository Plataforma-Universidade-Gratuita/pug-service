package com.pug.academic.domain.enums;

import lombok.Getter;

/**
 * Enum representing the different campus locations.
 */
@Getter
public enum Campi {
  JARAGUA("JARAGUÁ DO SUL"),
  JOINVILLE("JOINVILLE");

  private final String description;

  Campi(String description) {
    this.description = description;
  }
}
