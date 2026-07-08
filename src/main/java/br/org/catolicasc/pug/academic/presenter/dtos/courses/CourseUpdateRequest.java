package br.org.catolicasc.pug.academic.presenter.dtos.courses;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for partially updating an existing
 * Course.
 *
 * <p>Because updates can be partial, all fields in this record are inherently optional. If a field
 * is provided as {@code null} or omitted from the JSON payload, the application service will ignore
 * it and retain the existing value for that specific attribute in the database.
 *
 * @param name the new name of the course, or {@code null} to leave unchanged
 * @param areaOfExpertiseId the new unique identifier of the area of expertise offering the course,
 *     or {@code null} to leave unchanged
 */
public record CourseUpdateRequest(String name, UUID areaOfExpertiseId) {}
