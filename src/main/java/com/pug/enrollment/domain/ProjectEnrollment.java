package com.pug.enrollment.domain;

import com.pug.enrollment.domain.enums.ProjectEnrollmentStatus;
import com.pug.project.domain.Project;
import com.pug.shared.id.UuidV7Hibernate;
import com.pug.student.domain.Student;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity
@Table(
    name = "projects_enrollments",
    indexes = {
      @Index(name = "idx_proj_enroll_project", columnList = "project_id"),
      @Index(name = "idx_proj_enroll_student", columnList = "student_id"),
      @Index(name = "idx_proj_enroll_status", columnList = "status")
    })
public class ProjectEnrollment {

  @Id
  @GeneratedValue
  @UuidGenerator(algorithm = UuidV7Hibernate.class)
  @Column(columnDefinition = "uuid", nullable = false, updatable = false)
  private UUID id;

  @NotNull
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(
      name = "project_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_proj_enroll_project"))
  private Project project;

  @NotNull
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(
      name = "student_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_proj_enroll_student"))
  private Student student;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 50, nullable = false)
  @Builder.Default
  private ProjectEnrollmentStatus status = ProjectEnrollmentStatus.PENDING;

  @CreationTimestamp
  @Column(name = "request_at", nullable = false, updatable = false)
  private Instant requestAt;

  @Column(name = "accepted_at")
  private Instant acceptedAt;

  @Column(name = "closing_status_at")
  private Instant closingStatusAt;
}
