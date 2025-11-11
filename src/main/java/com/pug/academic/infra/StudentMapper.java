package com.pug.academic.infra;

import com.pug.academic.domain.Student;
import com.pug.academic.domain.enums.Campi;
import com.pug.academic.domain.vos.AcademicRegistration;
import com.pug.academic.domain.vos.CounterpartHours;
import com.pug.academic.domain.vos.Period;
import com.pug.academic.infra.persistence.StudentEntity;

/** Mapper for Student domain object and StudentEntity persistence object. */
public final class StudentMapper {
  /** Private constructor to prevent instantiation. */
  private StudentMapper() {}

  /**
   * Maps a StudentEntity to a Student domain object.
   *
   * @param e the StudentEntity to map
   * @return the mapped Student domain object
   */
  public static Student toDomain(StudentEntity e) {
    if (e == null) {
      return null;
    }
    Campi campusEnum = decodeCampus(e.getCampus());
    return Student.builder()
        .userId(e.getUserId())
        .academicRegistration(new AcademicRegistration(e.getAcademicRegistration()))
        .campus(campusEnum)
        .courseId(e.getCourseId())
        .counterpartHours(new CounterpartHours(e.getRequiredHours(), e.getCompletedHours()))
        .period(new Period(e.getStartDate(), e.getDueDate()))
        .build();
  }

  /**
   * Maps a Student domain object to a StudentEntity.
   *
   * @param d the Student domain object to map
   * @return the mapped StudentEntity
   */
  public static StudentEntity toEntity(Student d) {
    if (d == null) {
      return null;
    }
    StudentEntity e = new StudentEntity();
    copy(d, e);
    return e;
  }

  /**
   * Copies properties from a Student domain object to a StudentEntity.
   *
   * @param d the Student domain object
   * @param e the StudentEntity to copy properties to
   */
  public static void copy(Student d, StudentEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setUserId(d.getUserId());
    e.setAcademicRegistration(d.getAcademicRegistration().toString());
    e.setCampus(encodeCampus(d.getCampus()));
    e.setCourseId(d.getCourseId());
    e.setRequiredHours(d.getCounterpartHours().requiredHours());
    e.setCompletedHours(d.getCounterpartHours().completedHours());
    e.setStartDate(d.getPeriod().startDate());
    e.setDueDate(d.getPeriod().dueDate());
  }

  /**
   * Encodes a Campi enum to its string representation.
   *
   * @param c the Campi enum
   * @return the string representation
   */
  private static String encodeCampus(Campi c) {
    return c.name();
  }

  /**
   * Decodes a string to a Campi enum.
   *
   * @param raw the string representation
   * @return the Campi enum
   */
  private static Campi decodeCampus(String raw) {
    if (raw == null) {
      return null;
    }
    try {
      return Campi.valueOf(raw);
    } catch (IllegalArgumentException ex) {
      for (Campi c : Campi.values()) {
        if (c.getDescription().equalsIgnoreCase(raw)) {
          return c;
        }
      }
      return null;
    }
  }
}
