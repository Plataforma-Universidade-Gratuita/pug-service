package com.pug.academic.presenter.dtos;

import com.pug.shared.validation.UuidV7;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * DTO for creating a new course.
 *
 * @param name the name of the course
 * @param schoolId the unique identifier of the associated school
 */
public record CourseCreateOrUpdateRequest(
    @NotBlank @Size(max = 120) String name, @NotNull @UuidV7 UUID schoolId) {}
