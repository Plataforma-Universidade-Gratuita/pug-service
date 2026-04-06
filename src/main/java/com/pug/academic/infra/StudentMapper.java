package com.pug.academic.infra;

import com.pug.academic.domain.Student;
import com.pug.academic.domain.vos.AcademicRegistration;
import com.pug.academic.domain.vos.CounterpartHours;
import com.pug.academic.domain.vos.Period;
import com.pug.academic.infra.persistence.CourseEntity;
import com.pug.academic.infra.persistence.SchoolEntity;
import com.pug.academic.infra.persistence.StudentEntity;
import com.pug.academic.infra.read.dtos.StudentView;
import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.shared.domain.vos.AuditInfo;

/**
 * Stateless utility class responsible for mapping between Student boundary layers.
 *
 * <p>This mapper handles both the translation between pure domain and JPA entities, translating
 * complex nested value objects like {@link CounterpartHours} and {@link Period} to and from flat
 * database columns. It also handles the assembly of deeply nested CQRS read views that cross module
 * boundaries (Identity, Academic).
 */
public final class StudentMapper {
  /** Private constructor. */
  private StudentMapper() {}

  /**
   * Reconstitutes a pure Domain {@link Student} aggregate from a JPA {@link StudentEntity}.
   *
   * @param e the JPA persistence entity to convert
   * @return a fully constructed Domain {@link Student}, or {@code null} if the input entity is null
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
   * Translates a pure Domain {@link Student} aggregate into a newly instantiated JPA {@link
   * StudentEntity}.
   *
   * @param d the Domain aggregate to convert
   * @return a newly constructed JPA {@link StudentEntity}, or {@code null} if the input domain is
   *     null
   */
  public static StudentEntity toEntity(Student d) {
    if (d == null) {
      return null;
    }
    return StudentEntity.builder()
        .accountId(d.getAccountId())
        .academicRegistration(d.getAcademicRegistration().getValue())
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
   * Updates an existing, attached JPA {@link StudentEntity} with the current state of a Domain
   * {@link Student}.
   *
   * @param d the Domain aggregate containing the updated state
   * @param e the existing, attached JPA entity to update in-place
   */
  public static void copy(Student d, StudentEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setAcademicRegistration(d.getAcademicRegistration().getValue());
    e.setCampus(d.getCampus());
    e.setCourseId(d.getCourseId());
    e.setRequiredHours(d.getCounterpartHours().getRequiredHours());
    e.setConcluded(d.getCounterpartHours().getConcluded());
    e.setStartDate(d.getPeriod().getStartDate());
    e.setDueDate(d.getPeriod().getDueDate());
  }

  /**
   * Projects a deeply nested set of JPA entities across multiple domains into a flattened {@link
   * StudentView} DTO.
   *
   * <p>Used heavily by the CQRS query layer to construct fully resolved data structures that
   * encapsulate the student's identity (account and user references) and academic details (course
   * and school metadata) in a single, flat response ready for JSON serialization.
   *
   * @param s the JPA entity representing the student's academic records
   * @param acc the JPA entity representing the linked authentication account
   * @param c the JPA entity representing the enrolled course
   * @param sch the JPA entity representing the school offering the course
   * @return a fully populated, flattened {@link StudentView} DTO, or {@code null} if the student
   *     entity is null
   */
  public static StudentView toView(
      StudentEntity s, AccountEntity acc, CourseEntity c, SchoolEntity sch) {
    if (s == null) {
      return null;
    }

    return new StudentView(
        s.getAccountId(),
        acc != null ? acc.getUserId() : null,
        acc != null ? acc.getEmail() : null,
        acc != null ? acc.getAccountType() : null,
        acc != null ? acc.getActive() : null,
        s.getAcademicRegistration(),
        s.getCampus(),
        c != null ? c.getId() : null,
        c != null ? c.getName() : null,
        sch != null ? sch.getId() : null,
        sch != null ? sch.getName() : null,
        s.getRequiredHours(),
        s.getConcluded(),
        s.getStartDate(),
        s.getDueDate(),
        s.getCreatedAt(),
        s.getUpdatedAt());
  }
}
