package com.pug.identity.infra.read.dtos;

import com.pug.shared.domain.enums.Campi;

import java.time.OffsetDateTime;

/**
 * Data Transfer Object representing an admin view.
 *
 * @param accountView the view of the account associated with the admin.
 * @param grantedAt the timestamp when admin privileges were granted.
 * @param campus the campus at which the admin comes from.
 */
public record AdminView(AccountView accountView, OffsetDateTime grantedAt, Campi campus) {}
