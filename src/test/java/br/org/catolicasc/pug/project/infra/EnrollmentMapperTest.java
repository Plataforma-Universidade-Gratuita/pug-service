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

  @Test
  @DisplayName("toDomain should return null when entity is null")
  void toDomainShouldReturnNullForNullEntity() {
    assertThat(EnrollmentMapper.toDomain(null)).isNull();
  }

  @Test
  @DisplayName("toEntity should return null when domain is null")
  void toEntityShouldReturnNullForNullDomain() {
    assertThat(EnrollmentMapper.toEntity(null)).isNull();
  }

  @Test
  @DisplayName("copy should do nothing when domain is null")
  void copyShouldHandleNullDomain() {
    EnrollmentEntity entity = EnrollmentEntity.builder().status("PENDING").build();
    EnrollmentMapper.copy(null, entity);
    assertThat(entity.getStatus()).isEqualTo("PENDING");
  }

  @Test
  @DisplayName("copy should do nothing when entity is null")
  void copyShouldHandleNullEntity() {
    Enrollment enrollment = EnrollmentBuilder.anEnrollment().build();
    EnrollmentMapper.copy(enrollment, null);
  }

  @Test
  @DisplayName("copy should do nothing when both are null")
  void copyShouldHandleBothNull() {
    EnrollmentMapper.copy(null, null);
  }

  @Test
  @DisplayName("copy should update all entity fields from domain")
  void copyShouldUpdateEntityFields() {
    Enrollment enrollment = EnrollmentBuilder.anEnrollment().build();
    EnrollmentEntity entity = EnrollmentEntity.builder().status("OLD").build();

    EnrollmentMapper.copy(enrollment, entity);

    assertThat(entity.getStatus()).isEqualTo(enrollment.getStatus().name());
    assertThat(entity.getCreatedAt())
        .isEqualTo(enrollment.getEnrollmentInfo().getAuditInfo().getCreatedAt());
    assertThat(entity.getUpdatedAt())
        .isEqualTo(enrollment.getEnrollmentInfo().getAuditInfo().getUpdatedAt());
  }

  @Test
  @DisplayName("Round-trip should preserve enrollment info")
  void roundTripShouldPreserveEnrollmentInfo() {
    Enrollment enrollment = EnrollmentBuilder.anEnrollment().build();

    EnrollmentEntity entity = EnrollmentMapper.toEntity(enrollment);
    Enrollment mapped = EnrollmentMapper.toDomain(entity);

    assertThat(mapped.getIdentifier().getProjectId())
        .isEqualTo(enrollment.getIdentifier().getProjectId());
    assertThat(mapped.getIdentifier().getStudentId())
        .isEqualTo(enrollment.getIdentifier().getStudentId());
    assertThat(mapped.getStatus()).isEqualTo(enrollment.getStatus());
    assertThat(mapped.getEnrollmentInfo().getAuditInfo().getCreatedAt())
        .isEqualTo(enrollment.getEnrollmentInfo().getAuditInfo().getCreatedAt());
  }
}
