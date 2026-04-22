package br.org.catolicasc.pug.project.presenter.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.infra.read.dtos.EnrollmentView;
import br.org.catolicasc.pug.project.presenter.dtos.EnrollmentCreateRequest;
import br.org.catolicasc.pug.project.presenter.dtos.EnrollmentResponse;
import br.org.catolicasc.pug.project.service.dtos.EnrollmentCreateCommand;
import br.org.catolicasc.pug.shared.i18n.I18n;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("EnrollmentPresenter Coverage")
class EnrollmentPresenterTest {

  @Inject I18n i18n;

  @Nested
  @DisplayName("Create Command Mapping")
  class CreateCommandTests {

    @Test
    @DisplayName("Should map EnrollmentCreateRequest to command")
    void toCommand() {
      UUID projectId = UUID.randomUUID();
      var req = new EnrollmentCreateRequest(projectId);

      EnrollmentCreateCommand cmd = EnrollmentPresenter.toCommand(req);

      assertThat(cmd).isNotNull();
      assertThat(cmd.projectId()).isEqualTo(projectId);
    }

    @Test
    @DisplayName("Should return null when request is null")
    void toCommandNull() {
      assertThat(EnrollmentPresenter.toCommand(null)).isNull();
    }
  }

  @Nested
  @DisplayName("Response Mapping")
  class ResponseTests {

    @Test
    @DisplayName("Should return null when view is null")
    void toResponseNullView() {
      assertThat(EnrollmentPresenter.toResponse(null, Locale.US, i18n)).isNull();
    }

    @Test
    @DisplayName("Should return null when locale is null")
    void toResponseNullLocale() {
      assertThat(EnrollmentPresenter.toResponse(sampleView(), null, i18n)).isNull();
    }

    @Test
    @DisplayName("Should return null when i18n is null")
    void toResponseNullI18n() {
      assertThat(EnrollmentPresenter.toResponse(sampleView(), Locale.US, null)).isNull();
    }

    @Test
    @DisplayName("Should map EnrollmentView to response correctly")
    void toResponseSuccess() {
      var view = sampleView();

      EnrollmentResponse response = EnrollmentPresenter.toResponse(view, Locale.US, i18n);

      assertThat(response).isNotNull();
      assertThat(response.projectId()).isEqualTo(view.projectId());
      assertThat(response.studentId()).isEqualTo(view.studentId());
      assertThat(response.status()).isEqualTo(EnrollmentStatus.PENDING);
      assertThat(response.statusFormatted()).isNotBlank();
      assertThat(response.auditInfo()).isNotNull();
    }

    @Test
    @DisplayName("Should handle null optional timestamps")
    void toResponseNullTimestamps() {
      var view = sampleView();

      EnrollmentResponse response = EnrollmentPresenter.toResponse(view, Locale.US, i18n);

      assertThat(response.acceptedAt()).isNull();
      assertThat(response.closingStatusAt()).isNull();
    }

    private EnrollmentView sampleView() {
      return new EnrollmentView(
          UUID.randomUUID(),
          UUID.randomUUID(),
          EnrollmentStatus.PENDING,
          OffsetDateTime.now(),
          OffsetDateTime.now(),
          null,
          null);
    }
  }
}
