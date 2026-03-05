package com.pug.identity.presenter.dtos;

import com.pug.shared.presenter.dtos.CampusResponse;
import java.time.OffsetDateTime;

/**
 * Data Transfer Object (DTO) used as the standardized API JSON response for Administrator profiles.
 *
 * <p>This record consolidates the deeply nested backend structure (Admin -> Account -> User) into a
 * single, flattened response optimized for the presentation layer.
 *
 * @param accountResponse the consolidated details of the underlying account and user
 * @param campus the formatted response object representing the admin's assigned campus
 * @param grantedAt the exact timestamp when administrative privileges were granted
 * @param grantedAtFormatted a localized, human-readable string representing the granted date
 */
public record AdminResponse(
    AccountResponse accountResponse,
    CampusResponse campus,
    OffsetDateTime grantedAt,
    String grantedAtFormatted) {}
