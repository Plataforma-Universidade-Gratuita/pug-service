package br.org.catolicasc.pug.academic.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.academic.domain.vos.AcademicRegistration;
import br.org.catolicasc.pug.academic.domain.vos.CounterpartHours;
import br.org.catolicasc.pug.academic.domain.vos.Period;
import br.org.catolicasc.pug.academic.infra.persistence.StudentEntity;
import br.org.catolicasc.pug.academic.infra.read.dtos.StudentView;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("StudentMapper Tests")
class StudentMapperTest {

  private Student createValidStudent() {
    return Student.factory(
        UUID.randomUUID(),
        AcademicRegistration.factory("12345"),
        Campi.JARAGUA_DO_SUL,
        UUID.randomUUID(),
        CounterpartHours.factory(new BigDecimal("100"), BigDecimal.ZERO, false),
        Period.factory(LocalDate.now(), LocalDate.now().plusMonths(6)));
  }

  @Test
  @DisplayName("Should perform round-trip mapping for Student")
  void shouldPerformRoundTrip() {
    Student student = createValidStudent();

    StudentEntity entity = StudentMapper.toEntity(student);
    Student mapped = StudentMapper.toDomain(entity);

    assertThat(mapped.getAccountId()).isEqualTo(student.getAccountId());
    assertThat(mapped.getAcademicRegistration().getValue())
        .isEqualTo(student.getAcademicRegistration().getValue());
    assertThat(mapped.getCounterpartHours().getRequiredHours())
        .isEqualTo(student.getCounterpartHours().getRequiredHours());
  }

  @Test
  @DisplayName("toDomain should return null when entity is null")
  void toDomainShouldReturnNullForNullEntity() {
    assertThat(StudentMapper.toDomain(null)).isNull();
  }

  @Test
  @DisplayName("toEntity should return null when domain is null")
  void toEntityShouldReturnNullForNullDomain() {
    assertThat(StudentMapper.toEntity(null)).isNull();
  }

  @Test
  @DisplayName("copy should do nothing when domain is null")
  void copyShouldHandleNullDomain() {
    StudentEntity entity = StudentEntity.builder().academicRegistration("99999").build();
    StudentMapper.copy(null, entity);
    assertThat(entity.getAcademicRegistration()).isEqualTo("99999");
  }

  @Test
  @DisplayName("copy should do nothing when entity is null")
  void copyShouldHandleNullEntity() {
    Student student = createValidStudent();
    StudentMapper.copy(student, null);
  }

  @Test
  @DisplayName("copy should do nothing when both are null")
  void copyShouldHandleBothNull() {
    StudentMapper.copy(null, null);
  }

  @Test
  @DisplayName("copy should update all entity fields from domain")
  void copyShouldUpdateEntityFields() {
    Student student = createValidStudent();
    StudentEntity entity = StudentEntity.builder().academicRegistration("old").build();

    StudentMapper.copy(student, entity);

    assertThat(entity.getAcademicRegistration())
        .isEqualTo(student.getAcademicRegistration().getValue());
    assertThat(entity.getCampus()).isEqualTo(student.getCampus());
    assertThat(entity.getCourseId()).isEqualTo(student.getCourseId());
    assertThat(entity.getRequiredHours())
        .isEqualByComparingTo(student.getCounterpartHours().getRequiredHours());
    assertThat(entity.getCompletedHours())
        .isEqualByComparingTo(student.getCounterpartHours().getCompletedHours());
    assertThat(entity.getConcluded()).isEqualTo(student.getCounterpartHours().getConcluded());
    assertThat(entity.getStartDate()).isEqualTo(student.getPeriod().getStartDate());
    assertThat(entity.getDueDate()).isEqualTo(student.getPeriod().getDueDate());
    assertThat(entity.getCreatedAt()).isEqualTo(student.getAuditInfo().getCreatedAt());
    assertThat(entity.getUpdatedAt()).isEqualTo(student.getAuditInfo().getUpdatedAt());
  }

  @Test
  @DisplayName("toView should return null when entity is null")
  void toViewShouldReturnNullForNullEntity() {
    assertThat(StudentMapper.toView(null)).isNull();
  }

  @Test
  @DisplayName("toView should map all fields correctly")
  void toViewShouldMapAllFields() {
    UUID accountId = UUID.randomUUID();
    UUID courseId = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();
    LocalDate today = LocalDate.now();

    StudentEntity entity =
        StudentEntity.builder()
            .accountId(accountId)
            .academicRegistration("54321")
            .campus(Campi.JARAGUA_DO_SUL)
            .courseId(courseId)
            .requiredHours(new BigDecimal("200"))
            .completedHours(new BigDecimal("50"))
            .concluded(false)
            .startDate(today)
            .dueDate(today.plusMonths(12))
            .createdAt(now)
            .updatedAt(now)
            .build();

    StudentView view = StudentMapper.toView(entity);

    assertThat(view).isNotNull();
    assertThat(view.accountId()).isEqualTo(accountId);
    assertThat(view.academicRegistration()).isEqualTo("54321");
    assertThat(view.campus()).isEqualTo(Campi.JARAGUA_DO_SUL);
    assertThat(view.courseId()).isEqualTo(courseId);
    assertThat(view.requiredHours()).isEqualByComparingTo(new BigDecimal("200"));
    assertThat(view.completedHours()).isEqualByComparingTo(new BigDecimal("50"));
    assertThat(view.concluded()).isFalse();
    assertThat(view.startDate()).isEqualTo(today);
    assertThat(view.dueDate()).isEqualTo(today.plusMonths(12));
  }

  @Test
  @DisplayName("Round-trip should preserve all value objects")
  void roundTripShouldPreserveAllValueObjects() {
    Student student = createValidStudent();

    StudentEntity entity = StudentMapper.toEntity(student);
    Student mapped = StudentMapper.toDomain(entity);

    assertThat(mapped.getCampus()).isEqualTo(student.getCampus());
    assertThat(mapped.getCourseId()).isEqualTo(student.getCourseId());
    assertThat(mapped.getCounterpartHours().getCompletedHours())
        .isEqualByComparingTo(student.getCounterpartHours().getCompletedHours());
    assertThat(mapped.getCounterpartHours().getConcluded())
        .isEqualTo(student.getCounterpartHours().getConcluded());
    assertThat(mapped.getPeriod().getStartDate()).isEqualTo(student.getPeriod().getStartDate());
    assertThat(mapped.getPeriod().getDueDate()).isEqualTo(student.getPeriod().getDueDate());
    assertThat(mapped.getAuditInfo().getCreatedAt())
        .isEqualTo(student.getAuditInfo().getCreatedAt());
  }
}
