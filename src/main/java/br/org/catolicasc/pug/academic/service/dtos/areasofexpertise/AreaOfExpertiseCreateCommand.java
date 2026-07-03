/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.service.dtos.areasofexpertise;

/**
 * Service-layer command that carries the data required to create a new academic area of expertise.
 *
 * @param name the canonical display name that will be validated and persisted for the new area of
 *     expertise
 */
public record AreaOfExpertiseCreateCommand(String name) {}
