/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.partner.infra.read.dtos;

import br.org.catolicasc.pug.identity.infra.read.dtos.AccountComplexSearchView;

/**
 * Read-only projection used by the staff complex-search flow.
 *
 * @param account the lightweight account projection associated with the staff member
 * @param entity the lightweight partner-entity projection associated with the staff member
 */
public record StaffComplexSearchView(
    AccountComplexSearchView account, EntityComplexSearchView entity) {}
