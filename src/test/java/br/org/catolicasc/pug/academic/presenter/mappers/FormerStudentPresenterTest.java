package br.org.catolicasc.pug.academic.presenter.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentView;
import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentCreateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentUpdateRequest;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentUpdateCommand;
import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.i18n.I18n;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("FormerStudentPresenter Coverage")
class FormerStudentPresenterTest {

  @Inject I18n i18n;

  @Nested
  @DisplayName("Create Command Mapping")
  class CreateCommandMapping {

    @Test
    @DisplayName("Should map request to command with account data")
    void toCreateCommand() {
      String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
      UUID courseId = UuidCreator.getTimeOrderedEpoch();
      FormerStudentCreateRequest req =
          new FormerStudentCreateRequest(
              cpf,
              "John Doe",
              "john@test.com",
              "password123",
              "REG12345",
              Campi.JOINVILLE,
              courseId,
              new BigDecimal("100"),
              LocalDate.now(),
              LocalDate.now().plusMonths(6));

      FormerStudentCreateCommand cmd = FormerStudentPresenter.toCommand(req, "hashedPw");

      assertThat(cmd).isNotNull();
      assertThat(cmd.academicRegistration()).isEqualTo("REG12345");
      assertThat(cmd.campus()).isEqualTo(Campi.JOINVILLE);
      assertThat(cmd.courseId()).isEqualTo(courseId);
      assertThat(cmd.accountCreateCommand()).isNotNull();
      //      assertThat(cmd.accountCreateCommand().email()).isEqualTo("john@test.com");
      //      assertThat(cmd.accountCreateCommand().passwordHash()).isEqualTo("hashedPw");
      //      assertThat(cmd.accountCreateCommand().userCreateCommand().name()).isEqualTo("John
      // Doe");
      //      assertThat(cmd.accountCreateCommand().userCreateCommand().cpf()).isEqualTo(cpf);
    }

    @Test
    @DisplayName("Should return null when request is null")
    void toCreateCommandNull() {
      assertThat(FormerStudentPresenter.toCommand((FormerStudentCreateRequest) null, "hash")).isNull();
    }
  }

  @Nested
  @DisplayName("Update Command Mapping")
  class UpdateCommandMapping {

    @Test
    @DisplayName("Should map update request to command")
    void toUpdateCommand() {
      FormerStudentUpdateRequest req =
          new FormerStudentUpdateRequest(
              "Jane Doe",
              null,
              "jane@test.com",
              "newpass",
              "NEWREG",
              Campi.JOINVILLE,
              UuidCreator.getTimeOrderedEpoch(),
              new BigDecimal("200"),
              LocalDate.now(),
              LocalDate.now().plusMonths(12));

      FormerStudentUpdateCommand cmd = FormerStudentPresenter.toCommand(req, "hashedNewPw");

      assertThat(cmd).isNotNull();
      assertThat(cmd.academicRegistration()).isEqualTo("NEWREG");
      assertThat(cmd.accountUpdateCommand()).isNotNull();
      //      assertThat(cmd.accountUpdateCommand().email()).isEqualTo("jane@test.com");
      assertThat(cmd.accountUpdateCommand().passwordHash()).isEqualTo("hashedNewPw");
    }

    @Test
    @DisplayName("Should return null when request is null")
    void toUpdateCommandNull() {
      assertThat(FormerStudentPresenter.toCommand((FormerStudentUpdateRequest) null, "hash")).isNull();
    }
  }

  @Nested
  @DisplayName("Response Mapping")
  class ResponseMapping {

    @Test
    @DisplayName("Should return null when view is null")
    void toResponseNullView() {
      assertThat(FormerStudentPresenter.toResponse(null, Locale.US, i18n)).isNull();
    }

    @Test
    @DisplayName("Should return null when locale is null")
    void toResponseNullLocale() {
      assertThat(FormerStudentPresenter.toResponse(buildFormerStudentView(), null, i18n)).isNull();
    }

    @Test
    @DisplayName("Should return null when i18n is null")
    void toResponseNullI18n() {
      assertThat(FormerStudentPresenter.toResponse(buildFormerStudentView(), Locale.US, null)).isNull();
    }

    @Test
    @DisplayName("Should map view to response and calculate missing hours")
    void toResponseWithMissingHours() {
      FormerStudentView view = buildFormerStudentView();
      FormerStudentResponse response = FormerStudentPresenter.toResponse(view, Locale.US, i18n);

      assertThat(response).isNotNull();
      assertThat(response.accountId()).isEqualTo(view.accountId());
      assertThat(response.academicRegistration()).isEqualTo("REG12345");
      assertThat(response.requiredHours()).isEqualByComparingTo(new BigDecimal("100"));
      assertThat(response.completedHours()).isEqualByComparingTo(new BigDecimal("30"));
      assertThat(response.missingHours()).isEqualByComparingTo(new BigDecimal("70"));
    }

    @Test
    @DisplayName("Should set missingHours to zero when concluded")
    void toResponseConcluded() {
      UUID accountId = UuidCreator.getTimeOrderedEpoch();
      OffsetDateTime now = OffsetDateTime.now();
      FormerStudentView view =
          new FormerStudentView(
              accountId,
              "REG12345",
              Campi.JOINVILLE,
              UuidCreator.getTimeOrderedEpoch(),
              new BigDecimal("100"),
              new BigDecimal("100"),
              true,
              LocalDate.now(),
              LocalDate.now().plusMonths(6),
              now,
              now);

      FormerStudentResponse response = FormerStudentPresenter.toResponse(view, Locale.US, i18n);

      assertThat(response.missingHours()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should calculate remaining days")
    void toResponseRemainingDays() {
      FormerStudentView view = buildFormerStudentView();
      FormerStudentResponse response = FormerStudentPresenter.toResponse(view, Locale.US, i18n);

      assertThat(response.remainingDays()).isGreaterThan(0);
      assertThat(response.remainingDaysFormatted()).isNotBlank();
    }

    @Test
    @DisplayName("Should handle null dueDate")
    void toResponseNullDueDate() {
      UUID accountId = UuidCreator.getTimeOrderedEpoch();
      OffsetDateTime now = OffsetDateTime.now();
      FormerStudentView view =
          new FormerStudentView(
              accountId,
              "REG12345",
              Campi.JOINVILLE,
              UuidCreator.getTimeOrderedEpoch(),
              new BigDecimal("100"),
              new BigDecimal("30"),
              false,
              LocalDate.now(),
              null,
              now,
              now);

      FormerStudentResponse response = FormerStudentPresenter.toResponse(view, Locale.US, i18n);

      assertThat(response.remainingDays()).isZero();
      assertThat(response.remainingDaysFormatted()).isEmpty();
    }

    private FormerStudentView buildFormerStudentView() {
      UUID accountId = UuidCreator.getTimeOrderedEpoch();
      OffsetDateTime now = OffsetDateTime.now();
      return new FormerStudentView(
          accountId,
          "REG12345",
          Campi.JOINVILLE,
          UuidCreator.getTimeOrderedEpoch(),
          new BigDecimal("100"),
          new BigDecimal("30"),
          false,
          LocalDate.now(),
          LocalDate.now().plusMonths(6),
          now,
          now);
    }
  }
}

