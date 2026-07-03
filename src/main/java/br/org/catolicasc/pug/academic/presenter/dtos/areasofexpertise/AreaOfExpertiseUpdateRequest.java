/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise;

/**
 * Request DTO used as the JSON payload for updating an academic area of expertise.
 *
 * @param name the new area-of-expertise name, or {@code null} to leave unchanged
 */
public record AreaOfExpertiseUpdateRequest(String name) {}
