/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.presenter.mappers;

import br.org.catolicasc.pug.academic.infra.read.dtos.AreaOfExpertiseView;
import br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise.AreaOfExpertiseComplexSearchResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise.AreaOfExpertiseCreateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise.AreaOfExpertiseResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise.AreaOfExpertiseUpdateRequest;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseUpdateCommand;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import br.org.catolicasc.pug.shared.presenter.mappers.SharedDataPresenter;
import java.util.Locale;

/**
 * Stateless mapper responsible for translating academic area-of-expertise requests and projections.
 */
public final class AreaOfExpertisePresenter {

  private AreaOfExpertisePresenter() {}

  /**
   * Converts an area-of-expertise creation request into its command counterpart.
   *
   * @param req incoming presenter-layer payload
   * @return command representation, or {@code null} when the request is null
   */
  public static AreaOfExpertiseCreateCommand toCommand(AreaOfExpertiseCreateRequest req) {
    if (req == null) {
      return null;
    }
    return new AreaOfExpertiseCreateCommand(req.name());
  }

  /**
   * Converts an area-of-expertise update request into its command counterpart.
   *
   * @param req incoming presenter-layer payload
   * @return command representation, or {@code null} when the request is null
   */
  public static AreaOfExpertiseUpdateCommand toCommand(AreaOfExpertiseUpdateRequest req) {
    if (req == null) {
      return null;
    }
    return new AreaOfExpertiseUpdateCommand(req.name());
  }

  /**
   * Converts a read projection into the standard area-of-expertise response.
   *
   * @param view read projection returned by the query layer
   * @param locale locale used to build localized nested values
   * @return presenter response, or {@code null} when any required input is null
   */
  public static AreaOfExpertiseResponse toResponse(AreaOfExpertiseView view, Locale locale) {
    if (view == null || locale == null) {
      return null;
    }

    AuditInfoResponse auditInfo =
        SharedDataPresenter.createAuditInfoResponse(view.createdAt(), view.updatedAt(), locale);
    return new AreaOfExpertiseResponse(view.id(), view.name(), auditInfo);
  }

  /**
   * Converts a read projection into the lightweight complex-search response shape.
   *
   * @param view read projection returned by the query layer
   * @return lightweight complex-search response, or {@code null} when the projection is null
   */
  public static AreaOfExpertiseComplexSearchResponse toComplexSearchResponse(
      AreaOfExpertiseView view) {
    if (view == null) {
      return null;
    }
    return new AreaOfExpertiseComplexSearchResponse(view.id(), view.name());
  }
}
