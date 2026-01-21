package com.pug.academic.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
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
   * @param name the name of the school
   * @return the created school
   * @throws AppValidationException if validation fails
   */
  public static School createNew(String name) {
    String trimmedName = StringUtils.trim(name);
    School school =
        School.builder().id(UuidCreator.getTimeOrderedEpoch()).name(trimmedName).build();

    List<AppValidationException.Problem> problems = school.collectValidationProblems();
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return school;
  }

  /**
   * Behavior: change the school name.
   *
   * @param newName new name for the school
   * @return new school with updated name
   * @throws AppValidationException if validation fails
   */
  public School changeName(String newName) {
    String trimmedName = StringUtils.trim(newName);
    School updatedSchool = this.toBuilder().name(trimmedName).build();

    List<AppValidationException.Problem> problems = updatedSchool.collectValidationProblems();
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return updatedSchool;
  }

  /**
   * Collects all validation problems for the School instance.
   *
   * @return A list of {@code AppValidationException.Problem}; an empty list otherwise.
   */
  private List<AppValidationException.Problem> collectValidationProblems() {
    List<AppValidationException.Problem> problems = new ArrayList<>();

    if (id == null) {
      problems.add(new AppValidationException.Problem(AcademicErrorCodes.INVALID_ID_BLANK, "id"));
    }
    if (StringUtils.isEmpty(name)) {
      problems.add(
          new AppValidationException.Problem(AcademicErrorCodes.INVALID_SCHOOL_NAME_BLANK, "name"));
    } else if (name.length() > 100) {
      problems.add(
          new AppValidationException.Problem(
              AcademicErrorCodes.INVALID_SCHOOL_NAME_LENGTH, "name"));
    }
    return problems;
  }
}
