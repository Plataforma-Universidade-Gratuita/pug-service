package br.org.catolicasc.pug.project.presenter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.infra.EnrollmentMapper;
import br.org.catolicasc.pug.project.presenter.dtos.EnrollmentCreateRequest;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("EnrollmentResource Integration Tests")
class EnrollmentResourceTest {

  @Inject TestDataFactory factory;
  @Inject UserTransaction utx;
  @Inject EntityManager em;
  @InjectMock AuthService authService;

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  void getByIdsSuccess() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    Account acc = factory.createAccount(factory.createUser(), AccountType.STUDENT);
    Student student = factory.createStudent(acc, course);
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Project project = factory.createProject(entity, creator);
    factory.createEnrollment(student, project);
    em.flush();
    utx.commit();

    given()
        .pathParam("projectId", project.getId())
        .pathParam("studentId", student.getAccountId())
        .when()
        .get("/projects/enrollments/{projectId}/{studentId}")
        .then()
        .statusCode(200)
        .body("data.projectId", is(project.getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  void getByIdsNotFound() {
    given()
        .pathParam("projectId", UuidCreator.getTimeOrderedEpoch())
        .pathParam("studentId", UuidCreator.getTimeOrderedEpoch())
        .when()
        .get("/projects/enrollments/{projectId}/{studentId}")
        .then()
        .statusCode(404);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  void listAll() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    Account acc = factory.createAccount(factory.createUser(), AccountType.STUDENT);
    Student student = factory.createStudent(acc, course);
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Project project = factory.createProject(entity, creator);
    factory.createEnrollment(student, project);
    utx.commit();

    given()
        .when()
        .get("/projects/enrollments")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  void createSuccess() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    Account acc = factory.createAccount(factory.createUser(), AccountType.STUDENT);
    factory.createStudent(acc, course);
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Project project = factory.createProject(entity, creator);
    utx.commit();

    when(authService.getCurrentAccountId()).thenReturn(acc.getId());

    given()
        .contentType(ContentType.JSON)
        .body(new EnrollmentCreateRequest(project.getId()))
        .when()
        .post("/projects/enrollments")
        .then()
        .statusCode(201);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  void acceptSuccess() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    Account acc = factory.createAccount(factory.createUser(), AccountType.STUDENT);
    Student student = factory.createStudent(acc, course);
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Project project = factory.createProject(entity, creator);
    factory.createEnrollment(student, project);
    em.flush();
    utx.commit();

    given()
        .pathParam("projectId", project.getId())
        .pathParam("studentId", student.getAccountId())
        .when()
        .patch("/projects/enrollments/{projectId}/{studentId}/accept")
        .then()
        .statusCode(200)
        .body("data.status", is("APPROVED"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  void cancelSuccess() throws Exception {
    utx.begin();
    Enrollment enr = setupApprovedEnrollment();
    em.merge(EnrollmentMapper.toEntity(enr));
    em.flush();
    utx.commit();

    given()
        .pathParam("projectId", enr.getIdentifier().getProjectId())
        .pathParam("studentId", enr.getIdentifier().getStudentId())
        .when()
        .patch("/projects/enrollments/{projectId}/{studentId}/cancel")
        .then()
        .statusCode(200)
        .body("data.status", is("CANCELED"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  void completeSuccess() throws Exception {
    utx.begin();
    Enrollment enr = setupApprovedEnrollment();
    em.merge(EnrollmentMapper.toEntity(enr));
    em.flush();
    utx.commit();

    given()
        .pathParam("projectId", enr.getIdentifier().getProjectId())
        .pathParam("studentId", enr.getIdentifier().getStudentId())
        .when()
        .patch("/projects/enrollments/{projectId}/{studentId}/complete")
        .then()
        .statusCode(200)
        .body("data.status", is("COMPLETED"));
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  void exitSuccess() throws Exception {
    Account acc;
    Project project;
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    acc = factory.createAccount(factory.createUser(), AccountType.STUDENT);
    Student student = factory.createStudent(acc, course);
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    project = factory.createProject(entity, creator);
    Enrollment enr =
        factory.createEnrollment(student, project).changeStatus(EnrollmentStatus.APPROVED);
    em.merge(EnrollmentMapper.toEntity(enr));
    em.flush();
    utx.commit();

    when(authService.getCurrentAccountId()).thenReturn(acc.getId());

    given()
        .pathParam("projectId", project.getId())
        .when()
        .patch("/projects/enrollments/{projectId}/exit")
        .then()
        .statusCode(200)
        .body("data.status", is("EXITED"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  void rejectSuccess() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    Account acc = factory.createAccount(factory.createUser(), AccountType.STUDENT);
    Student student = factory.createStudent(acc, course);
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Project project = factory.createProject(entity, creator);
    factory.createEnrollment(student, project);
    em.flush();
    utx.commit();

    given()
        .pathParam("projectId", project.getId())
        .pathParam("studentId", student.getAccountId())
        .when()
        .patch("/projects/enrollments/{projectId}/{studentId}/reject")
        .then()
        .statusCode(200)
        .body("data.status", is("REJECTED"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  void removeSuccess() throws Exception {
    utx.begin();
    Enrollment enr = setupApprovedEnrollment();
    em.merge(EnrollmentMapper.toEntity(enr));
    em.flush();
    utx.commit();

    given()
        .pathParam("projectId", enr.getIdentifier().getProjectId())
        .pathParam("studentId", enr.getIdentifier().getStudentId())
        .when()
        .patch("/projects/enrollments/{projectId}/{studentId}/remove")
        .then()
        .statusCode(200)
        .body("data.status", is("REMOVED"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  void deleteSuccess() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    Account acc = factory.createAccount(factory.createUser(), AccountType.STUDENT);
    Student student = factory.createStudent(acc, course);
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Project project = factory.createProject(entity, creator);
    factory.createEnrollment(student, project);
    em.flush();
    utx.commit();

    given()
        .pathParam("projectId", project.getId())
        .pathParam("studentId", student.getAccountId())
        .when()
        .delete("/projects/enrollments/{projectId}/{studentId}")
        .then()
        .statusCode(200);
  }

  private Enrollment setupApprovedEnrollment() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    Account acc = factory.createAccount(factory.createUser(), AccountType.STUDENT);
    Student student = factory.createStudent(acc, course);
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Project project = factory.createProject(entity, creator);
    return factory.createEnrollment(student, project).changeStatus(EnrollmentStatus.APPROVED);
  }
}
