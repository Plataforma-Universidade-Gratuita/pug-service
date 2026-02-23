package com.pug.projects.infra.persistence;

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
      @Index(name = "idx_enrollments_student", columnList = "student_id"),
      @Index(name = "idx_enrollments_status", columnList = "status"),
      @Index(name = "idx_enrollments_project", columnList = "project_id")
    })
public class EnrollmentEntity {

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

    @Column(name = "student_id", nullable = false)
    private UUID studentId;
  }

  @EmbeddedId private EnrollmentsId id;

  @NotBlank
  @Size(max = 16)
  @Column(name = "status", nullable = false, length = 16)
  private String status;

  @NotNull
  @Column(name = "request_at", nullable = false)
  private OffsetDateTime requestAt;

  @Column(name = "accepted_at")
  private OffsetDateTime acceptedAt;

  @Column(name = "closing_status_at")
  private OffsetDateTime closingStatusAt;
}
