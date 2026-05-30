package br.org.catolicasc.pug.project.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.CopyableMapperTest;
import br.org.catolicasc.pug.helpers.builders.domain.AttendanceBuilder;
import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import br.org.catolicasc.pug.project.infra.persistence.AttendanceEntity;
import br.org.catolicasc.pug.project.infra.read.dtos.AttendanceView;
import com.github.f4b6a3.uuid.UuidCreator;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AttendanceMapper Tests")
class AttendanceMapperTest extends CopyableMapperTest<Attendance, AttendanceEntity> {

  @Override
  protected Attendance createDomain() {
    return AttendanceBuilder.anAttendance().build();
  }

  @Override
  protected AttendanceEntity createEntity() {
    return AttendanceEntity.builder().status("WAITING").build();
  }

  @Override
  protected Attendance mapToDomain(AttendanceEntity entity) {
    return AttendanceMapper.toDomain(entity);
  }

  @Override
  protected AttendanceEntity mapToEntity(Attendance domain) {
    return AttendanceMapper.toEntity(domain);
  }

  @Override
  protected void copy(Attendance domain, AttendanceEntity entity) {
    AttendanceMapper.copy(domain, entity);
  }

  @Override
  protected void assertRoundTrip(Attendance original, Attendance mapped) {
    assertThat(mapped.getId()).isEqualTo(original.getId());
    assertThat(mapped.getStatus()).isEqualTo(original.getStatus());
    assertThat(mapped.getQrValidationInfo().getQrValidationHash())
        .isEqualTo(original.getQrValidationInfo().getQrValidationHash());
    assertThat(mapped.getEnrollmentIdentifier().getProjectId())
        .isEqualTo(original.getEnrollmentIdentifier().getProjectId());
    assertThat(mapped.getEnrollmentIdentifier().getFormerStudentId())
        .isEqualTo(original.getEnrollmentIdentifier().getFormerStudentId());
    assertThat(mapped.getQrValidationInfo().getDuration())
        .isEqualByComparingTo(original.getQrValidationInfo().getDuration());
    assertThat(mapped.getAttendanceInfo().getAuditInfo().getCreatedAt())
        .isEqualTo(original.getAttendanceInfo().getAuditInfo().getCreatedAt());
  }

  @Test
  @DisplayName("copy should update all entity fields from domain")
  void copyShouldUpdateEntityFields() {
    Attendance attendance = createDomain();
    AttendanceEntity entity = createEntity();

    AttendanceMapper.copy(attendance, entity);

    assertThat(entity.getDuration())
        .isEqualByComparingTo(attendance.getQrValidationInfo().getDuration());
    assertThat(entity.getQrValidationHash())
        .isEqualTo(attendance.getQrValidationInfo().getQrValidationHash());
    assertThat(entity.getStatus()).isEqualTo(attendance.getStatus().name());
    assertThat(entity.getUpdatedAt())
        .isEqualTo(attendance.getAttendanceInfo().getAuditInfo().getUpdatedAt());
  }

  @Test
  @DisplayName("toView should return null when entity is null")
  void toViewShouldReturnNullForNullEntity() {
    assertThat(AttendanceMapper.toView(null)).isNull();
  }

  @Test
  @DisplayName("toView should map all fields correctly")
  void toViewShouldMapAllFields() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    UUID projectId = UuidCreator.getTimeOrderedEpoch();
    UUID formerStudentId = UuidCreator.getTimeOrderedEpoch();
    UUID validatedBy = UuidCreator.getTimeOrderedEpoch();
    OffsetDateTime now = OffsetDateTime.now();

    AttendanceEntity entity =
        AttendanceEntity.builder()
            .id(id)
            .projectId(projectId)
            .formerStudentId(formerStudentId)
            .duration(new BigDecimal("2.50"))
            .qrValidationHash("hash-123")
            .status("WAITING")
            .validatedBy(validatedBy)
            .validatedAt(now)
            .createdAt(now)
            .updatedAt(now)
            .build();

    AttendanceView view = AttendanceMapper.toView(entity);

    assertThat(view).isNotNull();
    assertThat(view.id()).isEqualTo(id);
    assertThat(view.projectId()).isEqualTo(projectId);
    assertThat(view.formerStudentId()).isEqualTo(formerStudentId);
    assertThat(view.duration()).isEqualByComparingTo(new BigDecimal("2.50"));
    assertThat(view.qrValidationHash()).isEqualTo("hash-123");
    assertThat(view.status()).isEqualTo(AttendanceStatus.WAITING);
    assertThat(view.validatedById()).isEqualTo(validatedBy);
    assertThat(view.validatedAt()).isEqualTo(now);
  }
}
