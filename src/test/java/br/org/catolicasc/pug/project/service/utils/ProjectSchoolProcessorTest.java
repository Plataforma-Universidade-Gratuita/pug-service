package br.org.catolicasc.pug.project.service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.project.domain.ProjectSchool;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProjectSchoolProcessor Tests")
class ProjectSchoolProcessorTest {

  @Test
  @DisplayName("Should create valid ProjectSchool association")
  void processCreateInputValid() {
    UUID projectId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();

    ProjectSchool association = ProjectSchoolProcessor.processCreateInput(projectId, schoolId);

    assertThat(association).isNotNull();
    assertThat(association.getProjectId()).isEqualTo(projectId);
    assertThat(association.getSchoolId()).isEqualTo(schoolId);
    assertThat(association.hasFieldErrors()).isFalse();
  }

  @Test
  @DisplayName("Should create ProjectSchool with errors for null project ID")
  void processCreateInputNullProjectId() {
    ProjectSchool association = ProjectSchoolProcessor.processCreateInput(null, UUID.randomUUID());

    assertThat(association.hasFieldErrors()).isTrue();
  }

  @Test
  @DisplayName("Should create ProjectSchool with errors for null school ID")
  void processCreateInputNullSchoolId() {
    ProjectSchool association = ProjectSchoolProcessor.processCreateInput(UUID.randomUUID(), null);

    assertThat(association.hasFieldErrors()).isTrue();
  }
}
