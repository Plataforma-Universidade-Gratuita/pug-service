package br.org.catolicasc.pug.partner.presenter.mappers;

import br.org.catolicasc.pug.identity.presenter.dtos.AccountComplexSearchResponse;
import br.org.catolicasc.pug.identity.presenter.mappers.AccountPresenter;
import br.org.catolicasc.pug.identity.service.dtos.AccountCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.AccountUpdateCommand;
import br.org.catolicasc.pug.identity.service.dtos.UserCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.UserUpdateCommand;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffComplexSearchView;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffView;
import br.org.catolicasc.pug.partner.presenter.dtos.StaffComplexSearchResponse;
import br.org.catolicasc.pug.partner.presenter.dtos.StaffCreateRequest;
import br.org.catolicasc.pug.partner.presenter.dtos.StaffResponse;
import br.org.catolicasc.pug.partner.presenter.dtos.StaffUpdateRequest;
import br.org.catolicasc.pug.partner.service.dtos.StaffCreateCommand;
import br.org.catolicasc.pug.partner.service.dtos.StaffUpdateCommand;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.i18n.I18n;
import java.util.Locale;

/**
 * Stateless utility class responsible for mapping internal staff projections to external API
 * responses and requests to commands.
 */
public final class StaffPresenter {

  private StaffPresenter() {}

  public static StaffCreateCommand toCommand(StaffCreateRequest req) {
    if (req == null) {
      return null;
    }
    UserCreateCommand userCmd = new UserCreateCommand(req.cpfString(), req.name());
    AccountCreateCommand accountCmd =
        new AccountCreateCommand(req.emailString(), AccountType.PARTNER, null, userCmd);
    return new StaffCreateCommand(req.entityId(), accountCmd);
  }

  public static StaffUpdateCommand toCommand(StaffUpdateRequest req) {
    if (req == null) {
      return null;
    }
    UserUpdateCommand userCmd = new UserUpdateCommand(req.name());
    AccountUpdateCommand accountCmd =
        new AccountUpdateCommand(req.emailString(), null, null, userCmd);
    return new StaffUpdateCommand(accountCmd, req.entityId());
  }

  /** Projects a read-only staff complex-search projection into a client-facing response item. */
  public static StaffComplexSearchResponse toComplexSearchResponse(
      StaffComplexSearchView view, Locale locale, I18n i18n) {
    if (view == null || locale == null || i18n == null) {
      return null;
    }

    AccountComplexSearchResponse account =
        AccountPresenter.toComplexSearchResponse(view.account(), locale, i18n);
    return new StaffComplexSearchResponse(
        account, EntityPresenter.toSimpleComplexSearchResponse(view.entity()));
  }

  public static StaffResponse toResponse(StaffView v, Locale locale, I18n i18n) {
    if (v == null || locale == null || i18n == null) {
      return null;
    }
    return new StaffResponse(
        AccountPresenter.toResponse(v.account(), locale, i18n), v.entityId(), v.cityId());
  }
}
