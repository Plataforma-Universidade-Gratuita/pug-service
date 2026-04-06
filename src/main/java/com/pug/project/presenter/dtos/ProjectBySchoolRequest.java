package com.pug.project.presenter.dtos;

import com.pug.shared.validation.UuidV7;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for assigning a Project to one or
 * more Schools.
 *
 * <p>This request does not create or update the {@link com.pug.project.domain.Project} itself; it
 * is dedicated exclusively to managing the project–school association via {@link
 * com.pug.project.domain.ProjectBySchool}.
 *
 * @param projectId the unique identifier (UUIDv7) of the project to be associated
 * @param schoolIds the list of unique identifiers (UUIDv7) of the schools to link to the project
 */
public record ProjectBySchoolRequest(
    @NotNull @UuidV7 UUID projectId, @NotEmpty List<@NotNull @UuidV7 UUID> schoolIds) {}
