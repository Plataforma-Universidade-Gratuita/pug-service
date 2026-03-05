package com.pug.projects.infra;

import com.pug.geo.infra.persistence.CityEntity;
import com.pug.geo.infra.read.dtos.CityView;
import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.identity.infra.persistence.UserEntity;
import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.identity.infra.read.dtos.UserView;
import com.pug.partner.infra.persistence.EntityEntity;
import com.pug.partner.infra.read.dtos.EntityView;
import com.pug.projects.domain.Project;
import com.pug.projects.domain.enums.ProjectStatus;
import com.pug.projects.domain.vos.ProjectInfo;
import com.pug.projects.infra.persistence.ProjectEntity;
import com.pug.projects.infra.read.dtos.ProjectView;
import com.pug.shared.domain.vos.AuditInfo;

/**
 * Stateless utility class responsible for mapping between Project boundary layers.
 * <p>
 * This mapper acts as an anti-corruption layer, ensuring that the pure Domain model ({@link Project})
 * does not leak into or depend upon the JPA Persistence model ({@link ProjectEntity}) or the
 * Read/Query model ({@link ProjectView}).
 */
public final class ProjectMapper {

    /**
     * Private constructor to prevent instantiation.
     */
    private ProjectMapper() {
    }

    /**
     * Reconstitutes a pure Domain {@link Project} aggregate from a JPA {@link ProjectEntity}.
     * <p>
     * This method translates primitive database columns back into their corresponding
     * Domain Value Objects (e.g., {@link ProjectInfo}, {@link AuditInfo}) and parses Enums.
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
     * Translates a pure Domain {@link Project} aggregate into a newly instantiated JPA {@link ProjectEntity}.
     * <p>
     * This is typically used when persisting a brand-new entity to the database. It flattens
     * Domain Value Objects back into primitive types suitable for JDBC insertion.
     *
     * @param d the Domain aggregate to convert
     * @return a newly constructed JPA {@link ProjectEntity}, or {@code null} if the input domain is null
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
     * Updates an existing, attached JPA {@link ProjectEntity} with the current state of a Domain {@link Project}.
     * <p>
     * This method is used during update operations. Modifying the attached entity allows the
     * ORM (Hibernate) to track changes and issue the appropriate SQL {@code UPDATE} statements.
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
     * Projects a deeply nested set of JPA Entities across multiple domains into a comprehensive
     * {@link ProjectView} DTO.
     * <p>
     * Used heavily by the CQRS query layer to construct fully resolved data structures
     * that encapsulate the project, its managing partner organization, and the staff creator.
     *
     * @param p    the JPA entity representing the project
     * @param ent  the JPA entity representing the partner organization
     * @param city the JPA entity representing the city where the partner operates
     * @param acc  the JPA entity representing the creator's authentication account
     * @param u    the JPA entity representing the creator's personal user profile
     * @return a fully populated {@link ProjectView} DTO
     */
    public static ProjectView toView(
            ProjectEntity p, EntityEntity ent, CityEntity city, AccountEntity acc, UserEntity u) {

        if (p == null) return null;

        CityView cityView = null;
        if (city != null) {
            cityView = new CityView(city.getId(), city.getName(), city.getIbgeCode());
        }

        EntityView entityView = null;
        if (ent != null) {
            entityView =
                    new EntityView(
                            ent.getId(),
                            ent.getCnpj(),
                            ent.getName(),
                            ent.getAddress(),
                            cityView,
                            ent.getCreatedAt(),
                            ent.getUpdatedAt());
        }

        UserView userView = null;
        if (u != null) {
            userView = new UserView(u.getId(), u.getCpf(), u.getName(), u.getCreatedAt(), u.getUpdatedAt());
        }

        AccountView accountView = null;
        if (acc != null) {
            accountView =
                    new AccountView(
                            acc.getId(),
                            userView,
                            acc.getEmail(),
                            acc.getAccountType(),
                            acc.getCreatedAt(),
                            acc.getUpdatedAt());
        }

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