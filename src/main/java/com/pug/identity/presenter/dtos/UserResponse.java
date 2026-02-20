package com.pug.identity.presenter.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing the response for user-related operations.
 *
 * <p>This record encapsulates the essential information about a user, including their unique
 * identifier, CPF (Brazilian individual taxpayer registry identification), name, and timestamps for
 * creation and last update. The formatted versions of the CPF and timestamps are also included
 * for easier presentation in the API responses.
 *
 * @param id                 the unique identifier of the user
 * @param cpf                the CPF number of the user
 * @param cpfFormatted       the formatted CPF number for display purposes
 * @param name               the name of the user
 * @param createdAt          the timestamp when the user was created
 * @param createdAtFormatted the formatted creation timestamp for display purposes
 * @param updatedAt          the timestamp when the user was last updated
 * @param updatedAtFormatted the formatted last update timestamp for display purposes
 */
public record UserResponse(
        UUID id,
        String cpf,
        String cpfFormatted,
        String name,
        OffsetDateTime createdAt,
        String createdAtFormatted,
        OffsetDateTime updatedAt,
        String updatedAtFormatted) {
}
