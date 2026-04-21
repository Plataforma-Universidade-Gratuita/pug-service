package br.org.catolicasc.pug.academic.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StudentQueriesImplTest {

  @Inject StudentQueriesImpl queries;
  @Inject TestDataFactory factory;

  private Student student;

  @BeforeEach
  void setup() {
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.STUDENT);
    School school = factory.createSchool();
    Course course = factory.createCourse(school);

    student = factory.createStudent(account, course);
  }

  @Test
  @Transactional
  @DisplayName("Should retrieve StudentView by Registration")
  void shouldFindByRegistration() {
    var view =
        queries.findOptionalByAcademicRegistration(student.getAcademicRegistration().getValue());

    assertThat(view).isPresent();
    assertThat(view.get().academicRegistration())
        .isEqualTo(student.getAcademicRegistration().getValue());
  }
}
