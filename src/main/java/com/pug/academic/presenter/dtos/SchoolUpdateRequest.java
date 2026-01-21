package com.pug.academic.presenter.dtos;

import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating an existing School. All fields are optional, as they may not be changed.
 *
 * @param name the new name of the school (optional).
 */
public record SchoolUpdateRequest(@Size(max = 100) String name) {}
