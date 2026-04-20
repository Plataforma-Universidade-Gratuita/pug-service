package br.org.catolicasc.pug.identity.infra.read.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a read-only view of a User.
 *
 * <p>Following CQRS principles, this record is used exclusively for returning queried data to the
 * client. It flattens the internal domain structure (e.g., extracting the raw string from the
 * {@code Cpf} value object) to provide a simple, lightweight structure optimized for JSON
 * serialization.
 *
 * @param id the unique identifier (UUIDv7) of the user
 * @param cpf the exact 11-digit Brazilian CPF string of the user
 * @param name the full name of the user
 * @param createdAt the exact timestamp when the user record was initially created
 * @param updatedAt the exact timestamp when the user record was last modified
 */
public record UserView(
    UUID id, String cpf, String name, OffsetDateTime createdAt, OffsetDateTime updatedAt) {}
