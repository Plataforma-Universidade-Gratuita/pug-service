package br.org.catolicasc.pug.academic.presenter.mappers;

import br.org.catolicasc.pug.academic.infra.read.dtos.AreaOfExpertiseComplexSearchView;
import br.org.catolicasc.pug.academic.infra.read.dtos.CourseComplexSearchView;
import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentComplexSearchView;
import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentView;
import br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise.AreaOfExpertiseComplexSearchResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.courses.CourseComplexSearchResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.CounterpartHoursResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentComplexSearchResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentCreateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentUpdateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.PeriodResponse;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentUpdateCommand;
import br.org.catolicasc.pug.identity.presenter.dtos.accounts.AccountComplexSearchResponse;
import br.org.catolicasc.pug.identity.presenter.mappers.AccountPresenter;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountUpdateCommand;
import br.org.catolicasc.pug.identity.service.dtos.users.UserCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.users.UserUpdateCommand;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import br.org.catolicasc.pug.shared.presenter.dtos.CampusResponse;
import br.org.catolicasc.pug.shared.presenter.mappers.SharedDataPresenter;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * Stateless mapper responsible for translating former-student presenter payloads and read
 * projections.
 *
 * <p>This component owns the boundary formatting rules for former-student responses, including the
 * derived counterpart-hours summary, period metadata, and nested account/course projections used by
 * complex-search contracts.
 */
public final class FormerStudentPresenter {

  private FormerStudentPresenter() {}

  /**
   * Converts a former-student creation request into the corresponding command graph.
   *
   * @param request validated presenter-layer payload
   * @return command graph used by the service layer, or {@code null} when the request is null
   */
  public static FormerStudentCreateCommand toCommand(FormerStudentCreateRequest request) {
    if (request == null) {
      return null;
    }

    UserCreateCommand userCommand = new UserCreateCommand(request.cpf(), request.name());
    AccountCreateCommand accountCommand =
        new AccountCreateCommand(request.email(), AccountType.FORMER_STUDENT, null, userCommand);

    return new FormerStudentCreateCommand(
        accountCommand,
        request.academicRegistration(),
        request.campus(),
        request.courseId(),
        request.requiredHours(),
        request.startDate(),
        request.dueDate());
  }

  /**
   * Converts a former-student update request into the corresponding command graph.
   *
   * @param request validated presenter-layer payload
   * @return command graph used by the service layer, or {@code null} when the request is null
   */
  public static FormerStudentUpdateCommand toCommand(FormerStudentUpdateRequest request) {
    if (request == null) {
      return null;
    }

    UserUpdateCommand userCommand = new UserUpdateCommand(request.name());
    AccountUpdateCommand accountCommand =
        new AccountUpdateCommand(request.email(), null, null, userCommand);

    return new FormerStudentUpdateCommand(
        accountCommand,
        request.academicRegistration(),
        request.campus(),
        request.courseId(),
        request.requiredHours(),
        request.startDate(),
        request.dueDate());
  }

  /**
   * Converts a read projection into the standard former-student response.
   *
   * @param view query-layer read projection
   * @param locale locale used for formatting and localization
   * @param i18n translation helper used by nested presenters
   * @return presenter response, or {@code null} when any required input is null
   */
  public static FormerStudentResponse toResponse(FormerStudentView view, Locale locale, I18n i18n) {
    if (view == null || locale == null || i18n == null) {
      return null;
    }

    return new FormerStudentResponse(
        view.accountId(),
        view.academicRegistration(),
        SharedDataPresenter.createCampusResponse(view.campus(), locale, i18n),
        view.courseId(),
        createCounterpartHoursResponse(
            view.requiredHours(), view.completedHours(), view.concluded()),
        createPeriodResponse(view.startDate(), view.dueDate(), locale, i18n),
        SharedDataPresenter.createAuditInfoResponse(view.createdAt(), view.updatedAt(), locale));
  }

  /**
   * Converts a complex-search read projection into the paginated response shape consumed by the
   * frontend.
   *
   * @param view query-layer complex-search projection
   * @param locale locale used for formatting and localization
   * @param i18n translation helper used by nested presenters
   * @return complex-search presenter response, or {@code null} when any required input is null
   */
  public static FormerStudentComplexSearchResponse toComplexSearchResponse(
      FormerStudentComplexSearchView view, Locale locale, I18n i18n) {
    if (view == null || locale == null || i18n == null) {
      return null;
    }

    AccountComplexSearchResponse account =
        AccountPresenter.toComplexSearchResponse(view.account(), locale, i18n);
    CampusResponse campus = SharedDataPresenter.createCampusResponse(view.campus(), locale, i18n);
    CounterpartHoursResponse counterpartHours =
        createCounterpartHoursResponse(
            view.requiredHours(), view.completedHours(), view.concluded());
    PeriodResponse period = createPeriodResponse(view.startDate(), view.dueDate(), locale, i18n);
    AuditInfoResponse auditInfo =
        SharedDataPresenter.createAuditInfoResponse(view.createdAt(), view.updatedAt(), locale);

    return new FormerStudentComplexSearchResponse(
        account,
        view.academicRegistration(),
        campus,
        counterpartHours,
        period,
        auditInfo,
        toCourseComplexSearchResponse(view.course()));
  }

  private static CounterpartHoursResponse createCounterpartHoursResponse(
      BigDecimal requiredHours, BigDecimal completedHours, Boolean concluded) {
    BigDecimal safeRequired = requiredHours == null ? BigDecimal.ZERO : requiredHours;
    BigDecimal safeCompleted = completedHours == null ? BigDecimal.ZERO : completedHours;
    boolean safeConcluded = Boolean.TRUE.equals(concluded);
    BigDecimal missingHours =
        safeConcluded ? BigDecimal.ZERO : safeRequired.subtract(safeCompleted).max(BigDecimal.ZERO);
    BigDecimal progress =
        safeRequired.signum() == 0
            ? BigDecimal.ZERO
            : safeCompleted
                .multiply(BigDecimal.valueOf(100))
                .divide(safeRequired, 2, RoundingMode.HALF_UP)
                .max(BigDecimal.ZERO);

    return new CounterpartHoursResponse(
        safeRequired,
        safeCompleted,
        missingHours,
        progress.min(BigDecimal.valueOf(100)),
        safeConcluded);
  }

  private static PeriodResponse createPeriodResponse(
      LocalDate startDate, LocalDate dueDate, Locale locale, I18n i18n) {
    String startDateFormatted = StringUtils.toStringFormatted(startDate, locale);
    String dueDateFormatted = StringUtils.toStringFormatted(dueDate, locale);
    long remainingDays = dueDate == null ? 0 : ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    String remainingDaysFormatted = formatRemainingDays(dueDate, locale, i18n);

    return new PeriodResponse(
        startDate,
        startDateFormatted,
        dueDate,
        dueDateFormatted,
        remainingDays,
        remainingDaysFormatted);
  }

  private static String formatRemainingDays(LocalDate dueDate, Locale locale, I18n i18n) {
    if (dueDate == null) {
      return "";
    }

    long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    if (remainingDays < 0) {
      return i18n.translation(
          "academic.formerStudent.days.overdue", locale, Math.abs(remainingDays));
    }
    if (remainingDays == 0) {
      return i18n.translation("academic.formerStudent.days.today", locale);
    }
    if (remainingDays == 1) {
      return i18n.translation("academic.formerStudent.days.tomorrow", locale);
    }
    return i18n.translation("academic.formerStudent.days.remaining", locale, remainingDays);
  }

  private static CourseComplexSearchResponse toCourseComplexSearchResponse(
      CourseComplexSearchView view) {
    if (view == null) {
      return null;
    }
    return new CourseComplexSearchResponse(
        view.id(), view.name(), toAreaOfExpertiseComplexSearchResponse(view.areaOfExpertise()));
  }

  private static AreaOfExpertiseComplexSearchResponse toAreaOfExpertiseComplexSearchResponse(
      AreaOfExpertiseComplexSearchView view) {
    if (view == null) {
      return null;
    }
    return new AreaOfExpertiseComplexSearchResponse(view.id(), view.name());
  }
}
