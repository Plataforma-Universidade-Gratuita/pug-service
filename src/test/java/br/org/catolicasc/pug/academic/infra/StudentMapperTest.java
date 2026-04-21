package br.org.catolicasc.pug.academic.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.academic.domain.vos.AcademicRegistration;
import br.org.catolicasc.pug.academic.domain.vos.CounterpartHours;
import br.org.catolicasc.pug.academic.domain.vos.Period;
import br.org.catolicasc.pug.academic.infra.persistence.StudentEntity;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("StudentMapper Tests")
class StudentMapperTest {

  @Test
  @DisplayName("Should perform round-trip mapping for Student")
  void shouldPerformRoundTrip() {
    Student student =
        Student.factory(
            UUID.randomUUID(),
            AcademicRegistration.factory("12345"),
            Campi.JARAGUA_DO_SUL,
            UUID.randomUUID(),
            CounterpartHours.factory(new BigDecimal("100"), BigDecimal.ZERO, false),
            Period.factory(LocalDate.now(), LocalDate.now().plusMonths(6)));

    StudentEntity entity = StudentMapper.toEntity(student);
    Student mapped = StudentMapper.toDomain(entity);

    assertThat(mapped.getAccountId()).isEqualTo(student.getAccountId());
    assertThat(mapped.getAcademicRegistration().getValue())
        .isEqualTo(student.getAcademicRegistration().getValue());
    assertThat(mapped.getCounterpartHours().getRequiredHours())
        .isEqualTo(student.getCounterpartHours().getRequiredHours());
  }
}
