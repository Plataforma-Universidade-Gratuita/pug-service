package com.pug.projects.infra.persistence;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    indexes = {
      @Index(name = "idx_pbs_project", columnList = "project_id"),
      @Index(name = "idx_pbs_school", columnList = "school_id")
    })
public class ProjectsBySchoolsEntity {

  @SuppressFBWarnings("SE_NO_SERIALVERSIONID")
  @Embeddable
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @EqualsAndHashCode
  public static final class ProjectsBySchoolsId implements Serializable {
    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "school_id", nullable = false)
    private UUID schoolId;
  }

  @EmbeddedId private ProjectsBySchoolsId id;
}
