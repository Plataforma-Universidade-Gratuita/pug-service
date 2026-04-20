package br.org.catolicasc.pug.academic.infra.read.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a read-only view of an Academic School.
 *
 * <p>Following CQRS principles, this record is used exclusively for returning queried data to the
 * client. It provides a simple, lightweight structure optimized for JSON serialization.
 *
 * @param id the unique identifier (UUIDv7) of the school
 * @param name the name of the academic school
 * @param createdAt the exact timestamp when the school record was initially created
 * @param updatedAt the exact timestamp when the school record was last modified
 */
public record SchoolView(
    UUID id, String name, OffsetDateTime createdAt, OffsetDateTime updatedAt) {}
