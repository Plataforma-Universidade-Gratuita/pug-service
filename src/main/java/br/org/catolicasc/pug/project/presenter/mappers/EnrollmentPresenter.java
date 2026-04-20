package br.org.catolicasc.pug.project.presenter.mappers;

import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.infra.read.dtos.EnrollmentView;
import br.org.catolicasc.pug.project.presenter.dtos.EnrollmentCreateRequest;
import br.org.catolicasc.pug.project.presenter.dtos.EnrollmentResponse;
import br.org.catolicasc.pug.project.service.dtos.EnrollmentCreateCommand;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import br.org.catolicasc.pug.shared.presenter.mappers.SharedDataPresenter;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import java.util.Locale;

/**
 * Stateless utility class responsible for mapping internal enrollment projections to external API
 * responses.
 *
 * <p>This presenter acts as a translation layer, converting lightweight CQRS query views ({@link
 * EnrollmentView}) into client-ready representations ({@link EnrollmentResponse}). It also applies
 * presentation-specific formatting for dates and localized status labels, while keeping project and
 * student details referenced only by their identifiers.
 */
public final class EnrollmentPresenter {

  /** Private constructor to prevent instantiation. */
  private EnrollmentPresenter() {}

  /**
   * Maps an incoming REST creation request into an application layer enrollment creation command.
   *
   * <p>This helper extracts the {@code projectId} from the payload and encapsulates it into an
   * {@link EnrollmentCreateCommand}, leaving student resolution and domain validation to the
   * application service layer.
   *
   * @param req the validated {@link EnrollmentCreateRequest} payload
   * @return the corresponding {@link EnrollmentCreateCommand}, or {@code null} if {@code req} is
   *     {@code null}
   */
  public static EnrollmentCreateCommand toCommand(EnrollmentCreateRequest req) {
    if (req == null) {
      return null;
    }
    return new EnrollmentCreateCommand(req.projectId());
  }

  /**
   * Projects a read-only {@link EnrollmentView} into a client-facing {@link EnrollmentResponse}.
   *
   * <p>Mapping rules:
   *
   * <ul>
   *   <li>Copies the {@code projectId} and {@code studentId} identifiers directly from the view.
   *   <li>Translates the {@link EnrollmentStatus} enum into a
   *       localized status label using {@link I18n}.
   *   <li>Formats the {@code acceptedAt} and {@code closingStatusAt} timestamps according to the
   *       provided {@link Locale}.
   *   <li>Builds a standard {@link AuditInfoResponse} using {@link SharedDataPresenter} to expose
   *       creation and last update timestamps in both raw and formatted forms.
   * </ul>
   *
   * @param v the internal read-model projection of the enrollment
   * @param locale the locale extracted from the client's request headers
   * @param i18n the internationalization service for resolving bundle keys
   * @return a fully populated {@link EnrollmentResponse} ready for JSON serialization, or {@code
   *     null} if any required input ({@code v}, {@code locale}, {@code i18n}) is {@code null}
   */
  public static EnrollmentResponse toResponse(EnrollmentView v, Locale locale, I18n i18n) {
    if (v == null || locale == null || i18n == null) {
      return null;
    }

    String statusFormatted = i18n.translation(v.status().getBundleKey(), locale);
    String acceptedAtFormatted = StringUtils.toStringFormatted(v.acceptedAt(), locale);
    String closingStatusAtFormatted = StringUtils.toStringFormatted(v.closingStatusAt(), locale);

    AuditInfoResponse auditInfo =
        SharedDataPresenter.createAuditInfoResponse(v.createdAt(), v.updatedAt(), locale);

    return new EnrollmentResponse(
        v.projectId(),
        v.studentId(),
        v.status(),
        statusFormatted,
        v.acceptedAt(),
        acceptedAtFormatted,
        v.closingStatusAt(),
        closingStatusAtFormatted,
        auditInfo);
  }
}
