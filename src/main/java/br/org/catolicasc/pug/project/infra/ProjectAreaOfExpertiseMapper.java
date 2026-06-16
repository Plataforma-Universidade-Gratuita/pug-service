package br.org.catolicasc.pug.project.infra;

import br.org.catolicasc.pug.project.domain.ProjectAreaOfExpertise;
import br.org.catolicasc.pug.project.infra.persistence.ProjectAreaOfExpertiseEntity;

/**
 * Stateless utility class responsible for mapping between ProjectsBySchool boundary layers.
 *
 * <p>This mapper acts as an anti-corruption layer for the association aggregate {@link
 * ProjectAreaOfExpertise}, handling conversions to and from the JPA persistence model ({@link
 * ProjectAreaOfExpertiseEntity}) as well as consolidated read models that join projects and
 * areaOfExpertises.
 */
public final class ProjectAreaOfExpertiseMapper {

  private ProjectAreaOfExpertiseMapper() {}

  /**
   * Reconstitutes a {@link ProjectAreaOfExpertise} aggregate from a JPA {@link
   * ProjectAreaOfExpertiseEntity}.
   *
   * @param e the JPA persistence entity to convert
   * @return a fully constructed {@link ProjectAreaOfExpertise}, or {@code null} if the input entity
   *     is null
   */
  public static ProjectAreaOfExpertise toDomain(ProjectAreaOfExpertiseEntity e) {
    if (e == null || e.getId() == null) {
      return null;
    }
    return ProjectAreaOfExpertise.builder()
        .projectId(e.getId().getProjectId())
        .areaOfExpertiseId(e.getId().getAreaOfExpertiseId())
        .build();
  }

  /**
   * Translates a {@link ProjectAreaOfExpertise} aggregate into a newly instantiated JPA {@link
   * ProjectAreaOfExpertiseEntity}.
   *
   * @param d the Domain aggregate to convert
   * @return a newly constructed {@link ProjectAreaOfExpertiseEntity}, or {@code null} if the input
   *     aggregate is null
   */
  public static ProjectAreaOfExpertiseEntity toEntity(ProjectAreaOfExpertise d) {
    if (d == null) {
      return null;
    }
    ProjectAreaOfExpertiseEntity.ProjectsAreaOfExpertiseId id =
        new ProjectAreaOfExpertiseEntity.ProjectsAreaOfExpertiseId(
            d.getProjectId(), d.getAreaOfExpertiseId());
    return new ProjectAreaOfExpertiseEntity(id);
  }
}
