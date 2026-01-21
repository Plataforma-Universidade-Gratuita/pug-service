package com.pug.academic.presenter.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request DTO for creating a new Course.
 *
 * @param name     the name of the course.
 * @param schoolId the ID of the school this course belongs to.
 */
public record CourseCreateRequest(
        @NotBlank @Size(max = 120) String name,
        @NotNull UUID schoolId
) {
}