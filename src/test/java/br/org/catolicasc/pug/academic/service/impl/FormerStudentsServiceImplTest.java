package br.org.catolicasc.pug.academic.service.impl;

import static br.org.catolicasc.pug.helpers.builders.commands.FormerStudentCreateCommandBuilder.aFormerStudentCreateCommand;
import static br.org.catolicasc.pug.helpers.builders.commands.FormerStudentUpdateCommandBuilder.aFormerStudentUpdateCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentCreateCommand;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.project.service.EnrollmentsService;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("FormerStudentsServiceImpl Integration Tests")
class FormerStudentsServiceImplTest {

  @Inject FormerStudentsServiceImpl service;
  @Inject TestDataFactory factory;
  @Inject EntityManager em;

  @InjectMock AuditPublisher audit;
  @InjectMock EnrollmentsService enrollmentsService;

  @Test
  @Transactional
  @DisplayName("Should save formerStudent successfully")
  void saveSuccess() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    em.flush();

    FormerStudentCreateCommand cmd =
        aFormerStudentCreateCommand().withCourseId(course.getId()).build();
    FormerStudent saved = service.save(cmd);

    assertThat(saved.getAccountId()).isNotNull();
    assertThat(saved.getCourseId()).isEqualTo(course.getId());
    verify(audit).fireCreate(FormerStudent.class.getName(), saved.getAccountId());
  }

  @Test
  @Transactional
  @DisplayName("Should throw when course not found on save")
  void saveCourseNotFound() {
    FormerStudentCreateCommand cmd = aFormerStudentCreateCommand().build();
    assertThrows(ResourceNotFoundException.class, () -> service.save(cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should throw DuplicateResourceException for same registration")
  void saveDuplicateRegistration() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.FORMER_STUDENT);
    factory.createStudent(account, course);
    em.flush();

    FormerStudent existingStudent = service.getById(account.getId());

    FormerStudentCreateCommand cmd =
        aFormerStudentCreateCommand()
            .withAcademicRegistration(existingStudent.getAcademicRegistration().getValue())
            .withCourseId(course.getId())
            .build();

    assertThrows(DuplicateResourceException.class, () -> service.save(cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should get formerStudent by ID")
  void getByIdSuccess() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.FORMER_STUDENT);
    factory.createStudent(account, course);
    em.flush();

    FormerStudent found = service.getById(account.getId());
    assertThat(found.getAccountId()).isEqualTo(account.getId());
  }

  @Test
  @DisplayName("Should throw when formerStudent not found")
  void getByIdNotFound() {
    assertThrows(
        ResourceNotFoundException.class, () -> service.getById(UuidCreator.getTimeOrderedEpoch()));
  }

  @Test
  @Transactional
  @DisplayName("Should update formerStudent successfully")
  void updateSuccess() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.FORMER_STUDENT);
    factory.createStudent(account, course);
    em.flush();

    var cmd = aFormerStudentUpdateCommand().withCampus(Campi.JOINVILLE).build();
    FormerStudent updated = service.update(account.getId(), cmd);

    assertThat(updated.getCampus()).isEqualTo(Campi.JOINVILLE);
  }

  @Test
  @DisplayName("Should throw when updating non-existing formerStudent")
  void updateNotFound() {
    var cmd = aFormerStudentUpdateCommand().build();
    assertThrows(
        ResourceNotFoundException.class,
        () -> service.update(UuidCreator.getTimeOrderedEpoch(), cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should delete formerStudent successfully")
  void deleteSuccess() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.FORMER_STUDENT);
    factory.createStudent(account, course);
    em.flush();

    when(enrollmentsService.existsAnyByStudentId(account.getId())).thenReturn(false);
    boolean deleted = service.delete(account.getId());

    assertThat(deleted).isTrue();
    verify(audit).fireDelete(FormerStudent.class.getName(), account.getId());
  }

  @Test
  @DisplayName("Should return false when deleting with null ID")
  void deleteNullId() {
    assertThat(service.delete(null)).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should throw when deleting formerStudent with enrollments")
  void deleteWithEnrollments() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.FORMER_STUDENT);
    factory.createStudent(account, course);
    em.flush();

    when(enrollmentsService.existsAnyByStudentId(account.getId())).thenReturn(true);
    assertThrows(BusinessRuleException.class, () -> service.delete(account.getId()));
  }

  @Test
  @Transactional
  @DisplayName("Should add completed hours successfully")
  void addCompletedHoursSuccess() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.FORMER_STUDENT);
    factory.createStudent(account, course);
    em.flush();

    FormerStudent updated = service.addCompletedHours(account.getId(), new BigDecimal("10"));
    assertThat(updated.getCounterpartHours().getCompletedHours())
        .isEqualByComparingTo(new BigDecimal("10"));
  }

  @Test
  @DisplayName("Should throw when adding hours to non-existing formerStudent")
  void addCompletedHoursNotFound() {
    assertThrows(
        ResourceNotFoundException.class,
        () -> service.addCompletedHours(UuidCreator.getTimeOrderedEpoch(), new BigDecimal("10")));
  }

  @Test
  @Transactional
  @DisplayName("Should save in bulk successfully")
  void saveInBulkSuccess() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    em.flush();

    FormerStudentCreateCommand cmd1 =
        aFormerStudentCreateCommand().withCourseId(course.getId()).build();
    FormerStudentCreateCommand cmd2 =
        aFormerStudentCreateCommand().withCourseId(course.getId()).build();

    List<FormerStudent> students = service.saveInBulk(List.of(cmd1, cmd2));
    assertThat(students).hasSize(2);
  }

  @Test
  @DisplayName("Should return empty list for empty bulk")
  void saveInBulkEmpty() {
    assertThat(service.saveInBulk(List.of())).isEmpty();
  }

  @Test
  @DisplayName("Should delegate existsAnyByCourseId to repo")
  void existsAnyByCourseId() {
    assertThat(service.existsAnyByCourseId(UuidCreator.getTimeOrderedEpoch())).isFalse();
  }
}
