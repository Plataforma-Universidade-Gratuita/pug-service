package com.pug.identity.infra.read.dtos;

import com.pug.shared.domain.enums.AccountType;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Data Transfer Object representing a user view. */
public record UserView(
    UUID id,
    String cpf,
    String name,
    String email,
    AccountType accountType,
    OffsetDateTime createdAt) {}
