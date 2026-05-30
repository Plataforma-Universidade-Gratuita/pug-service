package br.org.catolicasc.pug.project.presenter.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import br.org.catolicasc.pug.project.infra.read.dtos.AttendanceView;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceCreateRequest;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceResponse;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceValidateRequest;
import br.org.catolicasc.pug.project.service.dtos.AttendanceCreateCommand;
import br.org.catolicasc.pug.project.service.dtos.AttendanceValidateCommand;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.i18n.I18n;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AttendancePresenter Coverage")
class AttendancePresenterTest {

  @Inject I18n i18n;

  @Nested
  @DisplayName("Create Command Mapping")
  class CreateCommandTests {

    @Test
    @DisplayName("Should map AttendanceCreateRequest to command")
    void toCommand() {
      UUID projectId = UuidCreator.getTimeOrderedEpoch();
      UUID studentId = UuidCreator.getTimeOrderedEpoch();
      AttendanceCreateCommand command =
          AttendancePresenter.toCommand(
              new AttendanceCreateRequest(projectId, studentId, new BigDecimal("2.00")));

      assertThat(command).isNotNull();
      assertThat(command.projectId()).isEqualTo(projectId);
      assertThat(command.studentId()).isEqualTo(studentId);
      assertThat(command.duration()).isEqualByComparingTo("2.00");
    }

    @Test
    @DisplayName("Should return null when request is null")
    void toCommandNull() {
      assertThat(AttendancePresenter.toCommand((AttendanceCreateRequest) null)).isNull();
    }
  }

  @Nested
  @DisplayName("Validate Command Mapping")
  class ValidateCommandTests {

    @Test
    @DisplayName("Should map AttendanceValidateRequest to command")
    void toCommand() {
      AttendanceValidateCommand command =
          AttendancePresenter.toCommand(
              new AttendanceValidateRequest(AttendanceStatus.PRESENT, "hash-123"));

      assertThat(command).isNotNull();
      assertThat(command.status()).isEqualTo(AttendanceStatus.PRESENT);
      assertThat(command.qrValidationHash()).isEqualTo("hash-123");
    }
  }

  @Nested
  @DisplayName("Response Mapping")
  class ResponseTests {

    @Test
    @DisplayName("Should return null when view is null")
    void toResponseNullView() {
      assertThat(AttendancePresenter.toResponse(null, Locale.US, i18n)).isNull();
    }

    @Test
    @DisplayName("Should map AttendanceView to single response correctly")
    void toResponseSuccess() {
      AttendanceView view = sampleView();
      AttendanceResponse response = AttendancePresenter.toResponse(view, Locale.US, i18n);

      assertThat(response).isNotNull();
      assertThat(response.id()).isEqualTo(view.id());
      assertThat(response.projectId()).isEqualTo(view.projectId());
      assertThat(response.studentId()).isEqualTo(view.studentId());
      assertThat(response.status().status()).isEqualTo(AttendanceStatus.WAITING);
      assertThat(response.status().statusFormatted()).isNotBlank();
      assertThat(response.attendanceInfo().auditInfo()).isNotNull();
      assertThat(response.qrValidationInfo().duration()).isEqualByComparingTo("2.00");
    }

    @Test
    @DisplayName("Should map AttendanceView to complex-search response correctly")
    void toComplexSearchResponseSuccess() {
      AttendanceView view = sampleView();
      var response = AttendancePresenter.toComplexSearchResponse(view, Locale.US, i18n);

      assertThat(response).isNotNull();
      assertThat(response.project().id()).isEqualTo(view.projectId());
      assertThat(response.project().name()).isEqualTo(view.projectName());
      assertThat(response.student().account().id()).isEqualTo(view.studentId());
      assertThat(response.student().account().email()).isEqualTo(view.studentEmail());
      assertThat(response.validator()).isNotNull();
      assertThat(response.validator().id()).isEqualTo(view.validatedById());
    }

    private AttendanceView sampleView() {
      OffsetDateTime now = OffsetDateTime.now();
      return new AttendanceView(
          UuidCreator.getTimeOrderedEpoch(),
          UuidCreator.getTimeOrderedEpoch(),
          "Project Name",
          UuidCreator.getTimeOrderedEpoch(),
          "Student Name",
          "student@example.com",
          "20260001",
          Campi.ITAJAI,
          new BigDecimal("2.00"),
          "hash-123",
          AttendanceStatus.WAITING,
          UuidCreator.getTimeOrderedEpoch(),
          "Validator Name",
          "validator@example.com",
          now,
          now,
          now);
    }
  }
}
