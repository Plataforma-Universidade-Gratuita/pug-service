package br.org.catolicasc.pug.project.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.project.domain.enums.ProjectsFieldErrorCodes;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProjectByAreaOfExpertise Aggregate Tests")
class ProjectAreaOfExpertiseTest {

  @Test
  @DisplayName("Should create valid association")
  void shouldCreateValidAssociation() {
    UUID projectId = UuidCreator.getTimeOrderedEpoch();
    UUID areaOfExpertiseId = UuidCreator.getTimeOrderedEpoch();

    ProjectAreaOfExpertise pbs = ProjectAreaOfExpertise.factory(projectId, areaOfExpertiseId);

    assertThat(pbs.hasFieldErrors()).isFalse();
    assertThat(pbs.getProjectId()).isEqualTo(projectId);
    assertThat(pbs.getAreaOfExpertiseId()).isEqualTo(areaOfExpertiseId);
  }

  @Test
  @DisplayName("Should collect errors when IDs are missing")
  void shouldCollectValidationErrors() {
    ProjectAreaOfExpertise pbs = ProjectAreaOfExpertise.factory(null, null);

    assertThat(pbs.hasFieldErrors()).isTrue();
    assertThat(pbs.getFieldErrors())
        .contains(
            ProjectsFieldErrorCodes.INVALID_ENROLLMENT_PROJECT_BLANK,
            ProjectsFieldErrorCodes.INVALID_AREA_OF_EXPERTISE_ID_BLANK);
  }
}
