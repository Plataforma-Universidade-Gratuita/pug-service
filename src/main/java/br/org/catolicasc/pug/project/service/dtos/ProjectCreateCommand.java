package br.org.catolicasc.pug.project.service.dtos;

import br.org.catolicasc.pug.project.domain.Project;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) acting as an application command to provision a new Project.
 *
 * <p>This record encapsulates the raw input data required by the application service to instantiate
 * a new {@link Project} aggregate.
 *
 * @param description the optional detailed description of the project's objectives
 * @param entityId the unique identifier of the partner organization offering the project
 * @param maxParticipants the maximum number of students allowed to enroll (optional)
 * @param name the title or name of the project
 * @param offeredHours the total counterpart hours the project offers to participants
 */
public record ProjectCreateCommand(
    String name,
    UUID entityId,
    String description,
    Integer maxParticipants,
    BigDecimal offeredHours) {}
