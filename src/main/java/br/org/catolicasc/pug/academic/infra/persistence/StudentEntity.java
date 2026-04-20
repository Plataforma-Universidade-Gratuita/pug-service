package br.org.catolicasc.pug.academic.infra.persistence;

import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * JPA entity representing an enrolled Student within the persistence layer.
 *
 * <p>This class is the database-mapped counterpart to the {@link Student}
 * domain aggregate. Instead of a standalone ID, it uses the linked account's UUID as its primary
 * key, effectively functioning as a one-to-one extension of an authentication account.
 */
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "accountId")
@ToString(of = {"accountId", "academicRegistration"})
@Entity
@Table(
    name = "students",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_students_registration",
          columnNames = {"academic_registration"})
    },
    indexes = {@Index(name = "idx_students_course", columnList = "course_id")})
public class StudentEntity {

  /**
   * The unique identifier of the linked authentication account.
   *
   * <p>Serves dual purpose as both the primary key for this entity and the logical foreign key to
   * the identity accounts table. It is strictly immutable once persisted.
   */
  @Id
  @Column(name = "account_id", nullable = false, updatable = false)
  private UUID accountId;

  /** The formal academic registration identifier for the student. */
  @Column(name = "academic_registration", nullable = false, length = 15)
  private String academicRegistration;

  /** The specific university campus where the student is enrolled. */
  @Enumerated(EnumType.STRING)
  @Column(name = "campus", nullable = false, length = 16)
  private Campi campus;

  /** The unique identifier (UUID) of the associated {@link CourseEntity}. */
  @Column(name = "course_id", nullable = false)
  private UUID courseId;

  /** The quantified hours the student is required to complete. */
  @Column(name = "required_hours", nullable = false, precision = 6, scale = 2)
  private BigDecimal requiredHours;

  /** The amount of hours already completed by the student. */
  @Column(name = "completed_hours", nullable = false, precision = 6, scale = 2)
  private BigDecimal completedHours;

  /** Flag indicating whether the required counterpart hours have been successfully completed. */
  @Column(name = "concluded", nullable = false)
  private Boolean concluded;

  /** The start date defining the validity of the student's enrollment period. */
  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  /** The due date (end date) defining the expiration of the student's enrollment period. */
  @Column(name = "due_date", nullable = false)
  private LocalDate dueDate;

  /** Timestamp indicating when this student record was initially created. */
  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  /** Timestamp indicating when this student record was last updated. */
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;
}
