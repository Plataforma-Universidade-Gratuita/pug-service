package com.pug.identity.presenter.dtos;

import java.time.OffsetDateTime;

/**
 * AdminResponse record.
 *
 * @param accountResponse the user details associated with the admin
 * @param grantedAt the date and time when admin rights were granted
 * @param grantedAtLabel the label for the granted date and time
 */
public record AdminResponse(
        AccountResponse accountResponse, OffsetDateTime grantedAt, String grantedAtLabel) {}
