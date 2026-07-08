package br.org.catolicasc.pug.academic.infra;

import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.domain.vos.AcademicRegistration;
import br.org.catolicasc.pug.academic.domain.vos.CounterpartHours;
import br.org.catolicasc.pug.academic.domain.vos.Period;
import br.org.catolicasc.pug.academic.infra.persistence.FormerStudentEntity;
import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentView;
import br.org.catolicasc.pug.shared.domain.vos.AuditInfo;

/**
 * Stateless utility class responsible for mapping between FormerStudent boundary layers.
 *
 * <p>This mapper handles both the translation between pure domain and JPA entities, translating
 * complex nested value objects like {@link CounterpartHours} and {@link Period} to and from flat
 * database columns. It also handles the assembly of deeply nested CQRS read views that cross module
 * boundaries (Identity, Academic).
 */
public final class FormerStudentMapper {

  private FormerStudentMapper() {}

  /**
   * Reconstitutes a pure Domain {@link FormerStudent} aggregate from a JPA {@link
   * FormerStudentEntity}.
   *
   * @param e the JPA persistence entity to convert
   * @return a fully constructed Domain {@link FormerStudent}, or {@code null} if the input entity
   *     is null
   */
  public static FormerStudent toDomain(FormerStudentEntity e) {
    if (e == null) {
      return null;
    }
    return FormerStudent.builder()
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
   * Translates a pure Domain {@link FormerStudent} aggregate into a newly instantiated JPA {@link
   * FormerStudentEntity}.
   *
   * @param d the Domain aggregate to convert
   * @return a newly constructed JPA {@link FormerStudentEntity}, or {@code null} if the input
   *     domain is null
   */
  public static FormerStudentEntity toEntity(FormerStudent d) {
    if (d == null) {
      return null;
    }
    return FormerStudentEntity.builder()
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
   * Updates an existing, attached JPA {@link FormerStudentEntity} with the current state of a
   * Domain {@link FormerStudent}.
   *
   * @param d the Domain aggregate containing the updated state
   * @param e the existing, attached JPA entity to update in-place
   */
  public static void copy(FormerStudent d, FormerStudentEntity e) {
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
   * FormerStudentView} DTO.
   *
   * @param s the JPA entity representing the formerStudent's academic records
   * @return a fully populated, flattened {@link FormerStudentView} DTO, or {@code null} if the
   *     formerStudent entity is null
   */
  public static FormerStudentView toView(FormerStudentEntity s) {
    if (s == null) {
      return null;
    }

    return new FormerStudentView(
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
