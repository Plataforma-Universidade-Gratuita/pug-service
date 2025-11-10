package com.pug.identity.presenter.dtos;

import java.time.OffsetDateTime;

/**
 * AdminResponse record.
 *
 * @param userResponse the user details associated with the admin
 * @param grantedAt the date and time when admin rights were granted
 * @param grantedAtLabel the label for the granted date and time
 */
public record AdminResponse(
    UserResponse userResponse, OffsetDateTime grantedAt, String grantedAtLabel) {}
