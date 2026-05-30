package br.org.catolicasc.pug.academic.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.academic.infra.persistence.AreaOfExpertiseEntity;
import br.org.catolicasc.pug.academic.infra.read.dtos.AreaOfExpertiseView;
import com.github.f4b6a3.uuid.UuidCreator;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AreaOfExpertiseMapper Tests")
class AreaOfExpertiseMapperTest {

  @Test
  @DisplayName("Should perform round-trip mapping")
  void shouldPerformRoundTrip() {
    AreaOfExpertise original = AreaOfExpertise.factory("Engineering");

    AreaOfExpertiseEntity entity = AreaOfExpertiseMapper.toEntity(original);
    AreaOfExpertise mapped = AreaOfExpertiseMapper.toDomain(entity);

    assertThat(mapped).isNotNull();
    assertThat(mapped.getId()).isEqualTo(original.getId());
    assertThat(mapped.getName()).isEqualTo(original.getName());
    assertThat(mapped.getAuditInfo().getCreatedAt())
        .isEqualTo(original.getAuditInfo().getCreatedAt());
    assertThat(mapped.getAuditInfo().getUpdatedAt())
        .isEqualTo(original.getAuditInfo().getUpdatedAt());
  }

  @Test
  @DisplayName("toDomain should return null when entity is null")
  void toDomainShouldReturnNullForNullEntity() {
    assertThat(AreaOfExpertiseMapper.toDomain(null)).isNull();
  }

  @Test
  @DisplayName("toEntity should return null when domain is null")
  void toEntityShouldReturnNullForNullDomain() {
    assertThat(AreaOfExpertiseMapper.toEntity(null)).isNull();
  }

  @Test
  @DisplayName("copy should update entity fields from domain")
  void copyShouldUpdateEntityFields() {
    AreaOfExpertise areaOfExpertise = AreaOfExpertise.factory("Updated Area");
    AreaOfExpertiseEntity entity =
        AreaOfExpertiseEntity.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .name("Original")
            .createdAt(OffsetDateTime.now().minusDays(1))
            .updatedAt(OffsetDateTime.now().minusDays(1))
            .build();

    AreaOfExpertiseMapper.copy(areaOfExpertise, entity);

    assertThat(entity.getName()).isEqualTo("Updated Area");
    assertThat(entity.getCreatedAt()).isEqualTo(areaOfExpertise.getAuditInfo().getCreatedAt());
    assertThat(entity.getUpdatedAt()).isEqualTo(areaOfExpertise.getAuditInfo().getUpdatedAt());
  }

  @Test
  @DisplayName("copy should ignore null arguments")
  void copyShouldIgnoreNullArguments() {
    AreaOfExpertiseEntity entity =
        AreaOfExpertiseEntity.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .name("Original")
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    AreaOfExpertiseMapper.copy(null, entity);
    assertThat(entity.getName()).isEqualTo("Original");

    AreaOfExpertiseMapper.copy(AreaOfExpertise.factory("Updated"), null);
  }

  @Test
  @DisplayName("toView should map entity fields")
  void toViewShouldMapEntityFields() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    OffsetDateTime now = OffsetDateTime.now();
    AreaOfExpertiseEntity entity =
        AreaOfExpertiseEntity.builder()
            .id(id)
            .name("Engineering")
            .createdAt(now)
            .updatedAt(now)
            .build();

    AreaOfExpertiseView view = AreaOfExpertiseMapper.toView(entity);

    assertThat(view).isNotNull();
    assertThat(view.id()).isEqualTo(id);
    assertThat(view.name()).isEqualTo("Engineering");
    assertThat(view.createdAt()).isEqualTo(now);
    assertThat(view.updatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("toView should return null when entity is null")
  void toViewShouldReturnNullForNullEntity() {
    assertThat(AreaOfExpertiseMapper.toView(null)).isNull();
  }
}
