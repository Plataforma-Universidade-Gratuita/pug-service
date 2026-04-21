package br.org.catolicasc.pug.project.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Project Aggregate Tests")
class ProjectTest {

  private final UUID entityId = UUID.randomUUID();
  private final UUID creatorId = UUID.randomUUID();

  @Nested
  @DisplayName("Factory and Validation")
  class FactoryTests {

    @Test
    @DisplayName("Should create valid Project in PLANNED status")
    void shouldCreateValidProject() {
      Project project =
          Project.factory(
              "Project Alpha",
              entityId,
              "Description",
              creatorId,
              10,
              new BigDecimal("40.0"),
              BigDecimal.ZERO);

      assertThat(project.hasFieldErrors()).isFalse();
      assertThat(project.getProjectStatus()).isEqualTo(ProjectStatus.PLANNED);
    }
  }

  @Nested
  @DisplayName("Lifecycle Transitions")
  class TransitionTests {

    @Test
    @DisplayName("Should start PLANNED project to IN_PROGRESS")
    void shouldStartProject() {
      Project project =
          Project.factory("Name", entityId, "Desc", creatorId, 10, BigDecimal.TEN, BigDecimal.ZERO);

      Project started = project.start();
      assertThat(started.getProjectStatus()).isEqualTo(ProjectStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Should fail to cancel COMPLETED project")
    void shouldFailToCancelCompleted() {
      Project project =
          Project.factory("Name", entityId, "Desc", creatorId, 10, BigDecimal.TEN, BigDecimal.ZERO);

      Project completed = project.start().complete();

      Assertions.assertThrows(BusinessRuleException.class, completed::cancel);
    }
  }

  @Nested
  @DisplayName("Progress Management")
  class ProgressTests {

    @Test
    @DisplayName("Should complete project when hours are reached")
    void shouldCompleteOnHoursReached() {
      Project project =
          Project.factory(
              "Name", entityId, "Desc", creatorId, 10, new BigDecimal("10.0"), BigDecimal.ZERO);

      Project inProgress = project.start();
      Project completed = inProgress.addCompletedHours(new BigDecimal("10.0"));

      assertThat(completed.getProjectStatus()).isEqualTo(ProjectStatus.COMPLETED);
    }
  }
}
