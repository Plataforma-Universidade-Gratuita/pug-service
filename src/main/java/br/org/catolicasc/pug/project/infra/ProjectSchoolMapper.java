package br.org.catolicasc.pug.project.infra;

import br.org.catolicasc.pug.project.domain.ProjectSchool;
import br.org.catolicasc.pug.project.infra.persistence.ProjectSchoolEntity;

/**
 * Stateless utility class responsible for mapping between ProjectsBySchool boundary layers.
 *
 * <p>This mapper acts as an anti-corruption layer for the association aggregate {@link
 * ProjectSchool}, handling conversions to and from the JPA persistence model ({@link
 * ProjectSchoolEntity}) as well as consolidated read models that join projects and schools.
 */
public final class ProjectSchoolMapper {

  /** Private constructor to prevent instantiation. */
  private ProjectSchoolMapper() {}

  /**
   * Reconstitutes a {@link ProjectSchool} aggregate from a JPA {@link ProjectSchoolEntity}.
   *
   * @param e the JPA persistence entity to convert
   * @return a fully constructed {@link ProjectSchool}, or {@code null} if the input entity is null
   */
  public static ProjectSchool toDomain(ProjectSchoolEntity e) {
    if (e == null || e.getId() == null) {
      return null;
    }
    return ProjectSchool.builder()
        .projectId(e.getId().getProjectId())
        .schoolId(e.getId().getSchoolId())
        .build();
  }

  /**
   * Translates a {@link ProjectSchool} aggregate into a newly instantiated JPA {@link
   * ProjectSchoolEntity}.
   *
   * @param d the Domain aggregate to convert
   * @return a newly constructed {@link ProjectSchoolEntity}, or {@code null} if the input aggregate
   *     is null
   */
  public static ProjectSchoolEntity toEntity(ProjectSchool d) {
    if (d == null) {
      return null;
    }
    ProjectSchoolEntity.ProjectsBySchoolsId id =
        new ProjectSchoolEntity.ProjectsBySchoolsId(d.getProjectId(), d.getSchoolId());
    return new ProjectSchoolEntity(id);
  }
}
