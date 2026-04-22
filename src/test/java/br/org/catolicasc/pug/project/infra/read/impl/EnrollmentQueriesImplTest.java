package br.org.catolicasc.pug.project.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class EnrollmentQueriesImplTest {

  @Inject EnrollmentQueriesImpl queries;
  @Inject TestDataFactory factory;

  private Student student;
  private Project project;

  @BeforeEach
  void setup() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    Account acc = factory.createAccount(factory.createUser(), AccountType.STUDENT);
    student = factory.createStudent(acc, course);

    Entity entity = factory.createEntity(factory.getAnyCity());
    project =
        factory.createProject(
            entity, factory.createAccount(factory.createUser(), AccountType.PARTNER));

    factory.createEnrollment(student, project);
  }

  @Test
  @Transactional
  @DisplayName("Should retrieve EnrollmentView by composite ID")
  void shouldFindByIds() {
    var view = queries.findOptionalByIds(project.getId(), student.getAccountId());

    assertThat(view).isPresent();
    assertThat(view.get().projectId()).isEqualTo(project.getId());
  }

  @Test
  @Transactional
  @DisplayName("Should return empty when project ID is null")
  void shouldReturnEmptyForNullProjectId() {
    assertThat(queries.findOptionalByIds(null, student.getAccountId())).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should return empty when student ID is null")
  void shouldReturnEmptyForNullStudentId() {
    assertThat(queries.findOptionalByIds(project.getId(), null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should return empty for non-existent composite ID")
  void shouldReturnEmptyForNonExistentIds() {
    assertThat(queries.findOptionalByIds(UUID.randomUUID(), UUID.randomUUID())).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list all enrollments")
  void shouldListAllEnrollments() {
    var list = queries.listAllEnrollments();
    assertThat(list).isNotEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list enrollments by project ID")
  void shouldListByProjectId() {
    var list = queries.listByProjectId(project.getId());
    assertThat(list).isNotEmpty();
    assertThat(list).allSatisfy(v -> assertThat(v.projectId()).isEqualTo(project.getId()));
  }

  @Test
  @Transactional
  @DisplayName("Should return empty list for null project ID")
  void shouldReturnEmptyListForNullProjectId() {
    assertThat(queries.listByProjectId(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list enrollments by student ID")
  void shouldListByStudentId() {
    var list = queries.listByStudentId(student.getAccountId());
    assertThat(list).isNotEmpty();
    assertThat(list).allSatisfy(v -> assertThat(v.studentId()).isEqualTo(student.getAccountId()));
  }

  @Test
  @Transactional
  @DisplayName("Should return empty list for null student ID")
  void shouldReturnEmptyListForNullStudentId() {
    assertThat(queries.listByStudentId(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should verify all fields in enrollment view")
  void shouldVerifyAllFieldsInView() {
    var view = queries.findOptionalByIds(project.getId(), student.getAccountId());

    assertThat(view).isPresent();
    var ev = view.get();
    assertThat(ev.projectId()).isEqualTo(project.getId());
    assertThat(ev.studentId()).isEqualTo(student.getAccountId());
    assertThat(ev.status()).isNotNull();
    assertThat(ev.createdAt()).isNotNull();
    assertThat(ev.updatedAt()).isNotNull();
  }
}
