/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.geo.presenter.mappers;

import br.org.catolicasc.pug.geo.infra.read.dtos.CityView;
import br.org.catolicasc.pug.geo.presenter.dtos.CityResponse;
import br.org.catolicasc.pug.shared.domain.enums.Campi;

/**
 * Stateless utility class responsible for mapping internal geographic projections to external API
 * responses.
 *
 * <p>This presenter acts as a translation layer, converting raw CQRS query views ({@link CityView})
 * into client-ready representations ({@link CityResponse}). It is responsible for injecting
 * presentation-specific computed logic, such as determining if a city is a protected default
 * record.
 */
public final class CityPresenter {

  private CityPresenter() {}

  /**
   * Projects a read-only {@link CityView} into a client-facing {@link CityResponse}.
   *
   * <p>This mapping explicitly evaluates the city's IBGE code against the system's protected {@link
   * Campi} defaults to dynamically compute the {@code isDefault} flag.
   *
   * @param v the internal read-model projection of the city
   * @return a fully populated {@link CityResponse} ready for JSON serialization, or {@code null} if
   *     the input view is null
   */
  public static CityResponse toResponse(CityView v) {
    if (v == null) {
      return null;
    }
    return new CityResponse(v.id(), v.name(), v.ibgeCode());
  }
}
