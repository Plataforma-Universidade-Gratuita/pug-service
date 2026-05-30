package br.org.catolicasc.pug.project.presenter.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.infra.read.dtos.EnrollmentView;
import br.org.catolicasc.pug.project.presenter.dtos.EnrollmentResponse;
import br.org.catolicasc.pug.project.service.dtos.EnrollmentCreateCommand;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.i18n.I18n;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.LocalDate;
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
    @DisplayName("Should map project and student identifiers to command")
    void toCommand() {
      UUID projectId = UuidCreator.getTimeOrderedEpoch();
      UUID studentId = UuidCreator.getTimeOrderedEpoch();

      EnrollmentCreateCommand cmd = EnrollmentPresenter.toCommand(projectId, studentId);

      assertThat(cmd).isNotNull();
      assertThat(cmd.projectId()).isEqualTo(projectId);
      assertThat(cmd.studentId()).isEqualTo(studentId);
    }

    @Test
    @DisplayName("Should keep null identifiers untouched")
    void toCommandNull() {
      EnrollmentCreateCommand cmd = EnrollmentPresenter.toCommand(null, null);
      assertThat(cmd).isNotNull();
      assertThat(cmd.projectId()).isNull();
      assertThat(cmd.studentId()).isNull();
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
      assertThat(response.status().status()).isEqualTo(EnrollmentStatus.PENDING);
      assertThat(response.status().statusFormatted()).isNotBlank();
      assertThat(response.enrollmentInfo().auditInfo()).isNotNull();
    }

    @Test
    @DisplayName("Should handle null optional timestamps")
    void toResponseNullTimestamps() {
      var view = sampleView();

      EnrollmentResponse response = EnrollmentPresenter.toResponse(view, Locale.US, i18n);

      assertThat(response.enrollmentInfo().acceptedAt()).isNull();
      assertThat(response.enrollmentInfo().closingStatusAt()).isNull();
    }

    private EnrollmentView sampleView() {
      return new EnrollmentView(
          UuidCreator.getTimeOrderedEpoch(),
          "Project Name",
          UuidCreator.getTimeOrderedEpoch(),
          "Student Name",
          "student@example.com",
          "20260001",
          Campi.ITAJAI,
          LocalDate.now(),
          LocalDate.now().plusMonths(6),
          EnrollmentStatus.PENDING,
          OffsetDateTime.now(),
          OffsetDateTime.now(),
          null,
          null);
    }
  }
}
