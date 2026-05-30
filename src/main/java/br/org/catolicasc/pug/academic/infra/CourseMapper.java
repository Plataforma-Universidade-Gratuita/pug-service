package br.org.catolicasc.pug.academic.infra;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.infra.persistence.AreaOfExpertiseEntity;
import br.org.catolicasc.pug.academic.infra.persistence.CourseEntity;
import br.org.catolicasc.pug.academic.infra.read.dtos.AreaOfExpertiseView;
import br.org.catolicasc.pug.academic.infra.read.dtos.CourseView;
import br.org.catolicasc.pug.shared.domain.vos.AuditInfo;

/**
 * Stateless utility class responsible for mapping between Course boundary layers.
 *
 * <p>This mapper acts as an anti-corruption layer, ensuring that the pure Domain model ({@link
 * Course}) does not leak into or depend upon the JPA Persistence model ({@link CourseEntity}) or
 * the Read/Query model ({@link CourseView}).
 */
public final class CourseMapper {
  /** Private constructor to prevent instantiation. */
  private CourseMapper() {}

  /**
   * Reconstitutes a pure Domain {@link Course} aggregate from a JPA {@link CourseEntity}.
   *
   * @param e the JPA persistence entity to convert
   * @return a fully constructed Domain {@link Course}, or {@code null} if the input entity is null
   */
  public static Course toDomain(CourseEntity e) {
    if (e == null) {
      return null;
    }
    return Course.builder()
        .id(e.getId())
        .name(e.getName())
        .areaOfExpertiseId(e.getAreaOfExpertiseId())
        .auditInfo(AuditInfo.factory(e.getCreatedAt(), e.getUpdatedAt()))
        .build();
  }

  /**
   * Translates a pure Domain {@link Course} aggregate into a newly instantiated JPA {@link
   * CourseEntity}.
   *
   * @param d the Domain aggregate to convert
   * @return a newly constructed JPA {@link CourseEntity}, or {@code null} if the input domain is
   *     null
   */
  public static CourseEntity toEntity(Course d) {
    if (d == null) {
      return null;
    }
    return CourseEntity.builder()
        .id(d.getId())
        .name(d.getName())
        .areaOfExpertiseId(d.getAreaOfExpertiseId())
        .createdAt(d.getAuditInfo().getCreatedAt())
        .updatedAt(d.getAuditInfo().getUpdatedAt())
        .build();
  }

  /**
   * Updates an existing, attached JPA {@link CourseEntity} with the current state of a Domain
   * {@link Course}.
   *
   * @param d the Domain aggregate containing the updated state
   * @param e the existing, attached JPA entity to update in-place
   */
  public static void copy(Course d, CourseEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setName(d.getName());
    e.setAreaOfExpertiseId(d.getAreaOfExpertiseId());
    e.setCreatedAt(d.getAuditInfo().getCreatedAt());
    e.setUpdatedAt(d.getAuditInfo().getUpdatedAt());
  }

  /**
   * Projects a {@link CourseEntity} and its parent {@link AreaOfExpertiseEntity} into a
   * consolidated {@link CourseView} DTO.
   *
   * @param c the JPA persistence entity representing the course
   * @param s the JPA persistence entity representing the linked school
   * @return a populated {@link CourseView} DTO, or {@code null} if the course entity is null
   */
  public static CourseView toView(CourseEntity c, AreaOfExpertiseEntity s) {
    if (c == null) {
      return null;
    }
    AreaOfExpertiseView schoolView =
        (s != null)
            ? new AreaOfExpertiseView(s.getId(), s.getName(), s.getCreatedAt(), s.getUpdatedAt())
            : null;
    return new CourseView(c.getId(), c.getName(), schoolView, c.getCreatedAt(), c.getUpdatedAt());
  }
}
