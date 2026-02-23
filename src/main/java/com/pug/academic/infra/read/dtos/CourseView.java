package com.pug.academic.infra.read.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for Course read operations.
 *
 * @param id the course id
 * @param name the course name
 * @param school the school of the course
 * @param createdAt the course creation time
 * @param updatedAt the course update time
 */
public record CourseView(
    UUID id, String name, SchoolView school, OffsetDateTime createdAt, OffsetDateTime updatedAt) {}
