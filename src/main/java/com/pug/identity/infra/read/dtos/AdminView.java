package com.pug.identity.infra.read.dtos;

import java.time.OffsetDateTime;

/**
 * Data Transfer Object representing an admin view.
 *
 * @param accountView the view of the account associated with the admin.
 * @param grantedAt the timestamp when admin privileges were granted.
 */
public record AdminView(AccountView accountView, OffsetDateTime grantedAt) {}
