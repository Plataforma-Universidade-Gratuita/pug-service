package br.org.catolicasc.pug.project.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import br.org.catolicasc.pug.project.domain.enums.ProjectsFieldErrorCodes;
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
              "Desc",
              creatorId,
              10,
              new BigDecimal("40.0"),
              BigDecimal.ZERO);
      assertThat(project.hasFieldErrors()).isFalse();
      assertThat(project.getProjectStatus()).isEqualTo(ProjectStatus.PLANNED);
    }

    @Test
    @DisplayName("Should collect validation errors")
    void shouldCollectErrors() {
      Project project =
          Project.factory(
              "", null, "A".repeat(4001), creatorId, -1, new BigDecimal("-1"), BigDecimal.ZERO);
      assertThat(project.hasFieldErrors()).isTrue();
      assertThat(project.getFieldErrors())
          .contains(
              ProjectsFieldErrorCodes.INVALID_NAME_BLANK,
              ProjectsFieldErrorCodes.INVALID_PROJECT_CREATED_BY_BLANK,
              ProjectsFieldErrorCodes.INVALID_DESCRIPTION_TOO_LONG,
              ProjectsFieldErrorCodes.INVALID_MAX_PARTICIPANTS_NEGATIVE,
              ProjectsFieldErrorCodes.INVALID_PROJECT_OFFERED_HOURS_NEGATIVE);
    }
  }

  @Nested
  @DisplayName("Lifecycle Transitions")
  class TransitionTests {

    @Test
    @DisplayName("Should handle all lifecycle transitions")
    void shouldHandleTransitions() {
      Project p =
          Project.factory("Name", entityId, "Desc", creatorId, 10, BigDecimal.TEN, BigDecimal.ZERO);

      p = p.start();
      assertThat(p.getProjectStatus()).isEqualTo(ProjectStatus.IN_PROGRESS);

      p = p.putOnHold();
      assertThat(p.getProjectStatus()).isEqualTo(ProjectStatus.ON_HOLD);

      p = p.retake();
      assertThat(p.getProjectStatus()).isEqualTo(ProjectStatus.IN_PROGRESS);

      p = p.complete();
      assertThat(p.getProjectStatus()).isEqualTo(ProjectStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should cancel PLANNED project")
    void shouldCancelPlanned() {
      Project p =
          Project.factory("Name", entityId, "Desc", creatorId, 10, BigDecimal.TEN, BigDecimal.ZERO);
      p = p.cancel();
      assertThat(p.getProjectStatus()).isEqualTo(ProjectStatus.CANCELED);
    }

    @Test
    @DisplayName("Should handle idempotency")
    void shouldHandleIdempotency() {
      Project p =
          Project.factory("Name", entityId, "Desc", creatorId, 10, BigDecimal.TEN, BigDecimal.ZERO);
      assertThat(p.start().start()).isEqualTo(p.start());
      assertThat(p.cancel().cancel()).isEqualTo(p.cancel());
    }

    @Test
    @DisplayName("Should throw BusinessRuleException for invalid transitions")
    void shouldThrowInvalidTransitions() {
      Project p =
          Project.factory("Name", entityId, "Desc", creatorId, 10, BigDecimal.TEN, BigDecimal.ZERO);

      Assertions.assertThrows(BusinessRuleException.class, p::complete);
      Assertions.assertThrows(BusinessRuleException.class, p::retake);

      Project inProgress = p.start();
      Assertions.assertThrows(BusinessRuleException.class, inProgress::retake);
    }
  }

  @Nested
  @DisplayName("Update Methods")
  class UpdateTests {
    @Test
    @DisplayName("Should rename and change description")
    void shouldUpdateInfo() {
      Project p =
          Project.factory(
              "Old Name", entityId, "Old Desc", creatorId, 10, BigDecimal.TEN, BigDecimal.ZERO);

      Project renamed = p.rename("New Name");
      assertThat(renamed.getName()).isEqualTo("New Name");
      assertThat(p.rename("Old Name")).isEqualTo(p); // Idempotent

      Project descChanged = p.changeDescription("New Desc");
      assertThat(descChanged.getDescription()).isEqualTo("New Desc");
      assertThat(p.changeDescription("Old Desc")).isEqualTo(p); // Idempotent
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
      Project completed = project.start().addCompletedHours(new BigDecimal("10.0"));
      assertThat(completed.getProjectStatus()).isEqualTo(ProjectStatus.COMPLETED);
    }
  }
}
