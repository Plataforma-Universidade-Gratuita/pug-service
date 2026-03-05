package com.pug.partner.presenter.mappers;

import com.pug.identity.presenter.mappers.AccountPresenter;
import com.pug.identity.service.dtos.AccountCreateCommand;
import com.pug.identity.service.dtos.AccountUpdateCommand;
import com.pug.identity.service.dtos.UserCreateCommand;
import com.pug.identity.service.dtos.UserUpdateCommand;
import com.pug.partner.infra.read.dtos.StaffView;
import com.pug.partner.presenter.dtos.StaffCreateRequest;
import com.pug.partner.presenter.dtos.StaffResponse;
import com.pug.partner.presenter.dtos.StaffUpdateRequest;
import com.pug.partner.service.dtos.StaffCreateCommand;
import com.pug.partner.service.dtos.StaffUpdateCommand;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.i18n.I18n;
import java.util.Locale;

/**
 * Stateless utility class responsible for mapping internal staff projections to external API
 * responses and requests to commands.
 *
 * <p>This presenter acts as a translation layer, converting incoming REST payloads into application
 * commands, and deeply nested CQRS query views ({@link StaffView}) into consolidated, client-ready
 * representations ({@link StaffResponse}).
 */
public final class StaffPresenter {

  /** Private constructor to prevent instantiation of utility class. */
  private StaffPresenter() {}

  /**
   * Maps an incoming REST creation request into an application layer creation command.
   *
   * @param req the validated {@link StaffCreateRequest} payload
   * @param hashedPassword the securely hashed password string to assign to the new account
   * @return the corresponding {@link StaffCreateCommand}, or {@code null} if input is null
   */
  public static StaffCreateCommand toCommand(StaffCreateRequest req, String hashedPassword) {
    if (req == null) {
      return null;
    }
    var userCmd = new UserCreateCommand(req.cpfString(), req.name());
    var accountCmd =
        new AccountCreateCommand(req.emailString(), AccountType.PARTNER, hashedPassword, userCmd);
    return new StaffCreateCommand(req.entityId(), accountCmd);
  }

  /**
   * Maps an incoming REST update request into an application layer update command.
   *
   * @param req the validated {@link StaffUpdateRequest} payload
   * @param hashedPassword the securely hashed password string, or {@code null} if the password is
   *     not being updated
   * @return the corresponding {@link StaffUpdateCommand}, or {@code null} if input is null
   */
  public static StaffUpdateCommand toCommand(StaffUpdateRequest req, String hashedPassword) {
    if (req == null) {
      return null;
    }
    var userCmd = new UserUpdateCommand(req.name());
    var accountCmd = new AccountUpdateCommand(req.emailString(), hashedPassword, userCmd);
    return new StaffUpdateCommand(accountCmd);
  }

  /**
   * Projects a read-only {@link StaffView} into a client-facing {@link StaffResponse}.
   *
   * <p>This mapping delegates the formatting of the underlying account and user data to the
   * Identity domain's {@link AccountPresenter}, and delegates the formatting of the organizational
   * details to the {@link EntityPresenter}.
   *
   * @param v the internal read-model projection of the staff member
   * @param locale the locale extracted from the client's request headers
   * @param i18n the internationalization service for resolving bundle keys
   * @return a fully populated {@link StaffResponse} ready for JSON serialization, or {@code null}
   *     if any required input is null
   */
  public static StaffResponse toResponse(StaffView v, Locale locale, I18n i18n) {
    if (v == null || locale == null || i18n == null) {
      return null;
    }
    return new StaffResponse(
        AccountPresenter.toResponse(v.account(), locale, i18n),
        EntityPresenter.toResponse(v.entity(), locale));
  }
}
