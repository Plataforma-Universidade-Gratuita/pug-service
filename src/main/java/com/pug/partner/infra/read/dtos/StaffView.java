package com.pug.partner.infra.read.dtos;

import com.pug.identity.infra.read.dtos.UserView;
import com.pug.shared.domain.enums.AccountType;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Projection for Staff: user info + entity view (with city). */
public record StaffView(
    UserView user,
    EntityView entity) {}
