package br.org.catolicasc.pug.identity.presenter.mappers;

import br.org.catolicasc.pug.identity.infra.read.dtos.AdminComplexSearchView;
import br.org.catolicasc.pug.identity.infra.read.dtos.AdminView;
import br.org.catolicasc.pug.identity.presenter.dtos.accounts.AccountComplexSearchResponse;
import br.org.catolicasc.pug.identity.presenter.dtos.accounts.AccountResponse;
import br.org.catolicasc.pug.identity.presenter.dtos.admins.AdminComplexSearchResponse;
import br.org.catolicasc.pug.identity.presenter.dtos.admins.AdminCreateRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.admins.AdminResponse;
import br.org.catolicasc.pug.identity.presenter.dtos.admins.AdminUpdateRequest;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountUpdateCommand;
import br.org.catolicasc.pug.identity.service.dtos.admins.AdminCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.admins.AdminUpdateCommand;
import br.org.catolicasc.pug.identity.service.dtos.users.UserCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.users.UserUpdateCommand;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.dtos.CampusResponse;
import br.org.catolicasc.pug.shared.presenter.mappers.SharedDataPresenter;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import java.util.Locale;

/**
 * Stateless utility class responsible for mapping internal administrator projections to external
 * API responses and mapping REST payloads to application commands.
 *
 * <p>This presenter acts as a translation layer, converting deeply nested CQRS query views ({@link
 * AdminView}) into consolidated, client-ready representations ({@link AdminResponse}), and mapping
 * incoming data transfers to nested command structures.
 */
public final class AdminPresenter {

  private AdminPresenter() {}

  /**
   * Maps an incoming REST creation request into an application layer creation command.
   *
   * @param req the validated {@link AdminCreateRequest} payload
   * @return the corresponding {@link AdminCreateCommand}, or {@code null} if input is null
   */
  public static AdminCreateCommand toCommand(AdminCreateRequest req) {
    if (req == null) {
      return null;
    }
    var userCmd = new UserCreateCommand(req.cpf(), req.name());
    var accountCmd = new AccountCreateCommand(req.email(), AccountType.ADMIN, null, userCmd);
    return new AdminCreateCommand(accountCmd, req.campus());
  }

  /**
   * Maps an incoming REST update request into an application layer update command.
   *
   * @param req the validated {@link AdminUpdateRequest} payload
   * @return the corresponding {@link AdminUpdateCommand}, or {@code null} if input is null
   */
  public static AdminUpdateCommand toCommand(AdminUpdateRequest req) {
    if (req == null) {
      return null;
    }
    var userCmd = new UserUpdateCommand(req.name());
    var accountCmd = new AccountUpdateCommand(req.email(), null, null, userCmd);
    return new AdminUpdateCommand(accountCmd, req.campus());
  }

  /**
   * Projects a read-only {@link AdminComplexSearchView} into a client-facing {@link
   * AdminComplexSearchResponse}.
   *
   * <p>This mapping reuses the account complex-search presenter so the shared account payload keeps
   * the same shape across identity endpoints while the admin response adds the privilege-grant
   * timestamp required by the administrator filtering flow.
   *
   * @param v the internal read-model projection of the administrator search result
   * @param locale the locale extracted from the client's request headers
   * @param i18n the internationalization service for resolving bundle keys
   * @return a fully populated {@link AdminComplexSearchResponse} ready for JSON serialization, or
   *     {@code null} if any required input is null
   */
  public static AdminComplexSearchResponse toComplexSearchResponse(
      AdminComplexSearchView v, Locale locale, I18n i18n) {
    if (v == null || locale == null || i18n == null) {
      return null;
    }

    AccountComplexSearchResponse account =
        AccountPresenter.toComplexSearchResponse(v.accountView(), locale, i18n);
    CampusResponse campusResponse =
        SharedDataPresenter.createCampusResponse(v.campus(), locale, i18n);
    String grantedAtFormatted = StringUtils.toStringFormatted(v.grantedAt(), locale);

    return new AdminComplexSearchResponse(
        account, campusResponse, v.grantedAt(), grantedAtFormatted);
  }

  /**
   * Projects a read-only {@link AdminView} into a client-facing {@link AdminResponse}.
   *
   * <p>This mapping delegates the formatting of the underlying account and user data to the {@link
   * AccountPresenter}, while directly formatting the admin-specific fields (such as the localized
   * date of the privilege grant and the resolved campus).
   *
   * @param a the internal read-model projection of the administrator
   * @param locale the locale extracted from the client's request headers
   * @param i18n the internationalization service for resolving bundle keys
   * @return a fully populated {@link AdminResponse} ready for JSON serialization, or {@code null}
   *     if any required input is null
   */
  public static AdminResponse toResponse(AdminView a, Locale locale, I18n i18n) {
    if (a == null || locale == null || i18n == null) {
      return null;
    }

    AccountResponse accountResponse = AccountPresenter.toResponse(a.accountView(), locale, i18n);
    CampusResponse campus = SharedDataPresenter.createCampusResponse(a.campus(), locale, i18n);
    String grantedAtFormatted = StringUtils.toStringFormatted(a.grantedAt(), locale);

    return new AdminResponse(accountResponse, campus, a.grantedAt(), grantedAtFormatted);
  }
}
