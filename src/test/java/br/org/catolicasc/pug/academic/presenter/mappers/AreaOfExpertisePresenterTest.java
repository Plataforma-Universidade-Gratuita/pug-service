package br.org.catolicasc.pug.academic.presenter.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView;
import br.org.catolicasc.pug.academic.presenter.dtos.AreaOfExpertiseComplexSearchResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.AreaOfExpertiseCreateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.AreaOfExpertiseResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.AreaOfExpertiseUpdateRequest;
import br.org.catolicasc.pug.academic.service.dtos.AreaOfExpertiseCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.AreaOfExpertiseUpdateCommand;
import com.github.f4b6a3.uuid.UuidCreator;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AreaOfExpertisePresenter Coverage")
class AreaOfExpertisePresenterTest {

  @Test
  @DisplayName("Should map create request to command")
  void toCreateCommand() {
    AreaOfExpertiseCreateCommand command =
        AreaOfExpertisePresenter.toCommand(new AreaOfExpertiseCreateRequest("Engineering"));
    assertThat(command).isNotNull();
    assertThat(command.name()).isEqualTo("Engineering");
  }

  @Test
  @DisplayName("Should map update request to command")
  void toUpdateCommand() {
    AreaOfExpertiseUpdateCommand command =
        AreaOfExpertisePresenter.toCommand(new AreaOfExpertiseUpdateRequest("New Name"));
    assertThat(command).isNotNull();
    assertThat(command.name()).isEqualTo("New Name");
  }

  @Test
  @DisplayName("Should map view to response")
  void toResponse() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    OffsetDateTime now = OffsetDateTime.now();
    SchoolView view = new SchoolView(id, "Engineering", now, now);
    AreaOfExpertiseResponse response = AreaOfExpertisePresenter.toResponse(view, Locale.US);
    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(id);
    assertThat(response.name()).isEqualTo("Engineering");
    assertThat(response.auditInfo()).isNotNull();
  }

  @Test
  @DisplayName("Should map view to lightweight complex-search response")
  void toComplexSearchResponse() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    SchoolView view = new SchoolView(id, "Engineering", OffsetDateTime.now(), OffsetDateTime.now());
    AreaOfExpertiseComplexSearchResponse response =
        AreaOfExpertisePresenter.toComplexSearchResponse(view);
    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(id);
    assertThat(response.name()).isEqualTo("Engineering");
  }
}
