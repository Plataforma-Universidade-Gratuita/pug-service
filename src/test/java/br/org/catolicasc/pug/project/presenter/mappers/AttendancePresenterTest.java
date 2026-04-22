package br.org.catolicasc.pug.project.presenter.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import br.org.catolicasc.pug.project.infra.read.dtos.AttendanceView;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceCreateRequest;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceResponse;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceValidateRequest;
import br.org.catolicasc.pug.project.service.dtos.AttendanceCreateCommand;
import br.org.catolicasc.pug.project.service.dtos.AttendanceValidateCommand;
import br.org.catolicasc.pug.shared.i18n.I18n;
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
      UUID pid = UUID.randomUUID();
      UUID sid = UUID.randomUUID();
      var req = new AttendanceCreateRequest(pid, sid, new BigDecimal("2.00"));

      AttendanceCreateCommand cmd = AttendancePresenter.toCommand(req);

      assertThat(cmd).isNotNull();
      assertThat(cmd.projectId()).isEqualTo(pid);
      assertThat(cmd.studentId()).isEqualTo(sid);
      assertThat(cmd.duration()).isEqualByComparingTo(new BigDecimal("2.00"));
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
      var req = new AttendanceValidateRequest(AttendanceStatus.PRESENT, "hash-123");

      AttendanceValidateCommand cmd = AttendancePresenter.toCommand(req);

      assertThat(cmd).isNotNull();
      assertThat(cmd.status()).isEqualTo(AttendanceStatus.PRESENT);
      assertThat(cmd.qrValidationHash()).isEqualTo("hash-123");
    }

    @Test
    @DisplayName("Should return null when request is null")
    void toCommandNull() {
      assertThat(AttendancePresenter.toCommand((AttendanceValidateRequest) null)).isNull();
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
    @DisplayName("Should return null when locale is null")
    void toResponseNullLocale() {
      assertThat(AttendancePresenter.toResponse(sampleView(), null, i18n)).isNull();
    }

    @Test
    @DisplayName("Should return null when i18n is null")
    void toResponseNullI18n() {
      assertThat(AttendancePresenter.toResponse(sampleView(), Locale.US, null)).isNull();
    }

    @Test
    @DisplayName("Should map AttendanceView to response correctly")
    void toResponseSuccess() {
      var view = sampleView();

      AttendanceResponse response = AttendancePresenter.toResponse(view, Locale.US, i18n);

      assertThat(response).isNotNull();
      assertThat(response.id()).isEqualTo(view.id());
      assertThat(response.projectId()).isEqualTo(view.projectId());
      assertThat(response.studentId()).isEqualTo(view.studentId());
      assertThat(response.duration()).isEqualByComparingTo(view.duration());
      assertThat(response.qrValidationHash()).isEqualTo(view.qrValidationHash());
      assertThat(response.status()).isEqualTo(AttendanceStatus.WAITING);
      assertThat(response.statusFormatted()).isNotBlank();
      assertThat(response.auditInfo()).isNotNull();
    }

    @Test
    @DisplayName("Should handle null validated timestamps")
    void toResponseNullValidated() {
      var view = sampleView();

      AttendanceResponse response = AttendancePresenter.toResponse(view, Locale.US, i18n);

      assertThat(response.validatedById()).isNull();
      assertThat(response.validatedAt()).isNull();
    }

    private AttendanceView sampleView() {
      return new AttendanceView(
          UUID.randomUUID(),
          UUID.randomUUID(),
          UUID.randomUUID(),
          new BigDecimal("2.00"),
          "hash-123",
          AttendanceStatus.WAITING,
          null,
          null,
          OffsetDateTime.now(),
          OffsetDateTime.now());
    }
  }
}
