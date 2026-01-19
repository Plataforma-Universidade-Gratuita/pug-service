package com.pug.academic.presenter.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * DTO for creating a new course.
 *
 * @param name the name of the course
 * @param schoolName the name of the associated school
 */
public record CourseCreateBulkRequest(@NotNull List<String> name, @NotBlank String schoolName) {}
