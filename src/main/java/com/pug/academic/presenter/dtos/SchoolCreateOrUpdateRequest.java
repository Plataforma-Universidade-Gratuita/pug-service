package com.pug.academic.presenter.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for creating a new School.
 *
 * @param name the name of the school
 */
public record SchoolCreateOrUpdateRequest(@NotBlank @Size(max = 100) String name) {}
