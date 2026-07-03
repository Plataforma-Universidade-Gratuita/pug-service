/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.infra.persistence;

import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.shared.infra.persistence.BaseAuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * JPA entity representing a FormerStudent's Attendance record within the persistence layer.
 *
 * <p>This class acts as the database-mapped counterpart to the {@link Attendance} domain aggregate.
 * It inherits a time-ordered UUIDv7 primary key and standard audit tracking fields from {@link
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
    of = {"projectId", "formerStudentId", "status"})
@Entity
@Table(
    name = "attendances",
    indexes = {
      @Index(name = "idx_attendances_enrollment", columnList = "project_id, former_student_id"),
      @Index(name = "idx_attendances_status", columnList = "status"),
      @Index(name = "idx_attendances_validated_by", columnList = "validated_by"),
      @Index(name = "idx_attendances_validated_at", columnList = "validated_at"),
      @Index(name = "idx_attendances_former_student_stat", columnList = "former_student_id, status")
    })
public class AttendanceEntity extends BaseAuditedEntity {

  @NotNull
  @Column(name = "project_id", nullable = false)
  private UUID projectId;

  @NotNull
  @Column(name = "former_student_id", nullable = false)
  private UUID formerStudentId;

  @NotNull
  @DecimalMin(value = "0.00", inclusive = false)
  @Column(name = "duration", nullable = false, precision = 4, scale = 2)
  private BigDecimal duration;

  @NotBlank
  @Size(max = 16)
  @Column(name = "status", nullable = false, length = 16)
  private String status;

  @NotBlank
  @Size(max = 512)
  @Column(name = "qr_validation_hash", nullable = false, length = 512, unique = true)
  private String qrValidationHash;

  @Column(name = "validated_by")
  private UUID validatedBy;

  @Column(name = "validated_at")
  private OffsetDateTime validatedAt;
}
