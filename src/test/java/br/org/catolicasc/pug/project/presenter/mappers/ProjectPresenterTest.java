package br.org.catolicasc.pug.project.presenter.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import br.org.catolicasc.pug.project.presenter.dtos.projects.ProjectCreateRequest;
import br.org.catolicasc.pug.project.presenter.dtos.projects.ProjectResponse;
import br.org.catolicasc.pug.project.presenter.dtos.projects.ProjectUpdateRequest;
import br.org.catolicasc.pug.project.service.dtos.projects.ProjectCreateCommand;
import br.org.catolicasc.pug.project.service.dtos.projects.ProjectUpdateCommand;
import br.org.catolicasc.pug.shared.i18n.I18n;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("ProjectPresenter Coverage")
class ProjectPresenterTest {

  @Inject I18n i18n;

  @Test
  void toCreateCommand() {
    UUID entityId = UuidCreator.getTimeOrderedEpoch();
    ProjectCreateCommand cmd =
        ProjectPresenter.toCommand(
            new ProjectCreateRequest("Project", entityId, "desc", 20, new BigDecimal("40.00")));

    assertThat(cmd.name()).isEqualTo("Project");
    assertThat(cmd.entityId()).isEqualTo(entityId);
  }

  @Test
  void toUpdateCommand() {
    ProjectUpdateCommand cmd =
        ProjectPresenter.toCommand(
            new ProjectUpdateRequest("New Name", "New Desc", 30, new BigDecimal("80")));

    assertThat(cmd.name()).isEqualTo("New Name");
    assertThat(cmd.offeredHours()).isEqualByComparingTo("80");
  }

  @Test
  void toResponseSuccess() {
    ProjectView view = sampleView();

    ProjectResponse response = ProjectPresenter.toResponse(view, Locale.US, i18n);

    assertThat(response.id()).isEqualTo(view.id());
    assertThat(response.entity().id()).isEqualTo(view.entityId());
    assertThat(response.entity().name()).isEqualTo(view.entityName());
    assertThat(response.projectInfo().createdBy()).isNotNull();
    assertThat(response.projectInfo().createdBy().id()).isEqualTo(view.creatorId());
    assertThat(response.projectInfo().createdBy().name()).isEqualTo(view.creatorName());
    assertThat(response.projectInfo().createdBy().email()).isEqualTo(view.creatorEmail());
    assertThat(response.status().status()).isEqualTo(ProjectStatus.PLANNED);
    assertThat(response.status().statusFormatted()).isNotBlank();
    assertThat(response.projectInfo().auditInfo()).isNotNull();
  }

  private ProjectView sampleView() {
    return new ProjectView(
        UuidCreator.getTimeOrderedEpoch(),
        "Test Project",
        UuidCreator.getTimeOrderedEpoch(),
        "Entity Name",
        "desc",
        UuidCreator.getTimeOrderedEpoch(),
        "Creator Name",
        "creator@example.com",
        20,
        new BigDecimal("40.00"),
        BigDecimal.ZERO,
        ProjectStatus.PLANNED,
        null,
        OffsetDateTime.now(),
        OffsetDateTime.now());
  }
}
