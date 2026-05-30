package br.org.catolicasc.pug.academic.presenter.mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.academic.infra.read.dtos.AreaOfExpertiseComplexSearchView;
import br.org.catolicasc.pug.academic.infra.read.dtos.CourseComplexSearchView;
import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentComplexSearchView;
import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentView;
import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentComplexSearchResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentCreateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentUpdateRequest;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentUpdateCommand;
import br.org.catolicasc.pug.identity.infra.read.dtos.AccountComplexSearchView;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.i18n.I18n;
import com.github.f4b6a3.uuid.UuidCreator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("FormerStudentPresenter Coverage")
class FormerStudentPresenterTest {

  @Test
  @DisplayName("Should map create request to command")
  void toCreateCommand() {
    var courseId = UuidCreator.getTimeOrderedEpoch();
    FormerStudentCreateRequest request =
        new FormerStudentCreateRequest(
            "12345678901",
            "John Doe",
            "john@example.com",
            "REG123",
            Campi.JOINVILLE,
            courseId,
            new BigDecimal("100"),
            LocalDate.now(),
            LocalDate.now().plusMonths(6));

    FormerStudentCreateCommand command = FormerStudentPresenter.toCommand(request);

    assertThat(command).isNotNull();
    assertThat(command.academicRegistration()).isEqualTo("REG123");
    assertThat(command.campus()).isEqualTo(Campi.JOINVILLE);
    assertThat(command.courseId()).isEqualTo(courseId);
    assertThat(command.accountCreateCommand().emailString()).isEqualTo("john@example.com");
    assertThat(command.accountCreateCommand().userCommand().name()).isEqualTo("John Doe");
  }

  @Test
  @DisplayName("Should return null when create request is null")
  void toCreateCommandNull() {
    assertThat(FormerStudentPresenter.toCommand((FormerStudentCreateRequest) null)).isNull();
  }

  @Test
  @DisplayName("Should map update request to command")
  void toUpdateCommand() {
    var courseId = UuidCreator.getTimeOrderedEpoch();
    FormerStudentUpdateRequest request =
        new FormerStudentUpdateRequest(
            "New Name",
            "12345678901",
            "new@example.com",
            "REG999",
            Campi.JARAGUA_DO_SUL,
            courseId,
            new BigDecimal("150"),
            LocalDate.now(),
            LocalDate.now().plusMonths(12));

    FormerStudentUpdateCommand command = FormerStudentPresenter.toCommand(request);

    assertThat(command).isNotNull();
    assertThat(command.academicRegistration()).isEqualTo("REG999");
    assertThat(command.campus()).isEqualTo(Campi.JARAGUA_DO_SUL);
    assertThat(command.courseId()).isEqualTo(courseId);
    assertThat(command.accountUpdateCommand().emailString()).isEqualTo("new@example.com");
    assertThat(command.accountUpdateCommand().userCommand().name()).isEqualTo("New Name");
  }

  @Test
  @DisplayName("Should return null when update request is null")
  void toUpdateCommandNull() {
    assertThat(FormerStudentPresenter.toCommand((FormerStudentUpdateRequest) null)).isNull();
  }

  @Test
  @DisplayName("Should map FormerStudentView to response")
  void toResponseSuccess() {
    I18n i18n = i18n();
    FormerStudentView view =
        new FormerStudentView(
            UuidCreator.getTimeOrderedEpoch(),
            "REG123",
            Campi.JOINVILLE,
            UuidCreator.getTimeOrderedEpoch(),
            new BigDecimal("100"),
            new BigDecimal("25"),
            false,
            LocalDate.now(),
            LocalDate.now().plusDays(2),
            OffsetDateTime.now(),
            OffsetDateTime.now());

    FormerStudentResponse response = FormerStudentPresenter.toResponse(view, Locale.US, i18n);

    assertThat(response).isNotNull();
    assertThat(response.academicRegistration()).isEqualTo("REG123");
    assertThat(response.counterpartHours().missingHours())
        .isEqualByComparingTo(new BigDecimal("75"));
    assertThat(response.counterpartHours().progress())
        .isEqualByComparingTo(new BigDecimal("25.00"));
    assertThat(response.period().remainingDays()).isGreaterThanOrEqualTo(1);
    assertThat(response.auditInfo()).isNotNull();
  }

  @Test
  @DisplayName("Should cap progress at 100 and handle concluded counterpart hours")
  void toResponseConcludedCapsProgress() {
    I18n i18n = i18n();
    FormerStudentView view =
        new FormerStudentView(
            UuidCreator.getTimeOrderedEpoch(),
            "REG123",
            Campi.JOINVILLE,
            UuidCreator.getTimeOrderedEpoch(),
            new BigDecimal("100"),
            new BigDecimal("150"),
            true,
            LocalDate.now(),
            LocalDate.now(),
            OffsetDateTime.now(),
            OffsetDateTime.now());

    FormerStudentResponse response = FormerStudentPresenter.toResponse(view, Locale.US, i18n);

    assertThat(response.counterpartHours().missingHours()).isEqualByComparingTo(BigDecimal.ZERO);
    assertThat(response.counterpartHours().progress()).isEqualByComparingTo(new BigDecimal("100"));
    assertThat(response.counterpartHours().concluded()).isTrue();
  }

  @Test
  @DisplayName("Should handle null hours and due date when mapping response")
  void toResponseWithNullOptionalValues() {
    I18n i18n = i18n();
    FormerStudentView view =
        new FormerStudentView(
            UuidCreator.getTimeOrderedEpoch(),
            "REG123",
            Campi.JOINVILLE,
            UuidCreator.getTimeOrderedEpoch(),
            null,
            null,
            null,
            null,
            null,
            OffsetDateTime.now(),
            OffsetDateTime.now());

    FormerStudentResponse response = FormerStudentPresenter.toResponse(view, Locale.US, i18n);

    assertThat(response).isNotNull();
    assertThat(response.counterpartHours().requiredHours()).isEqualByComparingTo(BigDecimal.ZERO);
    assertThat(response.counterpartHours().completedHours()).isEqualByComparingTo(BigDecimal.ZERO);
    assertThat(response.period().remainingDaysFormatted()).isEmpty();
  }

  @Test
  @DisplayName("Should return null response when required argument is null")
  void toResponseNullInputs() {
    I18n i18n = i18n();
    FormerStudentView view =
        new FormerStudentView(
            UuidCreator.getTimeOrderedEpoch(),
            "REG123",
            Campi.JOINVILLE,
            UuidCreator.getTimeOrderedEpoch(),
            BigDecimal.ONE,
            BigDecimal.ZERO,
            false,
            LocalDate.now(),
            LocalDate.now(),
            OffsetDateTime.now(),
            OffsetDateTime.now());

    assertThat(FormerStudentPresenter.toResponse(null, Locale.US, i18n)).isNull();
    assertThat(FormerStudentPresenter.toResponse(view, null, i18n)).isNull();
    assertThat(FormerStudentPresenter.toResponse(view, Locale.US, null)).isNull();
  }

  @Test
  @DisplayName("Should map complex-search view to response")
  void toComplexSearchResponseSuccess() {
    I18n i18n = i18n();
    OffsetDateTime now = OffsetDateTime.now();
    var area =
        new AreaOfExpertiseComplexSearchView(UuidCreator.getTimeOrderedEpoch(), "Engineering");
    var course = new CourseComplexSearchView(UuidCreator.getTimeOrderedEpoch(), "Software", area);
    var account =
        new AccountComplexSearchView(
            UuidCreator.getTimeOrderedEpoch(),
            UuidCreator.getTimeOrderedEpoch(),
            "Jane Doe",
            "jane@example.com",
            AccountType.FORMER_STUDENT,
            now,
            now,
            true);
    FormerStudentComplexSearchView view =
        new FormerStudentComplexSearchView(
            account,
            "REG456",
            Campi.JARAGUA_DO_SUL,
            new BigDecimal("100"),
            new BigDecimal("100"),
            true,
            LocalDate.now(),
            LocalDate.now().minusDays(1),
            now,
            now,
            course);

    FormerStudentComplexSearchResponse response =
        FormerStudentPresenter.toComplexSearchResponse(view, Locale.US, i18n);

    assertThat(response).isNotNull();
    assertThat(response.account().user().name()).isEqualTo("Jane Doe");
    assertThat(response.course().areaOfExpertise().name()).isEqualTo("Engineering");
    assertThat(response.counterpartHours().concluded()).isTrue();
  }

  @Test
  @DisplayName("Should map complex-search response with null nested course")
  void toComplexSearchResponseNullNestedCourse() {
    I18n i18n = i18n();
    OffsetDateTime now = OffsetDateTime.now();
    var account =
        new AccountComplexSearchView(
            UuidCreator.getTimeOrderedEpoch(),
            UuidCreator.getTimeOrderedEpoch(),
            "Jane Doe",
            "jane@example.com",
            AccountType.FORMER_STUDENT,
            now,
            now,
            true);
    FormerStudentComplexSearchView view =
        new FormerStudentComplexSearchView(
            account,
            "REG456",
            Campi.JARAGUA_DO_SUL,
            new BigDecimal("100"),
            BigDecimal.ZERO,
            false,
            LocalDate.now(),
            LocalDate.now().plusDays(1),
            now,
            now,
            null);

    FormerStudentComplexSearchResponse response =
        FormerStudentPresenter.toComplexSearchResponse(view, Locale.US, i18n);

    assertThat(response).isNotNull();
    assertThat(response.course()).isNull();
  }

  @Test
  @DisplayName("Should return null complex-search response when required argument is null")
  void toComplexSearchResponseNullInputs() {
    I18n i18n = i18n();
    assertThat(FormerStudentPresenter.toComplexSearchResponse(null, Locale.US, i18n)).isNull();
  }

  private I18n i18n() {
    I18n i18n = mock(I18n.class);
    when(i18n.translation(anyString(), any(Locale.class))).thenAnswer(inv -> inv.getArgument(0));
    when(i18n.translation(anyString(), any(Locale.class), any()))
        .thenAnswer(inv -> inv.getArgument(0) + ":" + inv.getArgument(2));
    return i18n;
  }
}
