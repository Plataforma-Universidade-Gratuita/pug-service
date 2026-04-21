package br.org.catolicasc.pug.project.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AttendanceRepositoryImplTest {

  @Inject AttendanceRepositoryImpl repository;
  @Inject TestDataFactory factory;

  private Student student;
  private Project project;

  @BeforeEach
  void setup() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    Account sAcc = factory.createAccount(factory.createUser(), AccountType.STUDENT);
    student = factory.createStudent(sAcc, course);

    Entity entity = factory.createEntity(factory.getAnyCity());
    project =
        factory.createProject(
            entity, factory.createAccount(factory.createUser(), AccountType.PARTNER));

    factory.createEnrollment(student, project);
  }

  @Test
  @Transactional
  @DisplayName("Should persist and find Attendance")
  void shouldPersistAndFind() {
    Attendance attendance = factory.createAttendance(project, student);

    var found = repository.findOptionalById(attendance.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getQrValidationInfo().getQrValidationHash())
        .isEqualTo(attendance.getQrValidationInfo().getQrValidationHash());
  }
}
