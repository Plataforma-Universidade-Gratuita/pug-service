package com.pug.academic.infra;

import com.pug.academic.domain.Student;
import com.pug.academic.domain.vos.AcademicRegistration;
import com.pug.academic.domain.vos.CounterpartHours;
import com.pug.academic.domain.vos.Period;
import com.pug.academic.infra.persistence.StudentEntity;
import com.pug.shared.exceptions.AppValidationException;

/**
 * Mapper for Student domain object and StudentEntity persistence object.
 */
public final class StudentMapper {
  /**
   * Private constructor to prevent instantiation.
   */
  private StudentMapper() {
  }

  /**
   * Maps a StudentEntity to a Student domain object.
   *
   * @param e the StudentEntity to map
   * @return the mapped Student domain object
   * @throws AppValidationException if data in the entity is invalid.
   */
  public static Student toDomain(StudentEntity e) throws AppValidationException {
    if (e == null) {
      return null;
    }
    return Student.builder()
            .accountId(e.getAccountId())
            .academicRegistration(new AcademicRegistration(e.getAcademicRegistration()))
            .campus(e.getCampus())
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
    return StudentEntity.builder()
            .accountId(d.getAccountId())
            .academicRegistration(d.getAcademicRegistration().toString())
            .campus(d.getCampus())
            .courseId(d.getCourseId())
            .requiredHours(d.getCounterpartHours().requiredHours())
            .completedHours(d.getCounterpartHours().completedHours())
            .startDate(d.getPeriod().startDate())
            .dueDate(d.getPeriod().dueDate())
            .build();
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
    e.setAcademicRegistration(d.getAcademicRegistration().toString());
    e.setCampus(d.getCampus());
    e.setCourseId(d.getCourseId());
    e.setRequiredHours(d.getCounterpartHours().requiredHours());
    e.setCompletedHours(d.getCounterpartHours().completedHours());
    e.setStartDate(d.getPeriod().startDate());
    e.setDueDate(d.getPeriod().dueDate());
  }
}