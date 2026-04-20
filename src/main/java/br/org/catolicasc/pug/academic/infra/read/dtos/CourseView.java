package br.org.catolicasc.pug.academic.infra.read.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a read-only view of an Academic Course.
 *
 * <p>Following CQRS principles, this record is used exclusively for returning queried data to the
 * client. It flattens the internal domain structure and aggregates the associated {@link
 * SchoolView} to provide a lightweight structure optimized for JSON serialization.
 *
 * @param id the unique identifier (UUIDv7) of the course
 * @param name the name of the academic course
 * @param school the nested read-only projection of the school that offers this course
 * @param createdAt the exact timestamp when the course record was initially created
 * @param updatedAt the exact timestamp when the course record was last modified
 */
public record CourseView(
    UUID id, String name, SchoolView school, OffsetDateTime createdAt, OffsetDateTime updatedAt) {}
