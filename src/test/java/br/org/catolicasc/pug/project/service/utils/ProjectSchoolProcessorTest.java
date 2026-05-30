package br.org.catolicasc.pug.project.service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.project.domain.ProjectSchool;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProjectSchoolProcessor Tests")
class ProjectSchoolProcessorTest {

  @Test
  @DisplayName("Should create valid ProjectSchool association")
  void processCreateInputValid() {
    UUID projectId = UuidCreator.getTimeOrderedEpoch();
    UUID areaOfExpertiseId = UuidCreator.getTimeOrderedEpoch();

    ProjectSchool association = ProjectSchoolProcessor.processCreateInput(projectId, areaOfExpertiseId);

    assertThat(association).isNotNull();
    assertThat(association.getProjectId()).isEqualTo(projectId);
    assertThat(association.getSchoolId()).isEqualTo(areaOfExpertiseId);
    assertThat(association.hasFieldErrors()).isFalse();
  }

  @Test
  @DisplayName("Should create ProjectSchool with errors for null project ID")
  void processCreateInputNullProjectId() {
    ProjectSchool association =
        ProjectSchoolProcessor.processCreateInput(null, UuidCreator.getTimeOrderedEpoch());

    assertThat(association.hasFieldErrors()).isTrue();
  }

  @Test
  @DisplayName("Should create ProjectSchool with errors for null areaOfExpertise ID")
  void processCreateInputNullSchoolId() {
    ProjectSchool association =
        ProjectSchoolProcessor.processCreateInput(UuidCreator.getTimeOrderedEpoch(), null);

    assertThat(association.hasFieldErrors()).isTrue();
  }
}
