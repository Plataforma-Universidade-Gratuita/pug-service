package com.pug.attendance.domain;

import com.pug.attendance.domain.enums.AttendanceStatus;
import com.pug.enrollment.domain.ProjectEnrollment;
import com.pug.partner.domain.Staff;
import com.pug.project.domain.ProjectLocation;
import com.pug.shared.id.UuidV7Hibernate;
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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@Entity
@Table(
    name = "projects_attendances",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_projects_attendances_qr_hash",
            columnNames = "qr_validation_hash"),
    indexes = {
      @Index(name = "idx_projects_attendances_enrollment", columnList = "enrollment_id"),
      @Index(name = "idx_projects_attendances_location", columnList = "project_location_id"),
      @Index(name = "idx_projects_attendances_status", columnList = "status")
    })
public class ProjectAttendance {

  @Id
  @GeneratedValue
  @org.hibernate.annotations.UuidGenerator(algorithm = UuidV7Hibernate.class)
  @Column(columnDefinition = "uuid", nullable = false, updatable = false)
  UUID id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(
      name = "enrollment_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_projects_attendances_enrollment"))
  private ProjectEnrollment enrollment;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(
      name = "project_location_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_projects_attendances_location"))
  private ProjectLocation projectLocation;

  @NotNull
  @Digits(integer = 2, fraction = 2)
  @Column(precision = 4, scale = 2, nullable = false)
  private BigDecimal duration;

  @Digits(integer = 3, fraction = 6)
  @Column(precision = 9, scale = 6)
  private BigDecimal latitude;

  @Digits(integer = 3, fraction = 6)
  @Column(precision = 9, scale = 6)
  private BigDecimal longitude;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private AttendanceStatus status = AttendanceStatus.PENDING;

  @Size(max = 512)
  @Column(name = "qr_validation_hash", length = 512, unique = true)
  private String qrValidationHash;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "validated_by",
      foreignKey = @ForeignKey(name = "fk_projects_attendances_validated_by"))
  private Staff validatedBy;

  @Column(name = "validated_at")
  private Instant validatedAt;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;
}
