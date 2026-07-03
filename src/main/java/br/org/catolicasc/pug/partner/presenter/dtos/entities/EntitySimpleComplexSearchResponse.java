/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.partner.presenter.dtos.entities;

import java.util.UUID;

/**
 * Lightweight response DTO used by complex-search flows that only require basic partner-entity
 * identification data.
 *
 * @param id the unique identifier (UUIDv7) of the partner entity
 * @param name the registered name of the partner entity
 */
public record EntitySimpleComplexSearchResponse(UUID id, String name) {}
