/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.infra.read.dtos;

import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.time.OffsetDateTime;

/**
 * Read-only projection used by the administrator complex-search flow.
 *
 * <p>This projection keeps the account-search payload nested as a lightweight account view while
 * exposing administrator-specific data required by the frontend filtering contract.
 *
 * @param accountView the lightweight account projection associated with the administrator
 * @param campus the campus where the administrator has administrative privileges
 * @param grantedAt the timestamp when administrative privileges were granted
 */
public record AdminComplexSearchView(
    AccountComplexSearchView accountView, Campi campus, OffsetDateTime grantedAt) {}
