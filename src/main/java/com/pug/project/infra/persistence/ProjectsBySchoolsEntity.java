package com.pug.project.infra.persistence;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * JPA entity representing the many-to-many relationship between Projects and Schools.
 *
 * <p>This entity is intended strictly for read-only query projections and relational mapping.
 */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
@Entity
@Table(
    name = "projects_by_schools",
    indexes = {@Index(name = "idx_pbs_school", columnList = "school_id")})
public class ProjectsBySchoolsEntity {

  /** Embeddable composite primary key mapping the intersection of a Project and a School. */
  @SuppressFBWarnings("SE_NO_SERIALVERSIONID")
  @Embeddable
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @EqualsAndHashCode
  public static final class ProjectsBySchoolsId implements Serializable {
    @NotNull
    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @NotNull
    @Column(name = "school_id", nullable = false)
    private UUID schoolId;
  }

  /** The composite identifier mapping. */
  @EmbeddedId private ProjectsBySchoolsId id;
}
