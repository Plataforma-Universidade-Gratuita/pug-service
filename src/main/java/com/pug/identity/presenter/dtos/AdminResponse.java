package com.pug.identity.presenter.dtos;

import com.pug.shared.presenter.dtos.CampusResponse;

import java.time.OffsetDateTime;

/**
 * AdminResponse record.
 *
 * @param accountResponse    the account details associated with the admin (as an AccountResponse).
 * @param campus             the campus associated with the admin (as a CampusResponse).
 * @param grantedAt          the date and time when admin privileges were granted.
 * @param grantedAtFormatted the formatted text for the granted date and time (localized).
 */
public record AdminResponse(
        AccountResponse accountResponse, CampusResponse campus, OffsetDateTime grantedAt,
        String grantedAtFormatted) {
}
