package br.org.catolicasc.pug.project.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.CopyableMapperTest;
import br.org.catolicasc.pug.helpers.builders.domain.EnrollmentBuilder;
import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.infra.persistence.EnrollmentEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("EnrollmentMapper Tests")
class EnrollmentMapperTest extends CopyableMapperTest<Enrollment, EnrollmentEntity> {

  @Override
  protected Enrollment createDomain() {
    return EnrollmentBuilder.anEnrollment().build();
  }

  @Override
  protected EnrollmentEntity createEntity() {
    return EnrollmentEntity.builder().status("PENDING").build();
  }

  @Override
  protected Enrollment mapToDomain(EnrollmentEntity entity) {
    return EnrollmentMapper.toDomain(entity);
  }

  @Override
  protected EnrollmentEntity mapToEntity(Enrollment domain) {
    return EnrollmentMapper.toEntity(domain);
  }

  @Override
  protected void copy(Enrollment domain, EnrollmentEntity entity) {
    EnrollmentMapper.copy(domain, entity);
  }

  @Override
  protected void assertRoundTrip(Enrollment original, Enrollment mapped) {
    assertThat(mapped.getIdentifier().getProjectId())
        .isEqualTo(original.getIdentifier().getProjectId());
    assertThat(mapped.getIdentifier().getFormerStudentId())
        .isEqualTo(original.getIdentifier().getFormerStudentId());
    assertThat(mapped.getStatus()).isEqualTo(original.getStatus());
    assertThat(mapped.getEnrollmentInfo().getAuditInfo().getCreatedAt())
        .isEqualTo(original.getEnrollmentInfo().getAuditInfo().getCreatedAt());
  }

  @Test
  @DisplayName("copy should update all entity fields from domain")
  void copyShouldUpdateEntityFields() {
    Enrollment enrollment = createDomain();
    EnrollmentEntity entity = createEntity();

    EnrollmentMapper.copy(enrollment, entity);

    assertThat(entity.getStatus()).isEqualTo(enrollment.getStatus().name());
    assertThat(entity.getCreatedAt())
        .isEqualTo(enrollment.getEnrollmentInfo().getAuditInfo().getCreatedAt());
    assertThat(entity.getUpdatedAt())
        .isEqualTo(enrollment.getEnrollmentInfo().getAuditInfo().getUpdatedAt());
  }
}
