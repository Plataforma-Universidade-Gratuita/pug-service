package br.org.catolicasc.pug.academic.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.domain.vos.CounterpartHours;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("FormerStudentRepositoryImpl Tests")
class FormerStudentRepositoryImplTest {

  @Inject FormerStudentRepositoryImpl repository;
  @Inject TestDataFactory factory;
  @Inject EntityManager em;

  private Account account;
  private Course course;

  @BeforeEach
  void setup() {
    User user = factory.createUser();
    account = factory.createAccount(user, AccountType.FORMER_STUDENT);

    AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
    course = factory.createCourse(areaOfExpertise);
  }

  @Test
  @Transactional
  @DisplayName("Should persist and find FormerStudent")
  void shouldPersistAndFind() {
    FormerStudent formerStudent = factory.createStudent(account, course);

    var found = repository.findOptionalById(formerStudent.getAccountId());

    assertThat(found).isPresent();
    assertThat(found.get().getAcademicRegistration().getValue())
        .isEqualTo(formerStudent.getAcademicRegistration().getValue());
  }

  @Test
  @Transactional
  @DisplayName("Should persist all former students")
  void shouldPersistAll() {
    Account secondAccount = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);

    FormerStudent first =
        br.org.catolicasc.pug.helpers.builders.domain.FormerStudentBuilder.aStudent()
            .withAccountId(account.getId())
            .withCourse(course.getId())
            .build();
    FormerStudent second =
        br.org.catolicasc.pug.helpers.builders.domain.FormerStudentBuilder.aStudent()
            .withAccountId(secondAccount.getId())
            .withCourse(course.getId())
            .build();

    var saved = repository.persistAll(List.of(first, second));

    assertThat(saved).hasSize(2);
    assertThat(repository.findOptionalById(first.getAccountId())).isPresent();
    assertThat(repository.findOptionalById(second.getAccountId())).isPresent();
  }

  @Test
  @Transactional
  @DisplayName("Should return empty list when persistAll receives null or empty list")
  void persistAllEmptyInputs() {
    assertThat(repository.persistAll(null)).isEmpty();
    assertThat(repository.persistAll(List.of())).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should return null when persisting null")
  void persistNull() {
    assertThat(repository.persist((FormerStudent) null)).isNull();
  }

  @Test
  @Transactional
  @DisplayName("Should check registration and course existence")
  void shouldCheckExistence() {
    FormerStudent formerStudent = factory.createStudent(account, course);
    String registration = formerStudent.getAcademicRegistration().getValue();

    assertThat(repository.existsByRegistration(registration)).isTrue();
    assertThat(repository.existsByRegistration(null)).isFalse();
    assertThat(repository.existsByRegistration("   ")).isFalse();

    assertThat(repository.existsAnyByRegistrations(List.of(registration))).isTrue();
    assertThat(repository.existsAnyByRegistrations(null)).isFalse();
    assertThat(repository.existsAnyByRegistrations(List.of())).isFalse();

    assertThat(repository.existsByCourseId(course.getId())).isTrue();
    assertThat(repository.existsByCourseId(null)).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should update former-student fields")
  void shouldUpdate() {
    FormerStudent formerStudent = factory.createStudent(account, course);
    FormerStudent updated =
        formerStudent.updateRequiredHours(
            CounterpartHours.factory(new BigDecimal("200"), BigDecimal.ZERO, false));

    repository.update(updated);

    var found = repository.findOptionalById(formerStudent.getAccountId());
    assertThat(found).isPresent();
    assertThat(found.get().getCounterpartHours().getRequiredHours())
        .isEqualByComparingTo(new BigDecimal("200"));
  }

  @Test
  @Transactional
  @DisplayName("Should ignore null updates")
  void shouldIgnoreNullUpdates() {
    repository.update(null);
  }

  @Test
  @Transactional
  @DisplayName("Should delete by ID")
  void shouldDeleteById() {
    FormerStudent formerStudent = factory.createStudent(account, course);

    assertThat(repository.deleteById(formerStudent.getAccountId())).isTrue();
    em.clear();
    assertThat(repository.findOptionalById(formerStudent.getAccountId())).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should return false when deleting null or missing ID")
  void shouldReturnFalseWhenDeletingInvalidId() {
    assertThat(repository.deleteById(null)).isFalse();
  }
}
