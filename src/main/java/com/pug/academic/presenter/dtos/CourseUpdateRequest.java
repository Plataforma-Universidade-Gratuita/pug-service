package com.pug.academic.presenter.dtos;

import java.util.UUID;

/**
 * Request DTO for updating an existing Course. All fields are optional, as they may not be changed.
 *
 * @param name     the new name of the course (optional).
 * @param schoolId the new ID of the school this course belongs to (optional).
 */
public record CourseUpdateRequest(String name, UUID schoolId) {
}
