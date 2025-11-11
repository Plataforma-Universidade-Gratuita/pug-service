package com.pug.identity.infra.read.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * PersonView DTO.
 *
 * @param id the unique identifier of the person
 * @param cpf the CPF (Cadastro de Pessoas Físicas) number of the person
 * @param name the name of the person
 * @param createdAt the timestamp when the person record was created
 */
public record UserView(
        UUID id,
        String cpf,
        String name,
        OffsetDateTime createdAt
){
}
