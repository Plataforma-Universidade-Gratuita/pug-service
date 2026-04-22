package br.org.catolicasc.pug.project.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.project.domain.enums.ProjectsFieldErrorCodes;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProjectBySchool Aggregate Tests")
class ProjectSchoolTest {

  @Test
  @DisplayName("Should create valid association")
  void shouldCreateValidAssociation() {
    UUID projectId = UuidCreator.getTimeOrderedEpoch();
    UUID schoolId = UuidCreator.getTimeOrderedEpoch();

    ProjectSchool pbs = ProjectSchool.factory(projectId, schoolId);

    assertThat(pbs.hasFieldErrors()).isFalse();
    assertThat(pbs.getProjectId()).isEqualTo(projectId);
    assertThat(pbs.getSchoolId()).isEqualTo(schoolId);
  }

  @Test
  @DisplayName("Should collect errors when IDs are missing")
  void shouldCollectValidationErrors() {
    ProjectSchool pbs = ProjectSchool.factory(null, null);

    assertThat(pbs.hasFieldErrors()).isTrue();
    assertThat(pbs.getFieldErrors())
        .contains(
            ProjectsFieldErrorCodes.INVALID_ENROLLMENT_PROJECT_BLANK,
            ProjectsFieldErrorCodes.INVALID_SCHOOL_ID_BLANK);
  }
}
