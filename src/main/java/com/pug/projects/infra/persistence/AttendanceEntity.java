package com.pug.projects.infra.persistence;

import com.pug.shared.infra.persistence.BaseAuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * JPA entity representing a Student's Attendance record within the persistence layer.
 * <p>
 * This class acts as the database-mapped counterpart to the {@link com.pug.projects.domain.Attendance}
 * domain aggregate. It inherits a time-ordered UUIDv7 primary key and standard audit tracking
 * fields from {@link BaseAuditedEntity}.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(
        callSuper = true,
        of = {"projectId", "studentId", "status"})
@Entity
@Table(
        name = "attendances",
        indexes = {
                @Index(name = "idx_attendances_enrollment", columnList = "project_id, student_id"),
                @Index(name = "idx_attendances_status", columnList = "status"),
                @Index(name = "idx_attendances_validated_by", columnList = "validated_by"),
                @Index(name = "idx_attendances_validated_at", columnList = "validated_at"),
                @Index(name = "idx_attendances_student_stat", columnList = "student_id, status")
        })
public class AttendanceEntity extends BaseAuditedEntity {

  /**
   * The unique identifier (UUID) of the associated Project.
   */
  @NotNull
  @Column(name = "project_id", nullable = false)
  private UUID projectId;

  /**
   * The unique identifier (Account ID UUID) of the associated Student.
   */
  @NotNull
  @Column(name = "student_id", nullable = false)
  private UUID studentId;

  /**
   * The recorded time duration the student spent on the project.
   */
  @NotNull
  @DecimalMin(value = "0.00", inclusive = false)
  @Column(name = "duration", nullable = false, precision = 4, scale = 2)
  private BigDecimal duration;

  /**
   * The geographic latitude where the attendance was recorded.
   */
  @NotNull
  @DecimalMin(value = "-90.000000")
  @DecimalMax(value = "90.000000")
  @Column(name = "latitude", nullable = false, precision = 9, scale = 6)
  private BigDecimal latitude;

  /**
   * The geographic longitude where the attendance was recorded.
   */
  @NotNull
  @DecimalMin(value = "-180.000000")
  @DecimalMax(value = "180.000000")
  @Column(name = "longitude", nullable = false, precision = 9, scale = 6)
  private BigDecimal longitude;

  /**
   * The current validation status of the attendance (e.g., WAITING, PRESENT).
   */
  @NotBlank
  @Size(max = 16)
  @Column(name = "status", nullable = false, length = 16)
  private String status;

  /**
   * The unique cryptographic hash of the QR code used for validation.
   */
  @NotBlank
  @Size(max = 512)
  @Column(name = "qr_validation_hash", nullable = false, length = 512, unique = true)
  private String qrValidationHash;

  /**
   * The unique identifier of the Staff account that validated the attendance.
   */
  @Column(name = "validated_by")
  private UUID validatedBy;

  /**
   * The exact timestamp when the attendance was explicitly validated by staff.
   */
  @Column(name = "validated_at")
  private OffsetDateTime validatedAt;
}