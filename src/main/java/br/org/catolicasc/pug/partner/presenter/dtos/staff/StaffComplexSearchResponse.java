/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.partner.presenter.dtos.staff;

import br.org.catolicasc.pug.identity.presenter.dtos.accounts.AccountComplexSearchResponse;
import br.org.catolicasc.pug.partner.presenter.dtos.entities.EntitySimpleComplexSearchResponse;

/**
 * Response DTO used as the content item returned by the staff complex-search endpoint.
 *
 * @param account the lightweight account projection associated with the staff member
 * @param entity the lightweight partner-entity projection associated with the staff member
 */
public record StaffComplexSearchResponse(
    AccountComplexSearchResponse account, EntitySimpleComplexSearchResponse entity) {}
