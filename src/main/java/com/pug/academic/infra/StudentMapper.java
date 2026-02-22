package com.pug.academic.infra;

import com.pug.academic.domain.Student;
import com.pug.academic.domain.vos.AcademicRegistration;
import com.pug.academic.domain.vos.CounterpartHours;
import com.pug.academic.domain.vos.Period;
import com.pug.academic.infra.persistence.CourseEntity;
import com.pug.academic.infra.persistence.SchoolEntity;
import com.pug.academic.infra.persistence.StudentEntity;
import com.pug.academic.infra.read.dtos.CourseView;
import com.pug.academic.infra.read.dtos.SchoolView;
import com.pug.academic.infra.read.dtos.StudentView;
import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.identity.infra.persistence.UserEntity;
import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.identity.infra.read.dtos.UserView;
import com.pug.shared.domain.vos.AuditInfo;

/** Mapper for Student and StudentEntity. */
public final class StudentMapper {
  /** Private constructor. */
  private StudentMapper() {}

  /**
   * Convert StudentEntity to Student domain object.
   *
   * @param e the StudentEntity
   * @return the Student domain object
   */
  public static Student toDomain(StudentEntity e) {
    if (e == null) {
      return null;
    }
    return Student.builder()
        .accountId(e.getAccountId())
        .academicRegistration(AcademicRegistration.factory(e.getAcademicRegistration()))
        .campus(e.getCampus())
        .courseId(e.getCourseId())
        .counterpartHours(CounterpartHours.factory(e.getRequiredHours(), e.getConcluded()))
        .period(Period.factory(e.getStartDate(), e.getDueDate()))
        .auditInfo(AuditInfo.factory(e.getCreatedAt(), e.getUpdatedAt()))
        .build();
  }

  /**
   * Convert Student domain object to StudentEntity.
   *
   * @param d the Student domain object
   * @return the StudentEntity
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
        .requiredHours(d.getCounterpartHours().getRequiredHours())
        .concluded(d.getCounterpartHours().getConcluded())
        .startDate(d.getPeriod().getStartDate())
        .dueDate(d.getPeriod().getDueDate())
        .createdAt(d.getAuditInfo().getCreatedAt())
        .updatedAt(d.getAuditInfo().getUpdatedAt())
        .build();
  }

  /**
   * Copy properties from Student domain object to StudentEntity.
   *
   * @param d the Student domain object
   * @param e the StudentEntity
   */
  public static void copy(Student d, StudentEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setAcademicRegistration(d.getAcademicRegistration().toString());
    e.setCampus(d.getCampus());
    e.setCourseId(d.getCourseId());
    e.setRequiredHours(d.getCounterpartHours().getRequiredHours());
    e.setConcluded(d.getCounterpartHours().getConcluded());
    e.setStartDate(d.getPeriod().getStartDate());
    e.setDueDate(d.getPeriod().getDueDate());
  }

  /** Convert entities to StudentView. */
  public static StudentView toView(
      StudentEntity s, AccountEntity acc, UserEntity u, CourseEntity c, SchoolEntity sch) {
    if (s == null) {
      return null;
    }

    UserView userView =
        (u != null) ? new UserView(u.getId(), u.getCpf(), u.getName(), u.getCreatedAt(), u.getUpdatedAt()) : null;
    AccountView accountView =
        (acc != null)
            ? new AccountView(
                acc.getId(), userView, acc.getEmail(), acc.getAccountType(), acc.getCreatedAt(), acc.getUpdatedAt())
            : null;

    SchoolView schoolView = (sch != null) ? new SchoolView(sch.getId(), sch.getName(), s.getCreatedAt(), s.getUpdatedAt()) : null;
    CourseView courseView = (c != null) ? new CourseView(c.getId(), c.getName(), schoolView, c.getCreatedAt(), c.getUpdatedAt()) : null;

    return new StudentView(
        accountView,
        s.getAcademicRegistration(),
        s.getCampus().toString(),
        courseView,
        s.getRequiredHours(),
        s.getConcluded(),
        s.getStartDate(),
        s.getDueDate(),
        s.getCreatedAt(),
        s.getUpdatedAt());
  }
}
