package br.org.catolicasc.pug.academic.presenter.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.infra.read.dtos.CourseView;
import br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView;
import br.org.catolicasc.pug.academic.presenter.dtos.CourseComplexSearchResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.CourseCreateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.CourseResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.CourseUpdateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.CourseWithAuditInfoComplexSearchResponse;
import br.org.catolicasc.pug.academic.service.dtos.CourseCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.CourseUpdateCommand;
import com.github.f4b6a3.uuid.UuidCreator;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("CoursePresenter Coverage")
class CoursePresenterTest {

  @Nested
  @DisplayName("Create Command Mapping")
  class CreateCommandMapping {

    @Test
    @DisplayName("Should map request to command")
    void toCreateCommand() {
      UUID schoolId = UuidCreator.getTimeOrderedEpoch();
      CourseCreateRequest req = new CourseCreateRequest("CS", schoolId);
      CourseCreateCommand cmd = CoursePresenter.toCommand(req);

      assertThat(cmd).isNotNull();
      assertThat(cmd.name()).isEqualTo("CS");
      assertThat(cmd.schoolId()).isEqualTo(schoolId);
    }

    @Test
    @DisplayName("Should return null when request is null")
    void toCreateCommandNull() {
      assertThat(CoursePresenter.toCommand((CourseCreateRequest) null)).isNull();
    }
  }

  @Nested
  @DisplayName("Update Command Mapping")
  class UpdateCommandMapping {

    @Test
    @DisplayName("Should map update request to command")
    void toUpdateCommand() {
      UUID schoolId = UuidCreator.getTimeOrderedEpoch();
      CourseUpdateRequest req = new CourseUpdateRequest("New Name", schoolId);
      CourseUpdateCommand cmd = CoursePresenter.toCommand(req);

      assertThat(cmd).isNotNull();
      assertThat(cmd.name()).isEqualTo("New Name");
      assertThat(cmd.schoolId()).isEqualTo(schoolId);
    }

    @Test
    @DisplayName("Should map partial update request")
    void toUpdateCommandPartial() {
      CourseUpdateRequest req = new CourseUpdateRequest(null, null);
      CourseUpdateCommand cmd = CoursePresenter.toCommand(req);

      assertThat(cmd).isNotNull();
      assertThat(cmd.name()).isNull();
      assertThat(cmd.schoolId()).isNull();
    }

    @Test
    @DisplayName("Should return null when request is null")
    void toUpdateCommandNull() {
      assertThat(CoursePresenter.toCommand((CourseUpdateRequest) null)).isNull();
    }
  }

  @Nested
  @DisplayName("Response Mapping")
  class ResponseMapping {

    @Test
    @DisplayName("Should return null when view is null")
    void toResponseNullView() {
      assertThat(CoursePresenter.toResponse(null, Locale.US)).isNull();
    }

    @Test
    @DisplayName("Should return null when locale is null")
    void toResponseNullLocale() {
      CourseView view = buildCourseView();
      assertThat(CoursePresenter.toResponse(view, null)).isNull();
    }

    @Test
    @DisplayName("Should map view to response correctly with nested area of expertise")
    void toResponseSuccess() {
      CourseView view = buildCourseView();
      CourseResponse response = CoursePresenter.toResponse(view, Locale.US);

      assertThat(response).isNotNull();
      assertThat(response.name()).isEqualTo("Computer Science");
      assertThat(response.areaOfExpertise()).isNotNull();
      assertThat(response.areaOfExpertise().name()).isEqualTo("Engineering");
      assertThat(response.auditInfo()).isNotNull();
    }

    @Test
    @DisplayName("Should handle null area of expertise in view")
    void toResponseNullAreaOfExpertise() {
      OffsetDateTime now = OffsetDateTime.now();
      CourseView view = new CourseView(UuidCreator.getTimeOrderedEpoch(), "CS", null, now, now);
      CourseResponse response = CoursePresenter.toResponse(view, Locale.US);

      assertThat(response).isNotNull();
      assertThat(response.areaOfExpertise()).isNull();
    }

    @Test
    @DisplayName("Should map view to lightweight complex-search response")
    void toComplexSearchResponseSuccess() {
      CourseView view = buildCourseView();
      CourseComplexSearchResponse response = CoursePresenter.toComplexSearchResponse(view);

      assertThat(response).isNotNull();
      assertThat(response.name()).isEqualTo("Computer Science");
      assertThat(response.areaOfExpertise()).isNotNull();
      assertThat(response.areaOfExpertise().name()).isEqualTo("Engineering");
    }

    @Test
    @DisplayName("Should map view to course search response with audit info")
    void toWithAuditInfoComplexSearchResponseSuccess() {
      CourseView view = buildCourseView();
      CourseWithAuditInfoComplexSearchResponse response =
          CoursePresenter.toWithAuditInfoComplexSearchResponse(view, Locale.US);

      assertThat(response).isNotNull();
      assertThat(response.name()).isEqualTo("Computer Science");
      assertThat(response.areaOfExpertise()).isNotNull();
      assertThat(response.auditInfo()).isNotNull();
    }

    private CourseView buildCourseView() {
      OffsetDateTime now = OffsetDateTime.now();
      SchoolView schoolView =
          new SchoolView(UuidCreator.getTimeOrderedEpoch(), "Engineering", now, now);
      return new CourseView(
          UuidCreator.getTimeOrderedEpoch(), "Computer Science", schoolView, now, now);
    }
  }
}
