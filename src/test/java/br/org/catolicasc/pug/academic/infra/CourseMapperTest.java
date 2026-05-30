package br.org.catolicasc.pug.academic.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.infra.persistence.AreaOfExpertiseEntity;
import br.org.catolicasc.pug.academic.infra.persistence.CourseEntity;
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
        .areaOfExpertiseId(UuidCreator.getTimeOrderedEpoch())
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
    assertThat(mapped.getAreaOfExpertiseId()).isEqualTo(original.getAreaOfExpertiseId());
    assertThat(mapped.getAuditInfo().getCreatedAt())
        .isEqualTo(original.getAuditInfo().getCreatedAt());
    assertThat(mapped.getAuditInfo().getUpdatedAt())
        .isEqualTo(original.getAuditInfo().getUpdatedAt());
  }

  @Test
  @DisplayName("copy should update entity fields from domain")
  void copyShouldUpdateEntityFields() {
    UUID areaOfExpertiseId = UuidCreator.getTimeOrderedEpoch();
    Course course = Course.factory("Updated Course", areaOfExpertiseId);
    CourseEntity entity = createEntity();

    CourseMapper.copy(course, entity);

    assertThat(entity.getName()).isEqualTo("Updated Course");
    assertThat(entity.getAreaOfExpertiseId()).isEqualTo(areaOfExpertiseId);
    assertThat(entity.getCreatedAt()).isEqualTo(course.getAuditInfo().getCreatedAt());
    assertThat(entity.getUpdatedAt()).isEqualTo(course.getAuditInfo().getUpdatedAt());
  }

  @Test
  @DisplayName("toView should return null when course entity is null")
  void toViewShouldReturnNullForNullCourse() {
    assertThat(CourseMapper.toView(null, null)).isNull();
  }

  @Test
  @DisplayName("toView should map correctly with areaOfExpertise")
  void toViewShouldMapWithAreaOfExpertise() {
    UUID courseId = UuidCreator.getTimeOrderedEpoch();
    UUID areaOfExpertiseId = UuidCreator.getTimeOrderedEpoch();
    OffsetDateTime now = OffsetDateTime.now();

    CourseEntity courseEntity =
        CourseEntity.builder()
            .id(courseId)
            .name("Software Engineering")
            .areaOfExpertiseId(areaOfExpertiseId)
            .createdAt(now)
            .updatedAt(now)
            .build();
    AreaOfExpertiseEntity areaOfExpertiseEntity =
        AreaOfExpertiseEntity.builder()
            .id(areaOfExpertiseId)
            .name("AreaOfExpertise of Tech")
            .createdAt(now)
            .updatedAt(now)
            .build();

    CourseView view = CourseMapper.toView(courseEntity, areaOfExpertiseEntity);

    assertThat(view).isNotNull();
    assertThat(view.id()).isEqualTo(courseId);
    assertThat(view.name()).isEqualTo("Software Engineering");
    assertThat(view.areaOfExpertise()).isNotNull();
    assertThat(view.areaOfExpertise().name()).isEqualTo("AreaOfExpertise of Tech");
  }

  @Test
  @DisplayName("toView should handle null areaOfExpertise")
  void toViewShouldHandleNullAreaOfExpertise() {
    UUID courseId = UuidCreator.getTimeOrderedEpoch();
    OffsetDateTime now = OffsetDateTime.now();
    CourseEntity courseEntity =
        CourseEntity.builder()
            .id(courseId)
            .name("Course")
            .areaOfExpertiseId(UuidCreator.getTimeOrderedEpoch())
            .createdAt(now)
            .updatedAt(now)
            .build();

    CourseView view = CourseMapper.toView(courseEntity, null);

    assertThat(view).isNotNull();
    assertThat(view.areaOfExpertise()).isNull();
  }
}
