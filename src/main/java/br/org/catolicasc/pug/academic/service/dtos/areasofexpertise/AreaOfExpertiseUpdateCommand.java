/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.service.dtos.areasofexpertise;

/**
 * Service-layer command that carries the mutable data of an existing academic area of expertise.
 *
 * @param name the replacement display name to be validated and persisted
 */
public record AreaOfExpertiseUpdateCommand(String name) {}
