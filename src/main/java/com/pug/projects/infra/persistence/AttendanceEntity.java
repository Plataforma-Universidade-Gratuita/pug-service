package com.pug.projects.infra.persistence;

import com.pug.shared.infra.persistence.BaseUuidV7Entity;
import com.pug.shared.infra.persistence.TimestampColumnsListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
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
@EntityListeners(TimestampColumnsListener.class)
public class AttendanceEntity extends BaseUuidV7Entity {

  @NotNull
  @Column(name = "project_id", nullable = false)
  private UUID projectId;

  @NotNull
  @Column(name = "student_id", nullable = false)
  private UUID studentId;

  @NotNull
  @DecimalMin(value = "0.00", inclusive = false)
  @Column(name = "duration", nullable = false, precision = 4, scale = 2)
  private BigDecimal duration;

  @DecimalMin(value = "-90.000000")
  @DecimalMax(value = "90.000000")
  @Column(name = "latitude", precision = 9, scale = 6)
  private BigDecimal latitude;

  @DecimalMin(value = "-180.000000")
  @DecimalMax(value = "180.000000")
  @Column(name = "longitude", precision = 9, scale = 6)
  private BigDecimal longitude;

  @NotBlank
  @Size(max = 16)
  @Column(name = "status", nullable = false, length = 16)
  private String status;

  @Size(max = 512)
  @Column(name = "qr_validation_hash", length = 512, unique = true)
  private String qrValidationHash;

  @Column(name = "validated_by")
  private UUID validatedBy;

  @Column(name = "validated_at")
  private OffsetDateTime validatedAt;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;
}
