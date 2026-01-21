package com.pug.academic.service.dtos;

import java.util.UUID;

/**
 * Command DTO for updating an existing Course.
 *
 * @param name     the new name of the course (optional).
 * @param schoolId the new ID of the school this course belongs to (optional).
 */
public record CourseUpdateCommand(
        String name,
        UUID schoolId
) {
}