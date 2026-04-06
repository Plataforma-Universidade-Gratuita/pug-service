package com.pug.project.infra;

import com.pug.project.domain.ProjectBySchool;
import com.pug.project.infra.persistence.ProjectsBySchoolsEntity;

/**
 * Stateless utility class responsible for mapping between ProjectsBySchool boundary layers.
 *
 * <p>This mapper acts as an anti-corruption layer for the association aggregate {@link
 * ProjectBySchool}, handling conversions to and from the JPA persistence model ({@link
 * ProjectsBySchoolsEntity}) as well as consolidated read models that join projects and schools.
 */
public final class ProjectBySchoolMapper {

  /** Private constructor to prevent instantiation. */
  private ProjectBySchoolMapper() {}

  /**
   * Reconstitutes a {@link ProjectBySchool} aggregate from a JPA {@link ProjectsBySchoolsEntity}.
   *
   * @param e the JPA persistence entity to convert
   * @return a fully constructed {@link ProjectBySchool}, or {@code null} if the input entity is
   *     null
   */
  public static ProjectBySchool toDomain(ProjectsBySchoolsEntity e) {
    if (e == null || e.getId() == null) {
      return null;
    }
    return ProjectBySchool.builder()
        .projectId(e.getId().getProjectId())
        .schoolId(e.getId().getSchoolId())
        .build();
  }

  /**
   * Translates a {@link ProjectBySchool} aggregate into a newly instantiated JPA {@link
   * ProjectsBySchoolsEntity}.
   *
   * @param d the Domain aggregate to convert
   * @return a newly constructed {@link ProjectsBySchoolsEntity}, or {@code null} if the input
   *     aggregate is null
   */
  public static ProjectsBySchoolsEntity toEntity(ProjectBySchool d) {
    if (d == null) {
      return null;
    }
    ProjectsBySchoolsEntity.ProjectsBySchoolsId id =
        new ProjectsBySchoolsEntity.ProjectsBySchoolsId(d.getProjectId(), d.getSchoolId());
    return new ProjectsBySchoolsEntity(id);
  }
}
