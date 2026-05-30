package br.org.catolicasc.pug.identity.presenter.dtos.admins;

import br.org.catolicasc.pug.identity.presenter.dtos.accounts.AccountResponse;
import br.org.catolicasc.pug.shared.presenter.dtos.CampusResponse;
import java.time.OffsetDateTime;

/**
 * Data Transfer Object (DTO) used as the standardized API JSON response for Administrator profiles.
 *
 * <p>This record consolidates the administrator information for the presentation layer by combining
 * the flattened {@link AccountResponse} (which references the user via {@code userId}) with the
 * campus assignment and the timestamp indicating when administrative privileges were granted.
 *
 * @param accountResponse the consolidated details of the underlying account (including userId and
 *     audit info)
 * @param campus the formatted response object representing the admin's assigned campus
 * @param grantedAt the exact timestamp when administrative privileges were granted
 * @param grantedAtFormatted a localized, human-readable string representing the granted date
 */
public record AdminResponse(
    AccountResponse accountResponse,
    CampusResponse campus,
    OffsetDateTime grantedAt,
    String grantedAtFormatted) {}
