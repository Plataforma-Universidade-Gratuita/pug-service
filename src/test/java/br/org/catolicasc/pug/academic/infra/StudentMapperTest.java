package br.org.catolicasc.pug.academic.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.academic.domain.vos.AcademicRegistration;
import br.org.catolicasc.pug.academic.domain.vos.CounterpartHours;
import br.org.catolicasc.pug.academic.domain.vos.Period;
import br.org.catolicasc.pug.academic.infra.persistence.StudentEntity;
import br.org.catolicasc.pug.academic.infra.read.dtos.StudentView;
import br.org.catolicasc.pug.helpers.CopyableMapperTest;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import com.github.f4b6a3.uuid.UuidCreator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("StudentMapper Tests")
class StudentMapperTest extends CopyableMapperTest<Student, StudentEntity> {

  @Override
  protected Student createDomain() {
    return Student.factory(
        UuidCreator.getTimeOrderedEpoch(),
        AcademicRegistration.factory("12345"),
        Campi.JARAGUA_DO_SUL,
        UuidCreator.getTimeOrderedEpoch(),
        CounterpartHours.factory(new BigDecimal("100"), BigDecimal.ZERO, false),
        Period.factory(LocalDate.now(), LocalDate.now().plusMonths(6)));
  }

  @Override
  protected StudentEntity createEntity() {
    return StudentEntity.builder().academicRegistration("99999").build();
  }

  @Override
  protected Student mapToDomain(StudentEntity entity) {
    return StudentMapper.toDomain(entity);
  }

  @Override
  protected StudentEntity mapToEntity(Student domain) {
    return StudentMapper.toEntity(domain);
  }

  @Override
  protected void copy(Student domain, StudentEntity entity) {
    StudentMapper.copy(domain, entity);
  }

  @Override
  protected void assertRoundTrip(Student original, Student mapped) {
    assertThat(mapped.getAccountId()).isEqualTo(original.getAccountId());
    assertThat(mapped.getAcademicRegistration().getValue())
        .isEqualTo(original.getAcademicRegistration().getValue());
    assertThat(mapped.getCounterpartHours().getRequiredHours())
        .isEqualTo(original.getCounterpartHours().getRequiredHours());
    assertThat(mapped.getCampus()).isEqualTo(original.getCampus());
    assertThat(mapped.getCourseId()).isEqualTo(original.getCourseId());
    assertThat(mapped.getCounterpartHours().getCompletedHours())
        .isEqualByComparingTo(original.getCounterpartHours().getCompletedHours());
    assertThat(mapped.getCounterpartHours().getConcluded())
        .isEqualTo(original.getCounterpartHours().getConcluded());
    assertThat(mapped.getPeriod().getStartDate()).isEqualTo(original.getPeriod().getStartDate());
    assertThat(mapped.getPeriod().getDueDate()).isEqualTo(original.getPeriod().getDueDate());
    assertThat(mapped.getAuditInfo().getCreatedAt())
        .isEqualTo(original.getAuditInfo().getCreatedAt());
  }

  @Test
  @DisplayName("copy should update all entity fields from domain")
  void copyShouldUpdateEntityFields() {
    Student student = createDomain();
    StudentEntity entity = createEntity();

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
    UUID accountId = UuidCreator.getTimeOrderedEpoch();
    UUID courseId = UuidCreator.getTimeOrderedEpoch();
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
}
