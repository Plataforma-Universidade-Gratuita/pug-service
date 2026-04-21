package br.org.catolicasc.pug.academic.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.infra.persistence.SchoolEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SchoolMapper Tests")
class SchoolMapperTest {

  @Test
  @DisplayName("Should perform round-trip mapping for School")
  void shouldPerformRoundTrip() {
    School school = School.factory("School of Engineering");

    SchoolEntity entity = SchoolMapper.toEntity(school);
    School mapped = SchoolMapper.toDomain(entity);

    assertThat(mapped.getId()).isEqualTo(school.getId());
    assertThat(mapped.getName()).isEqualTo(school.getName());
  }
}
