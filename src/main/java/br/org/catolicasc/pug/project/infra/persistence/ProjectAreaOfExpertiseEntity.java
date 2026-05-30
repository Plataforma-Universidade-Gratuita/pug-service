package br.org.catolicasc.pug.project.infra.persistence;

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
 * JPA entity representing the many-to-many relationship between Projects and Areas of Expertise.
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
    name = "project_areas_of_expertise",
    indexes = {@Index(name = "idx_pbs_areas_of_expertise", columnList = "area_of_expertise_id")})
public class ProjectAreaOfExpertiseEntity {

  /**
   * Embeddable composite primary key mapping the intersection of a Project and an Area of
   * Expertise.
   */
  @SuppressFBWarnings("SE_NO_SERIALVERSIONID")
  @Embeddable
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @EqualsAndHashCode
  public static final class ProjectsAreaOfExpertiseId implements Serializable {
    @NotNull
    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @NotNull
    @Column(name = "area_of_expertise_id", nullable = false)
    private UUID areaOfExpertiseId;
  }

  /** The composite identifier mapping. */
  @EmbeddedId private ProjectsAreaOfExpertiseId id;
}
