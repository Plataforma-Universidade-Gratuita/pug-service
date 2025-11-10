package com.pug.identity.presenter.dtos;

import com.pug.shared.domain.enums.AccountType;
import java.time.OffsetDateTime;
import java.util.UUID;

/** API response for Admin with localized account type. */
public record AdminResponse(
    UUID userId,
    String cpf,
    String name,
    String email,
    AccountType accountType,
    String accountTypeLabel,
    OffsetDateTime createdAt,
    OffsetDateTime grantedAt) {}
