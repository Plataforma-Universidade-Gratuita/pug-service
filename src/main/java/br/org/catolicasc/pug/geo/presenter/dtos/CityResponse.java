/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.geo.presenter.dtos;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the standardized API JSON response for City data.
 *
 * <p>This record provides the client with the essential details of a city, including a computed
 * flag indicating whether the city is a protected system default (which affects whether the UI
 * should allow the user to edit or delete it).
 *
 * @param id the unique identifier (UUIDv7) of the city
 * @param name the name of the city
 * @param ibgeCode the 7-digit IBGE code of the city
 */
public record CityResponse(UUID id, String name, String ibgeCode) {}
