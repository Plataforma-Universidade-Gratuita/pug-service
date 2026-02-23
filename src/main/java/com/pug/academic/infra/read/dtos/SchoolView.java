package com.pug.academic.infra.read.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for School read operations.
 *
 * @param id the school id
 * @param name the school name
 * @param createdAt the school creation time
 * @param updatedAt the school update time
 */
public record SchoolView(
    UUID id, String name, OffsetDateTime createdAt, OffsetDateTime updatedAt) {}
