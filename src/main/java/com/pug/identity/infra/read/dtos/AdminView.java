package com.pug.identity.infra.read.dtos;

import java.time.OffsetDateTime;

/**
 * Data Transfer Object representing an admin view.
 *
 * @param userView the view of the user who is an admin
 * @param grantedAt the timestamp when admin privileges were granted
 */
public record AdminView(UserView userView, OffsetDateTime grantedAt) {}
