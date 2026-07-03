/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.presenter.dtos.users;

import java.util.UUID;

/**
 * Lightweight response DTO used by identity complex-search flows that only require basic user
 * identity information.
 *
 * @param id the unique identifier (UUIDv7) of the user
 * @param name the full name of the user
 */
public record UserSimpleComplexSearchResponse(UUID id, String name) {}
