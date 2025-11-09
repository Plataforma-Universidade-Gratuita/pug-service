package com.pug.academic.domain;

import com.pug.academic.domain.enums.Campi;
import com.pug.academic.domain.vos.AcademicRegistration;
import com.pug.academic.domain.vos.CounterpartHours;
import com.pug.academic.domain.vos.Period;
import com.pug.identity.domain.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** Domain entity representing a Student. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Student {
  private final User user;
  private final AcademicRegistration academicRegistration;
  private final Campi campus;
  private final Course course;
  private final CounterpartHours counterpartHours;
  private final Period period;

  private void validate() {}

  /** Builder class for constructing Student instances with validation. */
  public static class StudentBuilder {
    /**
     * Builds the Student instance and performs validation.
     *
     * @return the validated Student instance.
     */
    public Student build() {
      Student s = new Student(user, academicRegistration, campus, course, counterpartHours, period);
      s.validate();
      return s;
    }
  }
}
