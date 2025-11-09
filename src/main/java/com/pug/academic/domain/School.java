package com.pug.academic.domain;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** Domain entity representing a School. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class School {
  private final UUID id;
  private final String name;

  private void validate() {}

  /** Builder class for constructing School instances with validation. */
  public static class SchoolBuilder {
    /**
     * Builds the School instance and performs validation.
     *
     * @return the validated School instance.
     */
    public School build() {
      School s = new School(id, name);
      s.validate();
      return s;
    }
  }
}
