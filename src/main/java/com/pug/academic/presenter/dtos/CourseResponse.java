package com.pug.academic.presenter.dtos;

import com.pug.shared.presenter.dtos.AuditInfoResponse;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the standardized API JSON response for Course data.
 * <p>
 * This record consolidates the backend course data along with a nested representation
 * of its parent School into a single, flattened response optimized for the presentation layer.
 *
 * @param id        the unique identifier (UUIDv7) of the academic course
 * @param name      the name of the academic course
 * @param school    the nested, client-facing projection of the school that offers this course
 * @param auditInfo the nested audit information containing creation and update timestamps
 */
public record CourseResponse(
        UUID id,
        String name,
        SchoolResponse school,
        AuditInfoResponse auditInfo) {
}