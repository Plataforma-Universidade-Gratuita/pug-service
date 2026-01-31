package com.pug.academic.presenter.dtos;

/**
 * Request DTO for updating an existing School. All fields are optional, as they may not be changed.
 *
 * @param name the new name of the school (optional).
 */
public record SchoolUpdateRequest(String name) {}
