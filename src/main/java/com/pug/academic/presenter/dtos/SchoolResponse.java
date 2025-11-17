package com.pug.academic.presenter.dtos;

import java.util.UUID;

/**
 * Response DTO for School.
 *
 * @param id   the school ID
 * @param name the school name
 */
public record SchoolResponse(UUID id, String name) {
}
