package br.org.catolicasc.pug.project.presenter.mappers;

import br.org.catolicasc.pug.project.infra.read.dtos.AttendanceView;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceCreateRequest;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceResponse;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceValidateRequest;
import br.org.catolicasc.pug.project.service.dtos.AttendanceCreateCommand;
import br.org.catolicasc.pug.project.service.dtos.AttendanceValidateCommand;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import br.org.catolicasc.pug.shared.presenter.mappers.SharedDataPresenter;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import java.util.Locale;

/**
 * Stateless utility class responsible for mapping internal attendance projections to external API
 * responses and REST payloads to application commands.
 *
 * <p>This presenter acts as a translation layer, converting raw CQRS query views ({@link
 * AttendanceView}) into client-ready representations ({@link AttendanceResponse}), and mapping
 * incoming data transfers to command structures.
 */
public final class AttendancePresenter {

  /** Private constructor to prevent instantiation of utility class. */
  private AttendancePresenter() {}

  /**
   * Maps an incoming REST creation request into an application layer creation command.
   *
   * @param req the validated {@link AttendanceCreateRequest} payload
   * @return the corresponding {@link AttendanceCreateCommand}, or {@code null} if input is null
   */
  public static AttendanceCreateCommand toCommand(AttendanceCreateRequest req) {
    if (req == null) {
      return null;
    }
    return new AttendanceCreateCommand(req.projectId(), req.studentId(), req.duration());
  }

  /**
   * Maps an incoming REST validation request into an application layer validation command.
   *
   * @param req the validated {@link AttendanceValidateRequest} payload
   * @return the corresponding {@link AttendanceValidateCommand}, or {@code null} if input is null
   */
  public static AttendanceValidateCommand toCommand(AttendanceValidateRequest req) {
    if (req == null) {
      return null;
    }
    return new AttendanceValidateCommand(req.qrValidationHash(), req.status());
  }

  /**
   * Projects a read-only {@link AttendanceView} into a client-facing {@link AttendanceResponse}.
   *
   * <p>This mapping flattens the response, returning only the identifiers for project, student, and
   * validator, while resolving localized labels and formatting dates based on the client's {@link
   * Locale}.
   *
   * @param v the internal read-model projection of the attendance
   * @param locale the locale extracted from the client's request headers
   * @param i18n the internationalization service for resolving bundle keys
   * @return a fully populated {@link AttendanceResponse} ready for JSON serialization, or {@code
   *     null} if any required input is null
   */
  public static AttendanceResponse toResponse(AttendanceView v, Locale locale, I18n i18n) {
    if (v == null || locale == null || i18n == null) {
      return null;
    }

    String statusFormatted = i18n.translation(v.status().getBundleKey(), locale);
    String validatedAtFormatted = StringUtils.toStringFormatted(v.validatedAt(), locale);

    AuditInfoResponse auditInfo =
        SharedDataPresenter.createAuditInfoResponse(v.createdAt(), v.updatedAt(), locale);

    return new AttendanceResponse(
        v.id(),
        v.projectId(),
        v.studentId(),
        v.duration(),
        v.qrValidationHash(),
        v.status(),
        statusFormatted,
        v.validatedById(),
        v.validatedAt(),
        validatedAtFormatted,
        auditInfo);
  }
}
