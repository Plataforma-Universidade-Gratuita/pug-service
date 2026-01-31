package com.pug.identity.presenter.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response DTO for a user.
 *
 * @param id the user ID
 * @param cpf the CPF (Cadastro de Pessoas Físicas) of the user
 * @param cpfFormatted the CPF formatted as XXX.XXX.XXX-XX
 * @param name the name of the user
 * @param createdAt the creation date and time
 * @param createdAtFormatted the formatted creation date and time
 */
public record UserResponse(
    UUID id,
    String cpf,
    String cpfFormatted,
    String name,
    OffsetDateTime createdAt,
    String createdAtFormatted) {}
