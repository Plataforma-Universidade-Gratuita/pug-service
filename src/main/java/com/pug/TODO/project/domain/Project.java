package com.pug.TODO.project.domain;

import com.pug.TODO.project.domain.enums.ProjectStatus;
import com.pug.academic.domain.FieldOfStudy;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.Staff;
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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    name = "projects",
    indexes = {
      @Index(name = "idx_projects_entity", columnList = "entity_id"),
      @Index(name = "idx_projects_field", columnList = "field_id"),
      @Index(name = "idx_projects_status", columnList = "status"),
      @Index(name = "idx_projects_created_by", columnList = "created_by")
    })
public class Project {

  @Id
  @GeneratedValue
  @UuidGenerator(algorithm = UuidV7Hibernate.class)
  @Column(columnDefinition = "uuid", nullable = false, updatable = false)
  private UUID id;

  @NotBlank
  @Size(max = 150)
  @Column(length = 150, nullable = false)
  private String name;

  @Lob
  @Column(columnDefinition = "text")
  private String description;

  @NotNull
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(
      name = "entity_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_projects_entity"))
  private PartnerEntity entity;

  @NotNull
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(
      name = "field_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_projects_field"))
  private FieldOfStudy field;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 50, nullable = false)
  private ProjectStatus status;

  @Min(1)
  @Column(name = "max_participants")
  private Integer maxParticipants;

  @NotNull
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(
      name = "created_by",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_projects_created_by"))
  private Staff createdBy;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;
}
