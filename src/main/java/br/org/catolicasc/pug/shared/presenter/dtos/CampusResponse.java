/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.shared.presenter.dtos;

import br.org.catolicasc.pug.shared.domain.enums.Campi;

/**
 * Data Transfer Object (DTO) representing an academic campus.
 *
 * <p>This record pairs the raw enum constant with its corresponding localized display name, making
 * it easy for front-end applications to build select dropdowns or display labels without handling
 * client-side translation dictionaries.
 *
 * @param campus the programmatic enum constant representing the campus
 * @param campusFormatted the human-readable, localized name of the campus (e.g., "Joinville")
 */
public record CampusResponse(Campi campus, String campusFormatted) {}
