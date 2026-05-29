package br.org.catolicasc.pug.academic.infra.persistence;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.shared.infra.persistence.BaseAuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * JPA entity representing an Academic Course within the persistence layer.
 *
 * <p>This class acts as the database-mapped counterpart to the {@link Course} domain aggregate. It
 * inherits a time-ordered UUIDv7 primary key and standard audit tracking fields from {@link
 * BaseAuditedEntity}.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(
    callSuper = true,
    of = {"name", "schoolId"})
@Entity
@Table(
    name = "courses",
    indexes = {@Index(name = "idx_courses_school", columnList = "school_id")})
public class CourseEntity extends BaseAuditedEntity {

  /**
   * The name of the academic course.
   */
  @Column(name = "name", nullable = false, length = 120, unique = true)
  private String name;

  /**
   * The unique identifier (UUID) of the associated {@link SchoolEntity}.
   *
   * <p>Acts as a foreign key linking this course to its parent academic school.
   */
  @Column(name = "school_id", nullable = false)
  private UUID schoolId;
}
