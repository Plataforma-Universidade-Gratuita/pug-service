package com.pug.project.infra;

import com.pug.academic.infra.persistence.SchoolEntity;
import com.pug.academic.infra.read.dtos.SchoolView;
import com.pug.geo.infra.persistence.CityEntity;
import com.pug.geo.infra.read.dtos.CityView;
import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.identity.infra.persistence.UserEntity;
import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.identity.infra.read.dtos.UserView;
import com.pug.partner.infra.persistence.EntityEntity;
import com.pug.partner.infra.read.dtos.EntityView;
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
   * Projects a deeply nested set of JPA Entities across multiple domains into a comprehensive
   * {@link ProjectView} DTO.
   *
   * @param p the JPA entity representing the project
   * @param ent the JPA entity representing the partner organization
   * @param city the JPA entity representing the city where the partner operates
   * @param acc the JPA entity representing the creator's authentication account
   * @param u the JPA entity representing the creator's personal user profile
   * @return a fully populated {@link ProjectView} DTO
   */
  public static ProjectView toView(
      ProjectEntity p, EntityEntity ent, CityEntity city, AccountEntity acc, UserEntity u) {

    if (p == null) {
      return null;
    }

    CityView cityView =
        (city != null) ? new CityView(city.getId(), city.getName(), city.getIbgeCode()) : null;

    EntityView entityView =
        (ent != null)
            ? new EntityView(
                ent.getId(),
                ent.getCnpj(),
                ent.getName(),
                ent.getAddress(),
                cityView,
                ent.getCreatedAt(),
                ent.getUpdatedAt())
            : null;

    UserView userView =
        (u != null)
            ? new UserView(u.getId(), u.getCpf(), u.getName(), u.getCreatedAt(), u.getUpdatedAt())
            : null;

    AccountView accountView =
        (acc != null)
            ? new AccountView(
                acc.getId(),
                userView,
                acc.getEmail(),
                acc.getAccountType(),
                acc.getCreatedAt(),
                acc.getUpdatedAt(),
                acc.getActive())
            : null;

    return new ProjectView(
        p.getId(),
        p.getName(),
        entityView,
        p.getDescription(),
        accountView,
        p.getMaxParticipants(),
        p.getOfferedHours(),
        ProjectStatus.valueOf(p.getStatus()),
        p.getClosedAt(),
        p.getCreatedAt(),
        p.getUpdatedAt());
  }
}
