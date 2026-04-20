package br.org.catolicasc.pug.academic.infra;

import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.academic.domain.vos.AcademicRegistration;
import br.org.catolicasc.pug.academic.domain.vos.CounterpartHours;
import br.org.catolicasc.pug.academic.domain.vos.Period;
import br.org.catolicasc.pug.academic.infra.persistence.StudentEntity;
import br.org.catolicasc.pug.academic.infra.read.dtos.StudentView;
import br.org.catolicasc.pug.shared.domain.vos.AuditInfo;

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
        .counterpartHours(
            CounterpartHours.factory(e.getRequiredHours(), e.getCompletedHours(), e.getConcluded()))
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
        .completedHours(d.getCounterpartHours().getCompletedHours())
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
    e.setCompletedHours(d.getCounterpartHours().getCompletedHours());
    e.setConcluded(d.getCounterpartHours().getConcluded());
    e.setStartDate(d.getPeriod().getStartDate());
    e.setDueDate(d.getPeriod().getDueDate());
    e.setCreatedAt(d.getAuditInfo().getCreatedAt());
    e.setUpdatedAt(d.getAuditInfo().getUpdatedAt());
  }

  /**
   * Projects a deeply nested set of JPA entities across multiple domains into a flattened {@link
   * StudentView} DTO.
   *
   * @param s the JPA entity representing the student's academic records
   * @return a fully populated, flattened {@link StudentView} DTO, or {@code null} if the student
   *     entity is null
   */
  public static StudentView toView(StudentEntity s) {
    if (s == null) {
      return null;
    }

    return new StudentView(
        s.getAccountId(),
        s.getAcademicRegistration(),
        s.getCampus(),
        s.getCourseId(),
        s.getRequiredHours(),
        s.getCompletedHours(),
        s.getConcluded(),
        s.getStartDate(),
        s.getDueDate(),
        s.getCreatedAt(),
        s.getUpdatedAt());
  }
}
