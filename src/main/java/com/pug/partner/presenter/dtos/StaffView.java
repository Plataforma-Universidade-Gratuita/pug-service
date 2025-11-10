package com.pug.partner.presenter.dtos;

import com.pug.shared.domain.enums.AccountType;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Projection for Staff: user info + entity view (with city). */
public record StaffView(
    UUID userId,
    String cpf,
    String name,
    String email,
    AccountType accountType,
    OffsetDateTime createdAt,
    EntityView entity) {}
