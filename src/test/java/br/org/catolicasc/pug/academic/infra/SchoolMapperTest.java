package br.org.catolicasc.pug.academic.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.infra.persistence.SchoolEntity;
import br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView;
import br.org.catolicasc.pug.helpers.CopyableMapperTest;
import com.github.f4b6a3.uuid.UuidCreator;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SchoolMapper Tests")
class SchoolMapperTest extends CopyableMapperTest<School, SchoolEntity> {

  @Override
  protected School createDomain() {
    return School.factory("School of Engineering");
  }

  @Override
  protected SchoolEntity createEntity() {
    return SchoolEntity.builder().name("Original").build();
  }

  @Override
  protected School mapToDomain(SchoolEntity entity) {
    return SchoolMapper.toDomain(entity);
  }

  @Override
  protected SchoolEntity mapToEntity(School domain) {
    return SchoolMapper.toEntity(domain);
  }

  @Override
  protected void copy(School domain, SchoolEntity entity) {
    SchoolMapper.copy(domain, entity);
  }

  @Override
  protected void assertRoundTrip(School original, School mapped) {
    assertThat(mapped.getId()).isEqualTo(original.getId());
    assertThat(mapped.getName()).isEqualTo(original.getName());
  }

  @Test
  @DisplayName("copy should update entity fields from domain")
  void copyShouldUpdateEntityFields() {
    School school = School.factory("Updated School");
    SchoolEntity entity = createEntity();

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
    UUID id = UuidCreator.getTimeOrderedEpoch();
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
