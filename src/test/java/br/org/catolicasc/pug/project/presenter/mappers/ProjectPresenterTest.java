package br.org.catolicasc.pug.project.presenter.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import br.org.catolicasc.pug.project.presenter.dtos.ProjectCreateRequest;
import br.org.catolicasc.pug.project.presenter.dtos.ProjectResponse;
import br.org.catolicasc.pug.project.presenter.dtos.ProjectUpdateRequest;
import br.org.catolicasc.pug.project.service.dtos.ProjectCreateCommand;
import br.org.catolicasc.pug.project.service.dtos.ProjectUpdateCommand;
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
@DisplayName("ProjectPresenter Coverage")
class ProjectPresenterTest {

  @Inject I18n i18n;

  @Nested
  @DisplayName("Create Command Mapping")
  class CreateCommandTests {

    @Test
    @DisplayName("Should map ProjectCreateRequest to ProjectCreateCommand")
    void toCreateCommand() {
      UUID entityId = UuidCreator.getTimeOrderedEpoch();
      var req = new ProjectCreateRequest("Project", entityId, "desc", 20, new BigDecimal("40.00"));

      ProjectCreateCommand cmd = ProjectPresenter.toCommand(req);

      assertThat(cmd).isNotNull();
      assertThat(cmd.name()).isEqualTo("Project");
      assertThat(cmd.entityId()).isEqualTo(entityId);
      assertThat(cmd.description()).isEqualTo("desc");
      assertThat(cmd.maxParticipants()).isEqualTo(20);
      assertThat(cmd.offeredHours()).isEqualByComparingTo(new BigDecimal("40.00"));
    }

    @Test
    @DisplayName("Should return null when request is null")
    void toCreateCommandNull() {
      assertThat(ProjectPresenter.toCommand((ProjectCreateRequest) null)).isNull();
    }
  }

  @Nested
  @DisplayName("Update Command Mapping")
  class UpdateCommandTests {

    @Test
    @DisplayName("Should map ProjectUpdateRequest to ProjectUpdateCommand")
    void toUpdateCommand() {
      var req = new ProjectUpdateRequest("New Name", "New Desc", 30, new BigDecimal("80"), null);

      ProjectUpdateCommand cmd = ProjectPresenter.toCommand(req);

      assertThat(cmd).isNotNull();
      assertThat(cmd.name()).isEqualTo("New Name");
      assertThat(cmd.description()).isEqualTo("New Desc");
      assertThat(cmd.maxParticipants()).isEqualTo(30);
      assertThat(cmd.offeredHours()).isEqualByComparingTo(new BigDecimal("80"));
    }

    @Test
    @DisplayName("Should map partial update with nulls")
    void toUpdateCommandPartial() {
      var req = new ProjectUpdateRequest(null, null, null, null, null);

      ProjectUpdateCommand cmd = ProjectPresenter.toCommand(req);

      assertThat(cmd).isNotNull();
      assertThat(cmd.name()).isNull();
      assertThat(cmd.description()).isNull();
    }

    @Test
    @DisplayName("Should return null when request is null")
    void toUpdateCommandNull() {
      assertThat(ProjectPresenter.toCommand((ProjectUpdateRequest) null)).isNull();
    }
  }

  @Nested
  @DisplayName("Response Mapping")
  class ResponseTests {

    @Test
    @DisplayName("Should return null when view is null")
    void toResponseNullView() {
      assertThat(ProjectPresenter.toResponse(null, Locale.US, i18n)).isNull();
    }

    @Test
    @DisplayName("Should return null when locale is null")
    void toResponseNullLocale() {
      var view = sampleView();
      assertThat(ProjectPresenter.toResponse(view, null, i18n)).isNull();
    }

    @Test
    @DisplayName("Should return null when i18n is null")
    void toResponseNullI18n() {
      var view = sampleView();
      assertThat(ProjectPresenter.toResponse(view, Locale.US, null)).isNull();
    }

    @Test
    @DisplayName("Should map ProjectView to ProjectResponse correctly")
    void toResponseSuccess() {
      var view = sampleView();

      ProjectResponse response = ProjectPresenter.toResponse(view, Locale.US, i18n);

      assertThat(response).isNotNull();
      assertThat(response.id()).isEqualTo(view.id());
      assertThat(response.name()).isEqualTo(view.name());
      assertThat(response.entityId()).isEqualTo(view.entityId());
      assertThat(response.description()).isEqualTo(view.description());
      assertThat(response.createdBy()).isEqualTo(view.creatorId());
      assertThat(response.maxParticipants()).isEqualTo(view.maxParticipants());
      assertThat(response.offeredHours()).isEqualByComparingTo(view.offeredHours());
      assertThat(response.completedHours()).isEqualByComparingTo(view.completedHours());
      assertThat(response.status()).isEqualTo(ProjectStatus.PLANNED);
      assertThat(response.statusFormatted()).isNotBlank();
      assertThat(response.auditInfo()).isNotNull();
    }

    @Test
    @DisplayName("Should handle null closedAt gracefully")
    void toResponseNullClosedAt() {
      var view = sampleView();

      ProjectResponse response = ProjectPresenter.toResponse(view, Locale.US, i18n);

      assertThat(response.closedAt()).isNull();
    }

    private ProjectView sampleView() {
      return new ProjectView(
          UuidCreator.getTimeOrderedEpoch(),
          "Test Project",
          UuidCreator.getTimeOrderedEpoch(),
          "desc",
          UuidCreator.getTimeOrderedEpoch(),
          20,
          new BigDecimal("40.00"),
          BigDecimal.ZERO,
          ProjectStatus.PLANNED,
          null,
          OffsetDateTime.now(),
          OffsetDateTime.now());
    }
  }
}
