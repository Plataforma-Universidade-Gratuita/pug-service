package com.pug.identity.presenter.dtos;

import java.time.OffsetDateTime;

/**
 * AdminResponse record.
 *
 * @param accountResponse    the account details associated with the admin (as an AccountResponse).
 * @param grantedAt          the date and time when admin privileges were granted.
 * @param grantedAtFormatted the formatted text for the granted date and time (localized).
 */
public record AdminResponse(
        AccountResponse accountResponse, OffsetDateTime grantedAt, String grantedAtFormatted) {
}