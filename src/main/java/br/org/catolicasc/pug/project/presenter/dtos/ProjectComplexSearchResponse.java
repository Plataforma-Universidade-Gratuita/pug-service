package br.org.catolicasc.pug.project.presenter.dtos;

import br.org.catolicasc.pug.partner.presenter.dtos.EntitySimpleComplexSearchResponse;
import java.util.UUID;

/**
 * Paginated search response DTO for project complex-search results.
 *
 * @param id the unique identifier of the project
 * @param name the project name
 * @param entity the lightweight partner-entity projection associated with the project
 * @param description the project description
 * @param projectInfo the grouped project operational metadata
 * @param status the grouped status projection
 */
public record ProjectComplexSearchResponse(
    UUID id,
    String name,
    EntitySimpleComplexSearchResponse entity,
    String description,
    ProjectInfoResponse projectInfo,
    ProjectStatusResponse status) {}
