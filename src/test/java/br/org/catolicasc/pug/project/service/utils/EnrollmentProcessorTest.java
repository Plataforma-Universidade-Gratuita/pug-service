package br.org.catolicasc.pug.project.service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.builders.domain.FormerStudentBuilder;
import br.org.catolicasc.pug.helpers.builders.domain.ProjectBuilder;
import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("EnrollmentProcessor Tests")
class EnrollmentProcessorTest {

  @Test
  @DisplayName("Should create enrollment with valid inputs")
  void processCreateInputValid() {
    var formerStudent = FormerStudentBuilder.aStudent().build();
    var project = ProjectBuilder.aProject().build();

    Enrollment enrollment = EnrollmentProcessor.processCreateInput(formerStudent, project);

    assertThat(enrollment).isNotNull();
    assertThat(enrollment.getIdentifier().getProjectId()).isEqualTo(project.getId());
    assertThat(enrollment.getIdentifier().getFormerStudentId())
        .isEqualTo(formerStudent.getAccountId());
    assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.PENDING);
    assertThat(enrollment.hasFieldErrors()).isFalse();
  }
}
