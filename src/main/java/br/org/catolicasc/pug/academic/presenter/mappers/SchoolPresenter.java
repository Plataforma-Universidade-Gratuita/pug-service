package br.org.catolicasc.pug.academic.presenter.mappers;

import br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView;
import br.org.catolicasc.pug.academic.presenter.dtos.SchoolCreateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.SchoolResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.SchoolUpdateRequest;
import br.org.catolicasc.pug.academic.service.dtos.SchoolCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.SchoolUpdateCommand;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import br.org.catolicasc.pug.shared.presenter.mappers.SharedDataPresenter;
import java.util.Locale;

/**
 * Stateless utility class responsible for mapping internal academic school projections to external
 * API responses.
 *
 * <p>This presenter acts as a translation layer, converting raw CQRS query views ({@link
 * SchoolView}) into client-ready representations ({@link SchoolResponse}).
 */
public final class SchoolPresenter {
  /** Private constructor to prevent instantiation. */
  private SchoolPresenter() {}

  /**
   * Maps an incoming REST creation request into an application layer school creation command.
   *
   * <p>This helper is responsible only for the school portion of the payload. It extracts the raw
   * name and encapsulates it into a {@link SchoolCreateCommand}, leaving domain validation and
   * orchestration to the application service layer.
   *
   * @param req the validated {@link SchoolCreateRequest} payload
   * @return the corresponding {@link SchoolCreateCommand}, or {@code null} if the request is null
   */
  public static SchoolCreateCommand toCommand(SchoolCreateRequest req) {
    if (req == null) {
      return null;
    }
    return new SchoolCreateCommand(req.name());
  }

  /**
   * Maps an incoming REST update request into an application layer school update command.
   *
   * <p>This helper is responsible only for the school portion of the update payload. Because
   * updates can be partial, it directly propagates the potentially {@code null} name to the {@link
   * SchoolUpdateCommand}, allowing the service layer to interpret omitted values as "no change".
   *
   * @param req the validated {@link SchoolUpdateRequest} payload containing the modified data
   * @return the corresponding {@link SchoolUpdateCommand}, or {@code null} if the request is null
   */
  public static SchoolUpdateCommand toCommand(SchoolUpdateRequest req) {
    if (req == null) {
      return null;
    }
    return new SchoolUpdateCommand(req.name());
  }

  /**
   * Projects a read-only {@link SchoolView} into a client-facing {@link SchoolResponse}.
   *
   * @param v the internal read-model projection of the school
   * @param locale the locale extracted from the client's request headers
   * @return a fully populated {@link SchoolResponse} ready for JSON serialization, or {@code null}
   *     if the input view is null
   */
  public static SchoolResponse toResponse(SchoolView v, Locale locale) {
    if (v == null || locale == null) {
      return null;
    }

    AuditInfoResponse auditInfo =
        SharedDataPresenter.createAuditInfoResponse(v.createdAt(), v.updatedAt(), locale);

    return new SchoolResponse(v.id(), v.name(), auditInfo);
  }
}
