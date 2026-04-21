package br.org.catolicasc.pug.project.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.builders.EnrollmentBuilder;
import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.infra.persistence.EnrollmentEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("EnrollmentMapper Tests")
class EnrollmentMapperTest {

  @Test
  @DisplayName("Should perform round-trip mapping for Enrollment")
  void shouldPerformRoundTrip() {
    Enrollment enrollment = EnrollmentBuilder.anEnrollment().build();

    EnrollmentEntity entity = EnrollmentMapper.toEntity(enrollment);
    Enrollment mapped = EnrollmentMapper.toDomain(entity);

    assertThat(mapped.getIdentifier()).isEqualTo(enrollment.getIdentifier());
    assertThat(mapped.getStatus()).isEqualTo(enrollment.getStatus());
  }
}
