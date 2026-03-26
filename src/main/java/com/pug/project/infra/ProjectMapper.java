package com.pug.project.infra;

import com.pug.academic.infra.persistence.SchoolEntity;
import com.pug.academic.infra.read.dtos.SchoolView;
import com.pug.project.domain.Project;
import com.pug.project.domain.enums.ProjectStatus;
import com.pug.project.domain.vos.ProjectInfo;
import com.pug.project.infra.persistence.ProjectEntity;
import com.pug.project.infra.read.dtos.ProjectView;
import com.pug.project.infra.read.dtos.SchoolProjectView;
import com.pug.shared.domain.vos.AuditInfo;
import java.util.List;

/**
 * Stateless utility class responsible for mapping between Project boundary layers.
 *
 * <p>This mapper acts as an anti-corruption layer, ensuring that the pure Domain model ({@link
 * Project}) does not leak into or depend upon the JPA Persistence model ({@link ProjectEntity}) or
 * the Read/Query model ({@link ProjectView}).
 */
public final class ProjectMapper {

  /** Private constructor to prevent instantiation. */
  private ProjectMapper() {}

  /**
   * Updates an existing, attached JPA {@link ProjectEntity} with the current state of a Domain
   * {@link Project}.
   *
   * @param d the Domain aggregate containing the updated state
   * @param e the existing, attached JPA entity to update in-place
   */
  public static void copy(Project d, ProjectEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setName(d.getName());
    e.setEntityId(d.getEntityId());
    e.setDescription(d.getDescription());
    e.setMaxParticipants(d.getProjectInfo().getMaxParticipants());
    e.setOfferedHours(d.getProjectInfo().getOfferedHours());
    e.setClosedAt(d.getProjectInfo().getClosedAt());
    e.setStatus(d.getProjectStatus().name());
  }

  /**
   * Projects a raw JPA {@link SchoolEntity} and a list of resolved {@link ProjectView} DTOs into a
   * {@link SchoolProjectView}.
   *
   * @param s the JPA entity representing the school
   * @param projects the list of resolved project views associated with the school
   * @return a fully populated {@link SchoolProjectView} DTO
   */
  public static SchoolProjectView toSchoolProjectView(SchoolEntity s, List<ProjectView> projects) {
    if (s == null) {
      return null;
    }
    SchoolView schoolView =
        new SchoolView(s.getId(), s.getName(), s.getCreatedAt(), s.getUpdatedAt());
    return new SchoolProjectView(schoolView, projects);
  }

  /**
   * Reconstitutes a pure Domain {@link Project} aggregate from a JPA {@link ProjectEntity}.
   *
   * @param e the JPA persistence entity to convert
   * @return a fully constructed Domain {@link Project}, or {@code null} if the input entity is null
   */
  public static Project toDomain(ProjectEntity e) {
    if (e == null) {
      return null;
    }

    ProjectInfo info =
        ProjectInfo.builder()
            .createdBy(e.getCreatedBy())
            .maxParticipants(e.getMaxParticipants())
            .offeredHours(e.getOfferedHours())
            .closedAt(e.getClosedAt())
            .auditInfo(AuditInfo.factory(e.getCreatedAt(), e.getUpdatedAt()))
            .build();

    return Project.builder()
        .id(e.getId())
        .name(e.getName())
        .entityId(e.getEntityId())
        .description(e.getDescription())
        .projectInfo(info)
        .projectStatus(ProjectStatus.valueOf(e.getStatus()))
        .build();
  }

  /**
   * Translates a pure Domain {@link Project} aggregate into a newly instantiated JPA {@link
   * ProjectEntity}.
   *
   * @param d the Domain aggregate to convert
   * @return a newly constructed JPA {@link ProjectEntity}, or {@code null} if the input domain is
   *     null
   */
  public static ProjectEntity toEntity(Project d) {
    if (d == null) {
      return null;
    }

    return ProjectEntity.builder()
        .id(d.getId())
        .name(d.getName())
        .entityId(d.getEntityId())
        .description(d.getDescription())
        .createdBy(d.getProjectInfo().getCreatedBy())
        .maxParticipants(d.getProjectInfo().getMaxParticipants())
        .offeredHours(d.getProjectInfo().getOfferedHours())
        .closedAt(d.getProjectInfo().getClosedAt())
        .status(d.getProjectStatus().name())
        .createdAt(d.getProjectInfo().getAuditInfo().getCreatedAt())
        .updatedAt(d.getProjectInfo().getAuditInfo().getUpdatedAt())
        .build();
  }

  /**
   * Projects a raw JPA {@link ProjectEntity} into a {@link ProjectView} DTO.
   *
   * @param p the JPA entity representing the project
   * @return a fully populated {@link ProjectView} DTO containing IDs for relations
   */
  public static ProjectView toView(ProjectEntity p) {
    if (p == null) {
      return null;
    }

    return new ProjectView(
        p.getId(),
        p.getName(),
        p.getEntityId(),
        p.getDescription(),
        p.getCreatedBy(),
        p.getMaxParticipants(),
        p.getOfferedHours(),
        ProjectStatus.valueOf(p.getStatus()),
        p.getClosedAt(),
        p.getCreatedAt(),
        p.getUpdatedAt());
  }
}
