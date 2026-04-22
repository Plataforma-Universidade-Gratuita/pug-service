package br.org.catolicasc.pug.project.domain.vos;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.project.domain.enums.ProjectsFieldErrorCodes;
import com.github.f4b6a3.uuid.UuidCreator;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ProjectInfo VO Tests")
class ProjectInfoTest {

  private final UUID creatorId = UuidCreator.getTimeOrderedEpoch();

  @Test
  @DisplayName("Should create valid info")
  void shouldCreate() {
    ProjectInfo info = ProjectInfo.factory(creatorId, 10, new BigDecimal("40"), BigDecimal.ZERO);
    assertThat(info.hasFieldErrors()).isFalse();
  }

  @Test
  @DisplayName("Should close project correctly")
  void shouldCloseProject() {
    ProjectInfo info = ProjectInfo.factory(creatorId, 10, BigDecimal.TEN, BigDecimal.ZERO);

    ProjectInfo closed = info.closeProject();

    assertThat(closed.getClosedAt()).isNotNull();

    ProjectInfo alreadyClosed = closed.closeProject();
    assertThat(alreadyClosed.getClosedAt()).isEqualTo(closed.getClosedAt());
  }

  @Nested
  @DisplayName("Change Methods")
  class ChangeMethodsTests {

    @Test
    @DisplayName("Should change max participants")
    void shouldChangeMaxParticipants() {
      ProjectInfo info = ProjectInfo.factory(creatorId, 10, BigDecimal.TEN, BigDecimal.ZERO);

      ProjectInfo updated = info.changeMaxParticipantsAllowed(20);

      assertThat(updated.getMaxParticipants()).isEqualTo(20);

      ProjectInfo same = updated.changeMaxParticipantsAllowed(20);
      assertThat(same).isEqualTo(updated);
    }

    @Test
    @DisplayName("Should change offered hours")
    void shouldChangeOfferedHours() {
      ProjectInfo info = ProjectInfo.factory(creatorId, 10, BigDecimal.TEN, BigDecimal.ZERO);

      ProjectInfo updated = info.changeOfferedHours(new BigDecimal("50.00"));

      assertThat(updated.getOfferedHours()).isEqualByComparingTo("50.00");

      ProjectInfo same = updated.changeOfferedHours(new BigDecimal("50.00"));
      assertThat(same).isEqualTo(updated);
    }
  }

  @Nested
  @DisplayName("Validation Tests")
  class ValidationTests {

    @Test
    @DisplayName("Should reject negative values")
    void shouldRejectNegativeValues() {
      ProjectInfo info =
          ProjectInfo.factory(creatorId, -1, new BigDecimal("-1.00"), new BigDecimal("-1.00"));

      assertThat(info.hasFieldErrors()).isTrue();
      assertThat(info.getFieldErrors())
          .contains(
              ProjectsFieldErrorCodes.INVALID_MAX_PARTICIPANTS_NEGATIVE,
              ProjectsFieldErrorCodes.INVALID_PROJECT_OFFERED_HOURS_NEGATIVE,
              ProjectsFieldErrorCodes.INVALID_PROJECT_COMPLETED_HOURS_NEGATIVE);
    }

    @Test
    @DisplayName("Should reject completed hours exceeding offered hours")
    void shouldRejectExceedingHours() {
      ProjectInfo info =
          ProjectInfo.factory(creatorId, 10, new BigDecimal("10.00"), new BigDecimal("20.00"));

      assertThat(info.hasFieldErrors()).isTrue();
      assertThat(info.getFieldErrors())
          .contains(ProjectsFieldErrorCodes.INVALID_PROJECT_COMPLETED_HOURS_EXCEEDS);
    }
  }
}
