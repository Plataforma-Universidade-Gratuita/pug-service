package br.org.catolicasc.pug.academic.service.dtos;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) acting as an application command to update an existing Course.
 *
 * <p>This record encapsulates the requested state changes for a course. The fields are treated as
 * optional for partial updates; omitting a value will retain the current state in the database.
 *
 * @param name the new name of the course, or {@code null} to leave unchanged
 * @param schoolId the new unique identifier of the school, or {@code null} to leave unchanged
 */
public record CourseUpdateCommand(String name, UUID schoolId) {}
