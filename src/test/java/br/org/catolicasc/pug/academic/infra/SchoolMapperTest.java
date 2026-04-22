package br.org.catolicasc.pug.academic.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.infra.persistence.SchoolEntity;
import br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView;
import java.time.OffsetDateTime;
import java.util.UUID;
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

  @Test
  @DisplayName("toDomain should return null when entity is null")
  void toDomainShouldReturnNullForNullEntity() {
    assertThat(SchoolMapper.toDomain(null)).isNull();
  }

  @Test
  @DisplayName("toEntity should return null when domain is null")
  void toEntityShouldReturnNullForNullDomain() {
    assertThat(SchoolMapper.toEntity(null)).isNull();
  }

  @Test
  @DisplayName("copy should do nothing when domain is null")
  void copyShouldHandleNullDomain() {
    SchoolEntity entity = SchoolEntity.builder().name("Original").build();
    SchoolMapper.copy(null, entity);
    assertThat(entity.getName()).isEqualTo("Original");
  }

  @Test
  @DisplayName("copy should do nothing when entity is null")
  void copyShouldHandleNullEntity() {
    School school = School.factory("Test");
    SchoolMapper.copy(school, null);
  }

  @Test
  @DisplayName("copy should update entity fields from domain")
  void copyShouldUpdateEntityFields() {
    School school = School.factory("Updated School");
    SchoolEntity entity = SchoolEntity.builder().name("Old").build();

    SchoolMapper.copy(school, entity);

    assertThat(entity.getName()).isEqualTo("Updated School");
    assertThat(entity.getCreatedAt()).isEqualTo(school.getAuditInfo().getCreatedAt());
    assertThat(entity.getUpdatedAt()).isEqualTo(school.getAuditInfo().getUpdatedAt());
  }

  @Test
  @DisplayName("toView should return null when entity is null")
  void toViewShouldReturnNullForNullEntity() {
    assertThat(SchoolMapper.toView(null)).isNull();
  }

  @Test
  @DisplayName("toView should map all fields correctly")
  void toViewShouldMapAllFields() {
    UUID id = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();
    SchoolEntity entity =
        SchoolEntity.builder().id(id).name("Engineering").createdAt(now).updatedAt(now).build();

    SchoolView view = SchoolMapper.toView(entity);

    assertThat(view).isNotNull();
    assertThat(view.id()).isEqualTo(id);
    assertThat(view.name()).isEqualTo("Engineering");
    assertThat(view.createdAt()).isEqualTo(now);
    assertThat(view.updatedAt()).isEqualTo(now);
  }
}
