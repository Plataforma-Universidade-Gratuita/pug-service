package br.org.catolicasc.pug.project.service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.builders.domain.ProjectBuilder;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProjectProcessor Tests")
class ProjectProcessorTest {

  @Test
  @DisplayName("Should create project with valid inputs")
  void processCreateInputValid() {
    UUID entityId = UUID.randomUUID();
    UUID creatorId = UUID.randomUUID();

    Project project =
        ProjectProcessor.processCreateInput(
            "Test Project", entityId, "desc", creatorId, 20, new BigDecimal("40.00"));

    assertThat(project).isNotNull();
    assertThat(project.getName()).isEqualTo("Test Project");
    assertThat(project.getEntityId()).isEqualTo(entityId);
    assertThat(project.getDescription()).isEqualTo("desc");
    assertThat(project.getProjectStatus()).isEqualTo(ProjectStatus.PLANNED);
    assertThat(project.hasFieldErrors()).isFalse();
  }

  @Test
  @DisplayName("Should create project with validation errors for blank name")
  void processCreateInputBlankName() {
    Project project =
        ProjectProcessor.processCreateInput(
            "", UUID.randomUUID(), "desc", UUID.randomUUID(), 10, new BigDecimal("10"));

    assertThat(project.hasFieldErrors()).isTrue();
  }

  @Test
  @DisplayName("Should update only name when name provided")
  void processUpdateInputName() {
    Project existing = ProjectBuilder.aProject().withName("Original").build();

    Project updated = ProjectProcessor.processUpdateInput(existing, "New Name", null, null, null);

    assertThat(updated.getName()).isEqualTo("New Name");
    assertThat(updated.getDescription()).isEqualTo(existing.getDescription());
  }

  @Test
  @DisplayName("Should update only description when description provided")
  void processUpdateInputDescription() {
    Project existing = ProjectBuilder.aProject().build();

    Project updated =
        ProjectProcessor.processUpdateInput(existing, null, "New Description", null, null);

    assertThat(updated.getDescription()).isEqualTo("New Description");
    assertThat(updated.getName()).isEqualTo(existing.getName());
  }

  @Test
  @DisplayName("Should update max participants")
  void processUpdateInputMaxParticipants() {
    Project existing = ProjectBuilder.aProject().build();

    Project updated = ProjectProcessor.processUpdateInput(existing, null, null, 50, null);

    assertThat(updated.getProjectInfo().getMaxParticipants()).isEqualTo(50);
  }

  @Test
  @DisplayName("Should update offered hours")
  void processUpdateInputOfferedHours() {
    Project existing = ProjectBuilder.aProject().build();

    Project updated =
        ProjectProcessor.processUpdateInput(existing, null, null, null, new BigDecimal("100.00"));

    assertThat(updated.getProjectInfo().getOfferedHours())
        .isEqualByComparingTo(new BigDecimal("100.00"));
  }

  @Test
  @DisplayName("Should return same project when all fields are null")
  void processUpdateInputNoChanges() {
    Project existing = ProjectBuilder.aProject().build();

    Project updated = ProjectProcessor.processUpdateInput(existing, null, null, null, null);

    assertThat(updated.getName()).isEqualTo(existing.getName());
    assertThat(updated.getDescription()).isEqualTo(existing.getDescription());
  }

  @Test
  @DisplayName("Should update all fields simultaneously")
  void processUpdateInputAllFields() {
    Project existing = ProjectBuilder.aProject().build();

    Project updated =
        ProjectProcessor.processUpdateInput(
            existing, "Updated", "New Desc", 30, new BigDecimal("80.00"));

    assertThat(updated.getName()).isEqualTo("Updated");
    assertThat(updated.getDescription()).isEqualTo("New Desc");
    assertThat(updated.getProjectInfo().getMaxParticipants()).isEqualTo(30);
    assertThat(updated.getProjectInfo().getOfferedHours())
        .isEqualByComparingTo(new BigDecimal("80.00"));
  }
}
