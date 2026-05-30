package br.org.catolicasc.pug.project.presenter.dtos;

import br.org.catolicasc.pug.partner.presenter.dtos.entities.EntitySimpleComplexSearchResponse;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the standardized API JSON response for Projects.
 *
 * @param id the unique identifier (UUIDv7) of the project
 * @param name the title or name of the project
 * @param entity the lightweight partner-entity projection associated with the project
 * @param description the project description
 * @param projectInfo the grouped operational metadata associated with the project
 * @param status the grouped lifecycle status associated with the project
 */
public record ProjectResponse(
    UUID id,
    String name,
    EntitySimpleComplexSearchResponse entity,
    String description,
    ProjectInfoResponse projectInfo,
    ProjectStatusResponse status) {}
