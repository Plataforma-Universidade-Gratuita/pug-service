package com.pug.shared.presenter.dtos;

import java.time.OffsetDateTime;

/**
 * AuditInfoResponse is a record that represents the audit information for an entity, including
 * creation and update timestamps.
 *
 * @param createdAt the date and time when the entity was created.
 * @param createdAtFormatted the formatted text for the creation date and time (localized).
 * @param updatedAt the date and time when the entity was last updated.
 * @param updatedAtFormatted the formatted text for the last update date and time (localized).
 */
public record AuditInfoResponse(
    OffsetDateTime createdAt,
    String createdAtFormatted,
    OffsetDateTime updatedAt,
    String updatedAtFormatted) {}
