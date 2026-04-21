package br.org.catolicasc.pug.academic.infra.persistence.impl;

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
class StudentRepositoryImplTest {

  @Inject StudentRepositoryImpl repository;
  @Inject TestDataFactory factory;

  private Account account;
  private Course course;

  @BeforeEach
  void setup() {
    User user = factory.createUser();
    account = factory.createAccount(user, AccountType.STUDENT);

    School school = factory.createSchool();
    course = factory.createCourse(school);
  }

  @Test
  @Transactional
  @DisplayName("Should persist and find Student")
  void shouldPersistAndFind() {
    Student student = factory.createStudent(account, course);

    var found = repository.findOptionalById(student.getAccountId());
    assertThat(found).isPresent();
    assertThat(found.get().getAcademicRegistration().getValue())
        .isEqualTo(student.getAcademicRegistration().getValue());
  }
}
