package com.pug.academic.presenter.mappers;

import com.pug.academic.infra.read.dtos.StudentView;
import com.pug.academic.presenter.dtos.StudentCreateRequest;
import com.pug.academic.presenter.dtos.StudentResponse;
import com.pug.academic.presenter.dtos.StudentUpdateRequest;
import com.pug.academic.service.dtos.StudentCreateCommand;
import com.pug.academic.service.dtos.StudentUpdateCommand;
import com.pug.identity.service.dtos.AccountCreateCommand;
import com.pug.identity.service.dtos.AccountUpdateCommand;
import com.pug.identity.service.dtos.UserCreateCommand;
import com.pug.identity.service.dtos.UserUpdateCommand;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.dtos.AuditInfoResponse;
import com.pug.shared.presenter.dtos.CampusResponse;
import com.pug.shared.presenter.mappers.SharedDataPresenter;
import com.pug.shared.utils.StringUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * Stateless utility class responsible for mapping internal student enrollment projections to
 * external API responses.
 *
 * <p>This presenter acts as a complex translation layer. It consolidates deeply nested CQRS query
 * views ({@link StudentView}) into client-ready representations ({@link StudentResponse}),
 * delegates to the Identity and Academic domain presenters, and executes UI-specific business logic
 * (such as calculating remaining days and missing hours).
 */
public final class StudentPresenter {
  /** Private constructor to prevent instantiation. */
  private StudentPresenter() {}

  /**
   * Maps an incoming REST creation request into an application layer student creation command.
   *
   * @param req the validated {@link StudentCreateRequest} payload containing identity and
   *     enrollment details
   * @param hashedPassword the securely hashed password string to assign to the new account
   * @return the corresponding {@link StudentCreateCommand}, or {@code null} if the request is null
   */
  public static StudentCreateCommand toCommand(StudentCreateRequest req, String hashedPassword) {
    if (req == null) {
      return null;
    }

    UserCreateCommand userCmd = new UserCreateCommand(req.cpf(), req.name());
    AccountCreateCommand accountCmd =
        new AccountCreateCommand(req.email(), AccountType.STUDENT, hashedPassword, userCmd);

    return new StudentCreateCommand(
        accountCmd,
        req.academicRegistration(),
        req.campus(),
        req.courseId(),
        req.requiredHours(),
        req.startDate(),
        req.dueDate());
  }

  /**
   * Maps an incoming REST update request into an application layer student update command.
   *
   * @param req the validated {@link StudentUpdateRequest} payload containing the modified data
   * @param hashedPassword the securely hashed password string, or {@code null} if the password is
   *     not being updated
   * @return the corresponding {@link StudentUpdateCommand}, or {@code null} if the request is null
   */
  public static StudentUpdateCommand toCommand(StudentUpdateRequest req, String hashedPassword) {
    if (req == null) {
      return null;
    }

    UserUpdateCommand userCmd = new UserUpdateCommand(req.name());
    AccountUpdateCommand accountCmd =
        new AccountUpdateCommand(req.email(), hashedPassword, userCmd);

    return new StudentUpdateCommand(
        accountCmd,
        req.academicRegistration(),
        req.campus(),
        req.courseId(),
        req.requiredHours(),
        req.startDate(),
        req.dueDate());
  }

  /**
   * Projects a read-only {@link StudentView} into a client-facing {@link StudentResponse}.
   *
   * <p>This mapping explicitly calculates dynamic presentation data:
   *
   * <ul>
   *   <li><b>Missing Hours:</b> Derived from the required hours if not yet concluded.
   *   <li><b>Remaining Days:</b> Calculated dynamically based on the current system date and the
   *       enrollment due date.
   * </ul>
   *
   * @param v the internal read-model projection of the student enrollment
   * @param locale the locale extracted from the client's request headers
   * @param i18n the internationalization service for resolving bundle keys
   * @return a fully populated {@link StudentResponse} ready for JSON serialization, or {@code null}
   *     if any required input is null
   */
  public static StudentResponse toResponse(StudentView v, Locale locale, I18n i18n) {
    if (v == null || locale == null || i18n == null) {
      return null;
    }

    String startDateFormatted = StringUtils.toStringFormatted(v.startDate(), locale);
    String dueDateFormatted = StringUtils.toStringFormatted(v.dueDate(), locale);

    BigDecimal requiredHours = v.requiredHours();
    BigDecimal completedHours = v.completedHours();
    BigDecimal missingHours = BigDecimal.ZERO;

    if (requiredHours != null && completedHours != null && Boolean.FALSE.equals(v.concluded())) {
      missingHours = requiredHours.subtract(completedHours).max(BigDecimal.ZERO);
    }

    long remainingDays = 0;
    String remainingDaysFormatted = "";
    if (v.dueDate() != null) {
      remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), v.dueDate());
      remainingDaysFormatted = formatRemainingDays(v.dueDate(), locale, i18n);
    }

    CampusResponse campus = SharedDataPresenter.createCampusResponse(v.campus(), locale, i18n);
    AuditInfoResponse auditInfo =
        SharedDataPresenter.createAuditInfoResponse(v.createdAt(), v.updatedAt(), locale);

    return new StudentResponse(
        v.accountId(),
        v.academicRegistration(),
        campus,
        v.courseId(),
        requiredHours,
        completedHours,
        missingHours,
        v.startDate(),
        startDateFormatted,
        v.dueDate(),
        dueDateFormatted,
        remainingDays,
        remainingDaysFormatted,
        auditInfo);
  }

  /**
   * Formats the number of remaining days until an enrollment due date into a human-readable,
   * localized string.
   *
   * @param dueDate the due date of the enrollment period
   * @param locale the target locale for formatting
   * @param i18n the internationalization service
   * @return a localized string representing the time remaining (e.g., "Hoje", "Amanhã", "X dias
   *     restantes", "Atrasado")
   */
  private static String formatRemainingDays(LocalDate dueDate, Locale locale, I18n i18n) {
    if (dueDate == null) {
      return "";
    }

    long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);

    if (remainingDays < 0) {
      return i18n.translation("academic.student.days.overdue", locale, Math.abs(remainingDays));
    }
    if (remainingDays == 0) {
      return i18n.translation("academic.student.days.today", locale);
    }
    if (remainingDays == 1) {
      return i18n.translation("academic.student.days.tomorrow", locale);
    }

    return i18n.translation("academic.student.days.remaining", locale, remainingDays);
  }
}
