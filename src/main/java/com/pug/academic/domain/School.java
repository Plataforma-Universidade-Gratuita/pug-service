package com.pug.academic.domain;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.text.StringUtils;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** School aggregate root. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class School {
  private final UUID id;
  private final String name;

  /**
   * Factory for new schools.
   *
   * @param name school name.
   * @return new School.
   */
  public static School createNew(String name) {
    School s = new School(null, StringUtils.trim(name));
    s.validate();
    return s;
  }

  /**
   * Behavior: change name.
   *
   * @param newName new name.
   * @return new School with updated name.
   */
  public School changeName(String newName) {
    School s = this.toBuilder().name(StringUtils.trim(newName)).build();
    s.validate();
    return s;
  }

  /**
   * Validates the School aggregate.
   *
   * @throws AppValidationException if validation fails.
   */
  private void validate() {
    if (name == null || name.isBlank()) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_SCHOOL_NAME_BLANK);
    }
    if (name.length() > 100) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_SCHOOL_NAME_TOOLONG);
    }
  }

  /** Builder that trims and validates on build. */
  public static class SchoolBuilder {
    /**
     * Builds the School instance after trimming and validating.
     *
     * @return the built School instance.
     */
    public School build() {
      School s = new School(id, StringUtils.trim(name));
      s.validate();
      return s;
    }
  }
}
