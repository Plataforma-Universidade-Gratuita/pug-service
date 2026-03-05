package com.pug.academic.presenter.dtos;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for partially updating an existing School.
 * <p>
 * Because updates can be partial, all fields in this record are inherently optional.
 * If a field is provided as {@code null} or omitted from the JSON payload, the application
 * service will ignore it and retain the existing value for that specific attribute in the database.
 *
 * @param name the new name of the academic school, or {@code null} to leave unchanged
 */
public record SchoolUpdateRequest(String name) {
}