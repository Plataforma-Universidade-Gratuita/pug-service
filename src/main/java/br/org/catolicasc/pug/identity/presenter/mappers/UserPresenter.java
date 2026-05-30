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

  /** Private constructor to prevent instantiation of utility class. */
  private UserPresenter() {}

  /**
   * Computes the formatted string representation of a Brazilian CPF.
   *
   * <p>Transforms a raw 11-digit numeric string (e.g., "12345678900") into the standard punctuated
   * format (e.g., "123.456.789-00"). If the input is null or not exactly 11 characters, the raw
   * value is returned safely to prevent exceptions.
   *
   * @param cpf the raw numeric CPF string
   * @return the punctuated CPF string, or the raw input if formatting is not possible
   */
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
