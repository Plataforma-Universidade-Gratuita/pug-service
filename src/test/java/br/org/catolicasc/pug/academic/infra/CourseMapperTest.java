package br.org.catolicasc.pug.academic.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.infra.persistence.CourseEntity;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CourseMapper Tests")
class CourseMapperTest {

  @Test
  @DisplayName("Should perform round-trip mapping for Course")
  void shouldPerformRoundTrip() {
    Course course = Course.factory("Software Engineering", UUID.randomUUID());

    CourseEntity entity = CourseMapper.toEntity(course);
    Course mapped = CourseMapper.toDomain(entity);

    assertThat(mapped.getId()).isEqualTo(course.getId());
    assertThat(mapped.getName()).isEqualTo(course.getName());
    assertThat(mapped.getSchoolId()).isEqualTo(course.getSchoolId());
  }
}
