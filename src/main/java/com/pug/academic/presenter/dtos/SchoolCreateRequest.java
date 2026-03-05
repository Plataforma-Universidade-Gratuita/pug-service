package com.pug.academic.presenter.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for creating a new Academic School.
 * <p>
 * This record applies Jakarta Bean Validation constraints to ensure the initial data
 * is structurally sound before it reaches the application service layer.
 *
 * @param name the raw name of the academic school or department (must not be blank and max 100 characters)
 */
public record SchoolCreateRequest(@NotBlank @Size(max = 100) String name) {
}