package br.org.catolicasc.pug.project.infra.persistence;

import br.org.catolicasc.pug.project.domain.Enrollment;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * JPA entity representing a FormerStudent's Enrollment in a Project within the persistence layer.
 *
 * <p>This class is the database-mapped counterpart to the {@link Enrollment} domain aggregate.
 * Because an enrollment is uniquely identified by the combination of a formerStudent and a project,
 * this entity utilizes a composite primary key ({@link EnrollmentsId}).
 */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "status"})
@Entity
@Table(
    name = "enrollments",
    indexes = {
      @Index(name = "idx_enrollments_former_student", columnList = "former_student_id"),
      @Index(name = "idx_enrollments_status", columnList = "status")
    })
public class EnrollmentEntity {

  /** Embeddable composite primary key mapping the intersection of a Project and a FormerStudent. */
  @SuppressFBWarnings("SE_NO_SERIALVERSIONID")
  @Embeddable
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @EqualsAndHashCode
  public static final class EnrollmentsId implements Serializable {
    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "former_student_id", nullable = false)
    private UUID formerStudentId;
  }

  /** The composite identifier mapping. */
  @EmbeddedId private EnrollmentsId id;

  /** The current lifecycle status of the enrollment (e.g., PENDING, APPROVED). */
  @NotBlank
  @Size(max = 16)
  @Column(name = "status", nullable = false, length = 16)
  private String status;

  /** Timestamp indicating when this enrollment request was initially created. */
  @NotNull
  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  /** Timestamp indicating when this enrollment record was last modified. */
  @NotNull
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  /** Timestamp indicating when the enrollment request was formally accepted by staff. */
  @Column(name = "accepted_at")
  private OffsetDateTime acceptedAt;

  /**
   * Timestamp indicating when the enrollment reached a terminal state (e.g., Completed, Canceled).
   */
  @Column(name = "closing_status_at")
  private OffsetDateTime closingStatusAt;
}
