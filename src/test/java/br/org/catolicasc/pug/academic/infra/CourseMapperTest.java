package br.org.catolicasc.pug.academic.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.infra.persistence.CourseEntity;
import br.org.catolicasc.pug.academic.infra.persistence.SchoolEntity;
import br.org.catolicasc.pug.academic.infra.read.dtos.CourseView;
import br.org.catolicasc.pug.helpers.CopyableMapperTest;
import com.github.f4b6a3.uuid.UuidCreator;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CourseMapper Tests")
class CourseMapperTest extends CopyableMapperTest<Course, CourseEntity> {

  @Override
  protected Course createDomain() {
    return Course.factory("Software Engineering", UuidCreator.getTimeOrderedEpoch());
  }

  @Override
  protected CourseEntity createEntity() {
    return CourseEntity.builder()
        .name("Original")
        .schoolId(UuidCreator.getTimeOrderedEpoch())
        .build();
  }

  @Override
  protected Course mapToDomain(CourseEntity entity) {
    return CourseMapper.toDomain(entity);
  }

  @Override
  protected CourseEntity mapToEntity(Course domain) {
    return CourseMapper.toEntity(domain);
  }

  @Override
  protected void copy(Course domain, CourseEntity entity) {
    CourseMapper.copy(domain, entity);
  }

  @Override
  protected void assertRoundTrip(Course original, Course mapped) {
    assertThat(mapped.getId()).isEqualTo(original.getId());
    assertThat(mapped.getName()).isEqualTo(original.getName());
    assertThat(mapped.getSchoolId()).isEqualTo(original.getSchoolId());
    assertThat(mapped.getAuditInfo().getCreatedAt())
        .isEqualTo(original.getAuditInfo().getCreatedAt());
    assertThat(mapped.getAuditInfo().getUpdatedAt())
        .isEqualTo(original.getAuditInfo().getUpdatedAt());
  }

  @Test
  @DisplayName("copy should update entity fields from domain")
  void copyShouldUpdateEntityFields() {
    UUID schoolId = UuidCreator.getTimeOrderedEpoch();
    Course course = Course.factory("Updated Course", schoolId);
    CourseEntity entity = createEntity();

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
    UUID courseId = UuidCreator.getTimeOrderedEpoch();
    UUID schoolId = UuidCreator.getTimeOrderedEpoch();
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
    UUID courseId = UuidCreator.getTimeOrderedEpoch();
    OffsetDateTime now = OffsetDateTime.now();
    CourseEntity courseEntity =
        CourseEntity.builder()
            .id(courseId)
            .name("Course")
            .schoolId(UuidCreator.getTimeOrderedEpoch())
            .createdAt(now)
            .updatedAt(now)
            .build();

    CourseView view = CourseMapper.toView(courseEntity, null);

    assertThat(view).isNotNull();
    assertThat(view.school()).isNull();
  }
}
