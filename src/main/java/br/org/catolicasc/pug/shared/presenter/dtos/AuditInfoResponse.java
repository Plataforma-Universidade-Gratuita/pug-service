package br.org.catolicasc.pug.shared.presenter.dtos;

import java.time.OffsetDateTime;

/**
 * Data Transfer Object (DTO) representing the standard audit trail of a domain entity.
 *
 * <p>This record provides both the raw ISO-8601 timestamps (useful for programmatic parsing by the
 * client) and pre-formatted, localized strings (ready for direct UI display).
 *
 * @param createdAt the exact date and time the entity was initially persisted
 * @param createdAtFormatted the human-readable, localized string representation of the creation
 *     date
 * @param updatedAt the exact date and time the entity was last modified
 * @param updatedAtFormatted the human-readable, localized string representation of the update date
 */
public record AuditInfoResponse(
    OffsetDateTime createdAt,
    String createdAtFormatted,
    OffsetDateTime updatedAt,
    String updatedAtFormatted) {}
