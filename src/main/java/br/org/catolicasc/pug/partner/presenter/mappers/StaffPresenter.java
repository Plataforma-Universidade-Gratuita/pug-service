package br.org.catolicasc.pug.partner.presenter.mappers;

import br.org.catolicasc.pug.identity.presenter.dtos.accounts.AccountComplexSearchResponse;
import br.org.catolicasc.pug.identity.presenter.mappers.AccountPresenter;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountUpdateCommand;
import br.org.catolicasc.pug.identity.service.dtos.users.UserCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.users.UserUpdateCommand;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffComplexSearchView;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffView;
import br.org.catolicasc.pug.partner.presenter.dtos.staff.StaffComplexSearchResponse;
import br.org.catolicasc.pug.partner.presenter.dtos.staff.StaffCreateRequest;
import br.org.catolicasc.pug.partner.presenter.dtos.staff.StaffResponse;
import br.org.catolicasc.pug.partner.presenter.dtos.staff.StaffUpdateRequest;
import br.org.catolicasc.pug.partner.service.dtos.staff.StaffCreateCommand;
import br.org.catolicasc.pug.partner.service.dtos.staff.StaffUpdateCommand;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.i18n.I18n;
import java.util.Locale;

/**
 * Stateless utility class responsible for mapping internal staff projections to external API
 * responses and requests to commands.
 */
public final class StaffPresenter {

  private StaffPresenter() {}

  /**
   * Maps the staff-creation request payload to a service-layer command tree.
   *
   * @param req the incoming presenter-layer request
   * @return the mapped service command, or {@code null} when the request is {@code null}
   */
  public static StaffCreateCommand toCommand(StaffCreateRequest req) {
    if (req == null) {
      return null;
    }
    UserCreateCommand userCmd = new UserCreateCommand(req.cpfString(), req.name());
    AccountCreateCommand accountCmd =
        new AccountCreateCommand(req.emailString(), AccountType.PARTNER, null, userCmd);
    return new StaffCreateCommand(req.entityId(), accountCmd);
  }

  /**
   * Maps the staff-update request payload to a service-layer command tree.
   *
   * @param req the incoming presenter-layer request
   * @return the mapped service command, or {@code null} when the request is {@code null}
   */
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

  /**
   * Maps a standard staff read projection to the canonical API response.
   *
   * @param v the read-side projection
   * @param locale the locale used to format localized nested data
   * @param i18n the translation helper used by nested presenters
   * @return the mapped response, or {@code null} when any required input is {@code null}
   */
  public static StaffResponse toResponse(StaffView v, Locale locale, I18n i18n) {
    if (v == null || locale == null || i18n == null) {
      return null;
    }
    return new StaffResponse(
        AccountPresenter.toResponse(v.account(), locale, i18n), v.entityId(), v.cityId());
  }
}
