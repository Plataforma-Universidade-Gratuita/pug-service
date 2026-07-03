/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.presenter.dtos.accounts;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO used to update the activation status of an existing account.
 *
 * @param active the activation flag that should be applied to the targeted account
 */
public record AccountStatusRequest(@NotNull Boolean active) {}
