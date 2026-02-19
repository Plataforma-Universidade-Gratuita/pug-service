package com.pug.projects.infra.persistence;

import com.pug.shared.infra.persistence.BaseUuidV7Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
    of = {"name", "status"})
@Entity
@Table(
    name = "projects",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_projects_entity_name",
          columnNames = {"entity_id", "name"})
    },
    indexes = {
      @Index(name = "idx_projects_entity", columnList = "entity_id"),
      @Index(name = "idx_projects_status", columnList = "status"),
      @Index(name = "idx_projects_created_by", columnList = "created_by_user_id"),
      @Index(name = "idx_projects_created_at", columnList = "created_at"),
      @Index(name = "idx_projects_closed_at", columnList = "closed_at")
    })
@EntityListeners(TimestampTechnicalColumns.class)
public class ProjectEntity extends BaseUuidV7Entity {

  @NotBlank
  @Size(max = 150)
  @Column(name = "name", nullable = false, length = 150)
  private String name;

  @NotNull
  @Column(name = "entity_id", nullable = false)
  private UUID entityId;

  @NotBlank
  @Size(max = 4000)
  @Column(name = "description", nullable = false, length = 4000)
  private String description;

  @NotNull
  @Column(name = "created_by", nullable = false)
  private UUID createdBy;

  @NotNull
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "closed_at")
  private OffsetDateTime closedAt;

  @NotNull
  @DecimalMin("0.00")
  @Column(name = "offered_hours", nullable = false, precision = 6, scale = 2)
  private BigDecimal offeredHours;

  @NotNull
  @DecimalMin("0.00")
  @Column(name = "completed_hours", nullable = false, precision = 6, scale = 2)
  private BigDecimal completedHours;

  @NotBlank
  @Size(max = 16)
  @Column(name = "status", nullable = false, length = 16)
  private String status;

  @Min(0)
  @Column(name = "max_participants")
  private Integer maxParticipants;
}
