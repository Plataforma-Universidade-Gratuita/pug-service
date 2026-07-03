/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.presenter.dtos.formerstudents;

import br.org.catolicasc.pug.identity.presenter.dtos.accounts.AccountSimpleComplexSearchResponse;
import br.org.catolicasc.pug.shared.presenter.dtos.CampusResponse;

/**
 * Lightweight former-student response used in nested complex-search payloads.
 *
 * @param account simplified account projection
 * @param academicRegistration academic registration identifier
 * @param campus localized campus projection
 */
public record FormerStudentSimpleComplexSearchResponse(
    AccountSimpleComplexSearchResponse account,
    String academicRegistration,
    CampusResponse campus) {}
