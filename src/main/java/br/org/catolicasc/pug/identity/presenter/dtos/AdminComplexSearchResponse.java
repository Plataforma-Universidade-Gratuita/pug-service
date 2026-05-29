package br.org.catolicasc.pug.identity.presenter.dtos;

import java.time.OffsetDateTime;

/**
 * Response DTO used as the content item returned by the administrator complex-search endpoint.
 *
 * @param account the lightweight account projection associated with the administrator
 * @param grantedAt the exact timestamp when administrative privileges were granted
 * @param grantedAtFormatted a localized, human-readable representation of {@code grantedAt}
 */
public record AdminComplexSearchResponse(
    AccountComplexSearchResponse account, OffsetDateTime grantedAt, String grantedAtFormatted) {}
