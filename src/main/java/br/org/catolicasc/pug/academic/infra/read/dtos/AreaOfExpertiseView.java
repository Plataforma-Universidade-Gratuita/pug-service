package br.org.catolicasc.pug.academic.infra.read.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Lightweight read-model projection representing a single academic area of expertise.
 *
 * @param id the unique identifier of the area of expertise
 * @param name the public name of the area of expertise
 * @param createdAt the timestamp when the area of expertise was created
 * @param updatedAt the timestamp when the area of expertise was last updated
 */
public record AreaOfExpertiseView(
    UUID id, String name, OffsetDateTime createdAt, OffsetDateTime updatedAt) {}
