package com.pug.academic.domain;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.utils.StringUtils;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** School entity aggregate. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class School {
  private final UUID id;
  private final String name;

  /**
   * Factory for new schools.
   *
   * <p>Behavior: create new School and validate its attributes
   *
   * @param name the name of the school
   * @return the created school
   */
  public static School createNew(String name) {
    School s = new School(null, StringUtils.trim(name));
    s.validate();
    return s;
  }

  /**
   * Behavior: change the school name.
   *
   * @param newName new name for the school
   * @return new school with updated name
   */
  public School changeName(String newName) {
    School s = this.toBuilder().name(StringUtils.trim(newName)).build();
    s.validate();
    return s;
  }

  /**
   * Validates the School aggregate.
   *
   * <p>Checks that the name is not blank and does not exceed length limits.
   *
   * @throws AppValidationException if validation fails.
   */
  private void validate() {
    if (StringUtils.isEmpty(name)) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_SCHOOL_NAME_BLANK);
    }
    if (name.length() > 100) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_SCHOOL_NAME_LENGTH);
    }
  }

  /**
   * Builder class for School.
   *
   * <p>Overrides the build method to include validation.
   */
  public static class SchoolBuilder {
    /**
     * Builds the School instance after trimming and validating.
     *
     * @return the built School instance
     */
    public School build() {
      School s = new School(id, StringUtils.trim(name));
      s.validate();
      return s;
    }
  }
}
