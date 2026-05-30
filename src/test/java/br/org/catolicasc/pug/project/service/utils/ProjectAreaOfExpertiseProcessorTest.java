package br.org.catolicasc.pug.project.service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.project.domain.ProjectAreaOfExpertise;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProjectAreaOfExpertiseProcessor Tests")
class ProjectAreaOfExpertiseProcessorTest {

  @Test
  @DisplayName("Should create valid ProjectAreaOfExpertise association")
  void processCreateInputValid() {
    UUID projectId = UuidCreator.getTimeOrderedEpoch();
    UUID areaOfExpertiseId = UuidCreator.getTimeOrderedEpoch();

    ProjectAreaOfExpertise association =
        ProjectAreaOfExpertiseProcessor.processCreateInput(projectId, areaOfExpertiseId);

    assertThat(association).isNotNull();
    assertThat(association.getProjectId()).isEqualTo(projectId);
    assertThat(association.getAreaOfExpertiseId()).isEqualTo(areaOfExpertiseId);
    assertThat(association.hasFieldErrors()).isFalse();
  }

  @Test
  @DisplayName("Should create ProjectAreaOfExpertise with errors for null project ID")
  void processCreateInputNullProjectId() {
    ProjectAreaOfExpertise association =
        ProjectAreaOfExpertiseProcessor.processCreateInput(null, UuidCreator.getTimeOrderedEpoch());

    assertThat(association.hasFieldErrors()).isTrue();
  }

  @Test
  @DisplayName("Should create ProjectAreaOfExpertise with errors for null areaOfExpertise ID")
  void processCreateInputNullAreaOfExpertiseId() {
    ProjectAreaOfExpertise association =
        ProjectAreaOfExpertiseProcessor.processCreateInput(UuidCreator.getTimeOrderedEpoch(), null);

    assertThat(association.hasFieldErrors()).isTrue();
  }
}
