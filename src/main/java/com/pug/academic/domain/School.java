package com.pug.academic.domain;

import static com.pug.academic.domain.AcademicErrorCodes.ACADEMIC_SCHOOL_NAME_REQUIRED;
import static com.pug.academic.domain.AcademicErrorCodes.ACADEMIC_SCHOOL_NAME_TOO_LONG;

import com.pug.shared.domain.exceptions.AppValidationException;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public final class School {
  private final UUID id;
  private final String name;

  private void validate() {
    if (name == null || name.isBlank())
      throw new AppValidationException(ACADEMIC_SCHOOL_NAME_REQUIRED);
    if (name.length() > 100) throw new AppValidationException(ACADEMIC_SCHOOL_NAME_TOO_LONG);
  }

  public static class SchoolBuilder {
    public School build() {
      School s = new School(id, name);
      s.validate();
      return s;
    }
  }
}
