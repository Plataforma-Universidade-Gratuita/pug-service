/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.geo.presenter.dtos;

import jakarta.validation.constraints.Size;

/**
 * Request DTO used in paginated complex-search operations for cities.
 *
 * @param name the city name filter. Must not exceed 100 characters.
 */
public record CityComplexSearchRequest(@Size(max = 100) String name) {}
