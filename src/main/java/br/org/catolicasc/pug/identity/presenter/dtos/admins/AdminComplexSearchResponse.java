/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.presenter.dtos.admins;

import br.org.catolicasc.pug.identity.presenter.dtos.accounts.AccountComplexSearchResponse;
import br.org.catolicasc.pug.shared.presenter.dtos.CampusResponse;
import java.time.OffsetDateTime;

/**
 * Response DTO used as the content item returned by the administrator complex-search endpoint.
 *
 * @param account the lightweight account projection associated with the administrator
 * @param grantedAt the exact timestamp when administrative privileges were granted
 * @param grantedAtFormatted a localized, human-readable representation of {@code grantedAt}
 */
public record AdminComplexSearchResponse(
    AccountComplexSearchResponse account,
    CampusResponse campus,
    OffsetDateTime grantedAt,
    String grantedAtFormatted) {}
