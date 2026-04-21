package br.org.catolicasc.pug.builders;

import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.academic.domain.vos.AcademicRegistration;
import br.org.catolicasc.pug.academic.domain.vos.CounterpartHours;
import br.org.catolicasc.pug.academic.domain.vos.Period;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class StudentBuilder {
  private UUID accountId = UUID.randomUUID();
  private String registration = "123456";
  private Campi campus = Campi.JARAGUA_DO_SUL;
  private UUID courseId = UUID.randomUUID();

  private StudentBuilder() {}

  public static StudentBuilder aStudent() {
    return new StudentBuilder();
  }

  public StudentBuilder withAccountId(UUID accountId) {
    this.accountId = accountId;
    return this;
  }

  public Student build() {
    return Student.factory(
        accountId,
        AcademicRegistration.factory(registration),
        campus,
        courseId,
        CounterpartHours.factory(new BigDecimal("100"), BigDecimal.ZERO, false),
        Period.factory(LocalDate.now(), LocalDate.now().plusMonths(6)));
  }
}
