package br.org.catolicasc.pug.academic.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.infra.persistence.CourseEntity;
import br.org.catolicasc.pug.academic.infra.persistence.SchoolEntity;
import br.org.catolicasc.pug.academic.infra.read.dtos.CourseView;
import java.time.OffsetDateTime;
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

  @Test
  @DisplayName("toDomain should return null when entity is null")
  void toDomainShouldReturnNullForNullEntity() {
    assertThat(CourseMapper.toDomain(null)).isNull();
  }

  @Test
  @DisplayName("toEntity should return null when domain is null")
  void toEntityShouldReturnNullForNullDomain() {
    assertThat(CourseMapper.toEntity(null)).isNull();
  }

  @Test
  @DisplayName("copy should do nothing when domain is null")
  void copyShouldHandleNullDomain() {
    CourseEntity entity = CourseEntity.builder().name("Original").build();
    CourseMapper.copy(null, entity);
    assertThat(entity.getName()).isEqualTo("Original");
  }

  @Test
  @DisplayName("copy should do nothing when entity is null")
  void copyShouldHandleNullEntity() {
    Course course = Course.factory("Test", UUID.randomUUID());
    CourseMapper.copy(course, null);
  }

  @Test
  @DisplayName("copy should update entity fields from domain")
  void copyShouldUpdateEntityFields() {
    UUID schoolId = UUID.randomUUID();
    Course course = Course.factory("Updated Course", schoolId);
    CourseEntity entity = CourseEntity.builder().name("Old").schoolId(UUID.randomUUID()).build();

    CourseMapper.copy(course, entity);

    assertThat(entity.getName()).isEqualTo("Updated Course");
    assertThat(entity.getSchoolId()).isEqualTo(schoolId);
    assertThat(entity.getCreatedAt()).isEqualTo(course.getAuditInfo().getCreatedAt());
    assertThat(entity.getUpdatedAt()).isEqualTo(course.getAuditInfo().getUpdatedAt());
  }

  @Test
  @DisplayName("toView should return null when course entity is null")
  void toViewShouldReturnNullForNullCourse() {
    assertThat(CourseMapper.toView(null, null)).isNull();
  }

  @Test
  @DisplayName("toView should map correctly with school")
  void toViewShouldMapWithSchool() {
    UUID courseId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();

    CourseEntity courseEntity =
        CourseEntity.builder()
            .id(courseId)
            .name("Software Engineering")
            .schoolId(schoolId)
            .createdAt(now)
            .updatedAt(now)
            .build();
    SchoolEntity schoolEntity =
        SchoolEntity.builder()
            .id(schoolId)
            .name("School of Tech")
            .createdAt(now)
            .updatedAt(now)
            .build();

    CourseView view = CourseMapper.toView(courseEntity, schoolEntity);

    assertThat(view).isNotNull();
    assertThat(view.id()).isEqualTo(courseId);
    assertThat(view.name()).isEqualTo("Software Engineering");
    assertThat(view.school()).isNotNull();
    assertThat(view.school().name()).isEqualTo("School of Tech");
  }

  @Test
  @DisplayName("toView should handle null school")
  void toViewShouldHandleNullSchool() {
    UUID courseId = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();
    CourseEntity courseEntity =
        CourseEntity.builder()
            .id(courseId)
            .name("Course")
            .schoolId(UUID.randomUUID())
            .createdAt(now)
            .updatedAt(now)
            .build();

    CourseView view = CourseMapper.toView(courseEntity, null);

    assertThat(view).isNotNull();
    assertThat(view.school()).isNull();
  }

  @Test
  @DisplayName("Round-trip should preserve audit info")
  void roundTripShouldPreserveAuditInfo() {
    Course course = Course.factory("CS", UUID.randomUUID());

    CourseEntity entity = CourseMapper.toEntity(course);
    Course mapped = CourseMapper.toDomain(entity);

    assertThat(mapped.getAuditInfo().getCreatedAt())
        .isEqualTo(course.getAuditInfo().getCreatedAt());
    assertThat(mapped.getAuditInfo().getUpdatedAt())
        .isEqualTo(course.getAuditInfo().getUpdatedAt());
  }
}
