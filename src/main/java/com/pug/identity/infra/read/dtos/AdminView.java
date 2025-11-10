package com.pug.identity.infra.read.dtos;

import java.time.OffsetDateTime;

/** Data Transfer Object representing an admin view. */
public record AdminView(UserView userView, OffsetDateTime grantedAt) {}
