package br.org.catolicasc.pug.project.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.builders.AttendanceBuilder;
import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import br.org.catolicasc.pug.project.infra.persistence.AttendanceEntity;
import br.org.catolicasc.pug.project.infra.read.dtos.AttendanceView;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AttendanceMapper Tests")
class AttendanceMapperTest {

  @Test
  @DisplayName("Should perform round-trip mapping for Attendance")
  void shouldPerformRoundTrip() {
    Attendance attendance = AttendanceBuilder.anAttendance().build();

    AttendanceEntity entity = AttendanceMapper.toEntity(attendance);
    Attendance mapped = AttendanceMapper.toDomain(entity);

    assertThat(mapped.getId()).isEqualTo(attendance.getId());
    assertThat(mapped.getStatus()).isEqualTo(attendance.getStatus());
    assertThat(mapped.getQrValidationInfo().getQrValidationHash())
        .isEqualTo(attendance.getQrValidationInfo().getQrValidationHash());
  }

  @Test
  @DisplayName("toDomain should return null when entity is null")
  void toDomainShouldReturnNullForNullEntity() {
    assertThat(AttendanceMapper.toDomain(null)).isNull();
  }

  @Test
  @DisplayName("toEntity should return null when domain is null")
  void toEntityShouldReturnNullForNullDomain() {
    assertThat(AttendanceMapper.toEntity(null)).isNull();
  }

  @Test
  @DisplayName("copy should do nothing when domain is null")
  void copyShouldHandleNullDomain() {
    AttendanceEntity entity = AttendanceEntity.builder().status("WAITING").build();
    AttendanceMapper.copy(null, entity);
    assertThat(entity.getStatus()).isEqualTo("WAITING");
  }

  @Test
  @DisplayName("copy should do nothing when entity is null")
  void copyShouldHandleNullEntity() {
    Attendance attendance = AttendanceBuilder.anAttendance().build();
    AttendanceMapper.copy(attendance, null);
  }

  @Test
  @DisplayName("copy should do nothing when both are null")
  void copyShouldHandleBothNull() {
    AttendanceMapper.copy(null, null);
  }

  @Test
  @DisplayName("copy should update all entity fields from domain")
  void copyShouldUpdateEntityFields() {
    Attendance attendance = AttendanceBuilder.anAttendance().build();
    AttendanceEntity entity = AttendanceEntity.builder().status("OLD").build();

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
    UUID id = UUID.randomUUID();
    UUID projectId = UUID.randomUUID();
    UUID studentId = UUID.randomUUID();
    UUID validatedBy = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();

    AttendanceEntity entity =
        AttendanceEntity.builder()
            .id(id)
            .projectId(projectId)
            .studentId(studentId)
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
    assertThat(view.studentId()).isEqualTo(studentId);
    assertThat(view.duration()).isEqualByComparingTo(new BigDecimal("2.50"));
    assertThat(view.qrValidationHash()).isEqualTo("hash-123");
    assertThat(view.status()).isEqualTo(AttendanceStatus.WAITING);
    assertThat(view.validatedById()).isEqualTo(validatedBy);
    assertThat(view.validatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("Round-trip should preserve all value objects")
  void roundTripShouldPreserveAllValueObjects() {
    Attendance attendance = AttendanceBuilder.anAttendance().build();

    AttendanceEntity entity = AttendanceMapper.toEntity(attendance);
    Attendance mapped = AttendanceMapper.toDomain(entity);

    assertThat(mapped.getEnrollmentIdentifier().getProjectId())
        .isEqualTo(attendance.getEnrollmentIdentifier().getProjectId());
    assertThat(mapped.getEnrollmentIdentifier().getStudentId())
        .isEqualTo(attendance.getEnrollmentIdentifier().getStudentId());
    assertThat(mapped.getQrValidationInfo().getDuration())
        .isEqualByComparingTo(attendance.getQrValidationInfo().getDuration());
    assertThat(mapped.getAttendanceInfo().getAuditInfo().getCreatedAt())
        .isEqualTo(attendance.getAttendanceInfo().getAuditInfo().getCreatedAt());
  }
}
