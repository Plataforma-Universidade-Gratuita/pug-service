package com.pug.identity.presenter.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * PersonResponse DTO.
 *
 * @param id the person ID
 * @param cpf the cpf of the person
 * @param cpfFormatted the cpf formatted XXX.XXX.XXX-XX
 * @param name the name of the person
 * @param createdAt the creation data and time
 * @param createdAtFormatted the formatted creation date and time
 */
public record UserResponse(
    UUID id,
    String cpf,
    String cpfFormatted,
    String name,
    OffsetDateTime createdAt,
    String createdAtFormatted) {}
