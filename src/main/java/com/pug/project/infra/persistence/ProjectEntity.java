package com.pug.project.infra.persistence;

import com.pug.shared.infra.persistence.BaseAuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

/**
 * JPA entity representing a Project within the persistence layer.
 *
 * <p>This class acts as the database-mapped counterpart to the {@link
 * com.pug.project.domain.Project} domain aggregate. It inherits a time-ordered UUIDv7 primary key
 * and standard audit tracking fields from {@link BaseAuditedEntity}.
 */
@Getter
@Setter
@SuperBuilder
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
      @Index(name = "idx_projects_created_by", columnList = "created_by"),
      @Index(name = "idx_projects_created_at", columnList = "created_at"),
      @Index(name = "idx_projects_updated_at", columnList = "updated_at"),
      @Index(name = "idx_projects_closed_at", columnList = "closed_at")
    })
@Indexed
public class ProjectEntity extends BaseAuditedEntity {

  /**
   * The title or name of the project.
   *
   * <p>This field is heavily indexed for optimized searching using custom analyzers defined in
   * {@link com.pug.shared.infra.search.EsAnalysis}. It projects into four distinct index fields:
   *
   * <ul>
   *   <li><b>name:</b> Standard full-text search (fuzzy matching, accent-insensitive).
   *   <li><b>name_auto:</b> Edge n-gram indexing for fast autocomplete ("type-as-you-go").
   *   <li><b>name_exact:</b> Wildcard and exact phrase matching.
   *   <li><b>name_sort:</b> Normalized keyword field used exclusively for alphabetical sorting.
   * </ul>
   */
  @FullTextField(analyzer = "pt_folded", searchAnalyzer = "pt_folded")
  @FullTextField(name = "name_auto", analyzer = "auto_ngram", searchAnalyzer = "pt_folded")
  @KeywordField(name = "name_exact", normalizer = "folding_lowercase")
  @KeywordField(name = "name_sort", normalizer = "folding_lowercase", sortable = Sortable.YES)
  @NotBlank
  @Size(max = 150)
  @Column(name = "name", nullable = false, length = 150)
  private String name;

  /** The unique identifier (UUID) of the partner organization offering this project. */
  @NotNull
  @Column(name = "entity_id", nullable = false)
  private UUID entityId;

  /** A detailed description of the project's objectives and tasks. */
  @Size(max = 4000)
  @Column(name = "description", nullable = false, length = 4000)
  private String description;

  /** The unique identifier (Account ID) of the staff member who created the project. */
  @NotNull
  @Column(name = "created_by", nullable = false)
  private UUID createdBy;

  /** The exact timestamp when the project reached a terminal/closed state. */
  @Column(name = "closed_at")
  private OffsetDateTime closedAt;

  /** The total amount of counterpart hours the project offers to its participants. */
  @NotNull
  @DecimalMin("0.00")
  @Column(name = "offered_hours", nullable = false, precision = 6, scale = 2)
  private BigDecimal offeredHours;

  /** A quantidade de horas completadas no projeto até o momento. */
  @NotNull
  @DecimalMin("0.00")
  @Column(name = "completed_hours", nullable = false, precision = 6, scale = 2)
  private BigDecimal completedHours;

  /** The current execution state of the project (e.g., PLANNED, IN_PROGRESS). */
  @NotBlank
  @Size(max = 16)
  @Column(name = "status", nullable = false, length = 16)
  private String status;

  /** The maximum number of students allowed to enroll in the project. */
  @Min(0)
  @Column(name = "max_participants")
  private Integer maxParticipants;
}
