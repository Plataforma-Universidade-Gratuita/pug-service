package com.pug.identity.presenter.dtos;

import com.pug.shared.domain.enums.AccountType;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Projection for Admin + User data. */
public record AdminView(
    UUID userId,
    String cpf,
    String name,
    String email,
    AccountType accountType,
    OffsetDateTime createdAt,
    OffsetDateTime grantedAt) {}
