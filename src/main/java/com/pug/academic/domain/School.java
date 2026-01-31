package com.pug.academic.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.util.UUID;

/**
 * School entity aggregate.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class School extends DomainError {
  UUID id;
  String name;

  @Builder(toBuilder = true)
  private School(UUID id, String name) {
    this.id = id;
    this.name = name;
  }

  /**
   * Factory for new schools.
   *
   * @param name the name of the school
   * @return the created school (may contain errors)
   */
  public static School factory(String name) {
    String trimmedName = StringUtils.trim(name);
    School school = School.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .name(trimmedName)
            .build();

    school.collectValidationProblems();
    return school;
  }

  /**
   * Behavior: change the school name.
   *
   * @param newName new name for the school
   * @return new school with updated name
   */
  public School changeName(String newName) {
    String trimmedName = StringUtils.trim(newName);
    if (this.name.equals(trimmedName)) {
      return this;
    }
    School updatedSchool = this.toBuilder().name(trimmedName).build();
    updatedSchool.collectValidationProblems();
    return updatedSchool;
  }

  /**
   * Collects all validation problems for the School instance.
   */
  private void collectValidationProblems() {
    if (id == null) {
      addError(new AppValidationException.Problem(AcademicErrorCodes.INVALID_ID_BLANK));
    }

    if (StringUtils.isEmpty(name)) {
      addError(new AppValidationException.Problem(AcademicErrorCodes.INVALID_SCHOOL_NAME_BLANK));
    } else if (name.length() > 100) {
      addError(new AppValidationException.Problem(AcademicErrorCodes.INVALID_SCHOOL_NAME_LENGTH));
    }
  }
}