package br.org.catolicasc.pug.academic.presenter.mappers;

import br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView;
import br.org.catolicasc.pug.academic.presenter.dtos.AreaOfExpertiseComplexSearchResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.AreaOfExpertiseCreateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.AreaOfExpertiseResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.AreaOfExpertiseUpdateRequest;
import br.org.catolicasc.pug.academic.service.dtos.AreaOfExpertiseCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.AreaOfExpertiseUpdateCommand;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import br.org.catolicasc.pug.shared.presenter.mappers.SharedDataPresenter;
import java.util.Locale;

/**
 * Stateless mapper responsible for translating academic area-of-expertise requests and projections.
 */
public final class AreaOfExpertisePresenter {
  private AreaOfExpertisePresenter() {}

  public static AreaOfExpertiseCreateCommand toCommand(AreaOfExpertiseCreateRequest req) {
    if (req == null) {
      return null;
    }
    return new AreaOfExpertiseCreateCommand(req.name());
  }

  public static AreaOfExpertiseUpdateCommand toCommand(AreaOfExpertiseUpdateRequest req) {
    if (req == null) {
      return null;
    }
    return new AreaOfExpertiseUpdateCommand(req.name());
  }

  public static AreaOfExpertiseResponse toResponse(SchoolView view, Locale locale) {
    if (view == null || locale == null) {
      return null;
    }

    AuditInfoResponse auditInfo =
        SharedDataPresenter.createAuditInfoResponse(view.createdAt(), view.updatedAt(), locale);
    return new AreaOfExpertiseResponse(view.id(), view.name(), auditInfo);
  }

  public static AreaOfExpertiseComplexSearchResponse toComplexSearchResponse(SchoolView view) {
    if (view == null) {
      return null;
    }
    return new AreaOfExpertiseComplexSearchResponse(view.id(), view.name());
  }
}
