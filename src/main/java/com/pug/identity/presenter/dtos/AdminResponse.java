package com.pug.identity.presenter.dtos;

import java.time.OffsetDateTime;

/**
 * AdminResponse record.
 *
 * @param accountResponse the account details associated with the admin
 * @param grantedAt the date and time when admin rights were granted
 * @param grantedAtFormatted the label for the granted date and time
 */
public record AdminResponse(
    AccountResponse accountResponse, OffsetDateTime grantedAt, String grantedAtFormatted) {}
