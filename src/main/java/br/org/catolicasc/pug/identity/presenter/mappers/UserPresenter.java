/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.presenter.mappers;

import br.org.catolicasc.pug.identity.infra.read.dtos.UserView;
import br.org.catolicasc.pug.identity.presenter.dtos.users.UserResponse;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import br.org.catolicasc.pug.shared.presenter.mappers.SharedDataPresenter;
import java.util.Locale;

/**
 * Stateless utility class responsible for mapping internal user identity projections to external
 * API responses.
 *
 * <p>This presenter acts as a translation layer, converting raw CQRS query views ({@link UserView})
 * into client-ready representations ({@link UserResponse}). It encapsulates UI-specific formatting
 * logic, such as masking or formatting the Brazilian CPF.
 */
public final class UserPresenter {

  private UserPresenter() {}

  private static String cpfFormatted(String cpf) {
    if (cpf == null || cpf.length() != 11) {
      return cpf;
    }
    return cpf.substring(0, 3)
        + "."
        + cpf.substring(3, 6)
        + "."
        + cpf.substring(6, 9)
        + "-"
        + cpf.substring(9, 11);
  }

  /**
   * Projects a read-only {@link UserView} into a client-facing {@link UserResponse}.
   *
   * @param v the internal read-model projection of the user
   * @param locale the locale extracted from the client's request headers
   * @return a fully populated {@link UserResponse} ready for JSON serialization, or {@code null} if
   *     the input view is null
   */
  public static UserResponse toResponse(UserView v, Locale locale) {
    if (v == null) {
      return null;
    }

    AuditInfoResponse auditInfo =
        SharedDataPresenter.createAuditInfoResponse(v.createdAt(), v.updatedAt(), locale);

    return new UserResponse(v.id(), v.cpf(), cpfFormatted(v.cpf()), v.name(), auditInfo);
  }
}
