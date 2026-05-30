package br.org.catolicasc.pug.academic.presenter.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView;
import br.org.catolicasc.pug.academic.service.dtos.SchoolCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.SchoolUpdateCommand;
import com.github.f4b6a3.uuid.UuidCreator;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("SchoolPresenter Coverage")
class SchoolPresenterTest {

  @Nested
  @DisplayName("Create Command Mapping")
  class CreateCommandMapping {

    @Test
    @DisplayName("Should map request to command")
    void toCreateCommand() {
      SchoolCreateRequest req = new SchoolCreateRequest("Engineering");
      SchoolCreateCommand cmd = SchoolPresenter.toCommand(req);

      assertThat(cmd).isNotNull();
      assertThat(cmd.name()).isEqualTo("Engineering");
    }

    @Test
    @DisplayName("Should return null when request is null")
    void toCreateCommandNull() {
      assertThat(SchoolPresenter.toCommand((SchoolCreateRequest) null)).isNull();
    }
  }

  @Nested
  @DisplayName("Update Command Mapping")
  class UpdateCommandMapping {

    @Test
    @DisplayName("Should map update request to command")
    void toUpdateCommand() {
      SchoolUpdateRequest req = new SchoolUpdateRequest("New Name");
      SchoolUpdateCommand cmd = SchoolPresenter.toCommand(req);

      assertThat(cmd).isNotNull();
      assertThat(cmd.name()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("Should map partial update request (null name)")
    void toUpdateCommandPartial() {
      SchoolUpdateRequest req = new SchoolUpdateRequest(null);
      SchoolUpdateCommand cmd = SchoolPresenter.toCommand(req);

      assertThat(cmd).isNotNull();
      assertThat(cmd.name()).isNull();
    }

    @Test
    @DisplayName("Should return null when update request is null")
    void toUpdateCommandNull() {
      assertThat(SchoolPresenter.toCommand((SchoolUpdateRequest) null)).isNull();
    }
  }

  @Nested
  @DisplayName("Response Mapping")
  class ResponseMapping {

    @Test
    @DisplayName("Should return null when view is null")
    void toResponseNullView() {
      assertThat(SchoolPresenter.toResponse(null, Locale.US)).isNull();
    }

    @Test
    @DisplayName("Should return null when locale is null")
    void toResponseNullLocale() {
      SchoolView view =
          new SchoolView(
              UuidCreator.getTimeOrderedEpoch(), "Eng", OffsetDateTime.now(), OffsetDateTime.now());
      assertThat(SchoolPresenter.toResponse(view, null)).isNull();
    }

    @Test
    @DisplayName("Should map view to response correctly")
    void toResponseSuccess() {
      UUID id = UuidCreator.getTimeOrderedEpoch();
      OffsetDateTime now = OffsetDateTime.now();
      SchoolView view = new SchoolView(id, "Engineering", now, now);

      SchoolResponse response = SchoolPresenter.toResponse(view, Locale.US);

      assertThat(response).isNotNull();
      assertThat(response.id()).isEqualTo(id);
      assertThat(response.name()).isEqualTo("Engineering");
      assertThat(response.auditInfo()).isNotNull();
      assertThat(response.auditInfo().createdAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should handle null timestamps")
    void toResponseNullTimestamps() {
      SchoolView view = new SchoolView(UuidCreator.getTimeOrderedEpoch(), "Name", null, null);
      SchoolResponse response = SchoolPresenter.toResponse(view, Locale.US);

      assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("Should map view to lightweight complex-search response")
    void toComplexSearchResponseSuccess() {
      UUID id = UuidCreator.getTimeOrderedEpoch();
      SchoolView view =
          new SchoolView(id, "Engineering", OffsetDateTime.now(), OffsetDateTime.now());

      SchoolComplexSearchResponse response = SchoolPresenter.toComplexSearchResponse(view);

      assertThat(response).isNotNull();
      assertThat(response.id()).isEqualTo(id);
      assertThat(response.name()).isEqualTo("Engineering");
    }
  }
}
