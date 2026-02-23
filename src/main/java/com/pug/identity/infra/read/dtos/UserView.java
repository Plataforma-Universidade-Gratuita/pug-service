package com.pug.identity.infra.read.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * UserView DTO.
 *
 * @param id the unique identifier of the account
 * @param cpf the CPF (Cadastro de Pessoas Físicas) number of the account
 * @param name the name of the account
 * @param createdAt the timestamp when the account record was created
 * @param updatedAt the timestamp when the account record was last updated
 */
public record UserView(
    UUID id, String cpf, String name, OffsetDateTime createdAt, OffsetDateTime updatedAt) {}
