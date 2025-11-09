package com.pug.academic.domain;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** Domain entity representing a Course. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Course {
  private final UUID id;
  private final String name;
  private final School school;

  private void validate() {}

  /** Builder class for constructing Course instances with validation. */
  public static class CourseBuilder {
    /**
     * Builds the Course instance and performs validation.
     *
     * @return the validated Course instance.
     */
    public Course build() {
      Course c = new Course(id, name, school);
      c.validate();
      return c;
    }
  }
}
