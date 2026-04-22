package br.org.catolicasc.pug.academic.presenter.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.infra.read.dtos.StudentView;
import br.org.catolicasc.pug.academic.presenter.dtos.StudentCreateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.StudentResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.StudentUpdateRequest;
import br.org.catolicasc.pug.academic.service.dtos.StudentCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.StudentUpdateCommand;
import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.i18n.I18n;
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
@DisplayName("StudentPresenter Coverage")
class StudentPresenterTest {

  @Inject I18n i18n;

  @Nested
  @DisplayName("Create Command Mapping")
  class CreateCommandMapping {

    @Test
    @DisplayName("Should map request to command with account data")
    void toCreateCommand() {
      String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
      UUID courseId = UUID.randomUUID();
      StudentCreateRequest req =
          new StudentCreateRequest(
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

      StudentCreateCommand cmd = StudentPresenter.toCommand(req, "hashedPw");

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
      assertThat(StudentPresenter.toCommand((StudentCreateRequest) null, "hash")).isNull();
    }
  }

  @Nested
  @DisplayName("Update Command Mapping")
  class UpdateCommandMapping {

    @Test
    @DisplayName("Should map update request to command")
    void toUpdateCommand() {
      StudentUpdateRequest req =
          new StudentUpdateRequest(
              "Jane Doe",
              null,
              "jane@test.com",
              "newpass",
              "NEWREG",
              Campi.JOINVILLE,
              UUID.randomUUID(),
              new BigDecimal("200"),
              LocalDate.now(),
              LocalDate.now().plusMonths(12));

      StudentUpdateCommand cmd = StudentPresenter.toCommand(req, "hashedNewPw");

      assertThat(cmd).isNotNull();
      assertThat(cmd.academicRegistration()).isEqualTo("NEWREG");
      assertThat(cmd.accountUpdateCommand()).isNotNull();
      //      assertThat(cmd.accountUpdateCommand().email()).isEqualTo("jane@test.com");
      assertThat(cmd.accountUpdateCommand().passwordHash()).isEqualTo("hashedNewPw");
    }

    @Test
    @DisplayName("Should return null when request is null")
    void toUpdateCommandNull() {
      assertThat(StudentPresenter.toCommand((StudentUpdateRequest) null, "hash")).isNull();
    }
  }

  @Nested
  @DisplayName("Response Mapping")
  class ResponseMapping {

    @Test
    @DisplayName("Should return null when view is null")
    void toResponseNullView() {
      assertThat(StudentPresenter.toResponse(null, Locale.US, i18n)).isNull();
    }

    @Test
    @DisplayName("Should return null when locale is null")
    void toResponseNullLocale() {
      assertThat(StudentPresenter.toResponse(buildStudentView(), null, i18n)).isNull();
    }

    @Test
    @DisplayName("Should return null when i18n is null")
    void toResponseNullI18n() {
      assertThat(StudentPresenter.toResponse(buildStudentView(), Locale.US, null)).isNull();
    }

    @Test
    @DisplayName("Should map view to response and calculate missing hours")
    void toResponseWithMissingHours() {
      StudentView view = buildStudentView();
      StudentResponse response = StudentPresenter.toResponse(view, Locale.US, i18n);

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
      UUID accountId = UUID.randomUUID();
      OffsetDateTime now = OffsetDateTime.now();
      StudentView view =
          new StudentView(
              accountId,
              "REG12345",
              Campi.JOINVILLE,
              UUID.randomUUID(),
              new BigDecimal("100"),
              new BigDecimal("100"),
              true,
              LocalDate.now(),
              LocalDate.now().plusMonths(6),
              now,
              now);

      StudentResponse response = StudentPresenter.toResponse(view, Locale.US, i18n);

      assertThat(response.missingHours()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should calculate remaining days")
    void toResponseRemainingDays() {
      StudentView view = buildStudentView();
      StudentResponse response = StudentPresenter.toResponse(view, Locale.US, i18n);

      assertThat(response.remainingDays()).isGreaterThan(0);
      assertThat(response.remainingDaysFormatted()).isNotBlank();
    }

    @Test
    @DisplayName("Should handle null dueDate")
    void toResponseNullDueDate() {
      UUID accountId = UUID.randomUUID();
      OffsetDateTime now = OffsetDateTime.now();
      StudentView view =
          new StudentView(
              accountId,
              "REG12345",
              Campi.JOINVILLE,
              UUID.randomUUID(),
              new BigDecimal("100"),
              new BigDecimal("30"),
              false,
              LocalDate.now(),
              null,
              now,
              now);

      StudentResponse response = StudentPresenter.toResponse(view, Locale.US, i18n);

      assertThat(response.remainingDays()).isZero();
      assertThat(response.remainingDaysFormatted()).isEmpty();
    }

    private StudentView buildStudentView() {
      UUID accountId = UUID.randomUUID();
      OffsetDateTime now = OffsetDateTime.now();
      return new StudentView(
          accountId,
          "REG12345",
          Campi.JOINVILLE,
          UUID.randomUUID(),
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
