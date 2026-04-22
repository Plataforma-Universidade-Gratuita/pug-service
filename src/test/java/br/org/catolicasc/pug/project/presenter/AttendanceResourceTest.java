package br.org.catolicasc.pug.project.presenter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AttendanceResource Integration Tests")
class AttendanceResourceTest {

  @Inject TestDataFactory factory;
  @Inject UserTransaction utx;
  @Inject EntityManager em;

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /projects/attendances/{id} - Success")
  void getByIdSuccess() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    Account acc = factory.createAccount(factory.createUser(), AccountType.STUDENT);
    Student student = factory.createStudent(acc, course);
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Project project = factory.createProject(entity, creator);
    factory.createEnrollment(student, project);
    Attendance attendance = factory.createAttendance(project, student);
    em.flush();
    utx.commit();

    given()
        .pathParam("id", attendance.getId())
        .when()
        .get("/projects/attendances/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.id", is(attendance.getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /projects/attendances/{id} - Not Found")
  void getByIdNotFound() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .get("/projects/attendances/{id}")
        .then()
        .statusCode(404);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /projects/attendances - List All")
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
    factory.createAttendance(project, student);
    utx.commit();

    given()
        .when()
        .get("/projects/attendances")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /projects/attendances?projectId= - Filter by Project")
  void listByProjectId() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    Account acc = factory.createAccount(factory.createUser(), AccountType.STUDENT);
    Student student = factory.createStudent(acc, course);
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Project project = factory.createProject(entity, creator);
    factory.createEnrollment(student, project);
    factory.createAttendance(project, student);
    utx.commit();

    given()
        .queryParam("projectId", project.getId().toString())
        .when()
        .get("/projects/attendances")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /projects/attendances?studentId= - Filter by Student")
  void listByStudentId() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    Account acc = factory.createAccount(factory.createUser(), AccountType.STUDENT);
    Student student = factory.createStudent(acc, course);
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Project project = factory.createProject(entity, creator);
    factory.createEnrollment(student, project);
    factory.createAttendance(project, student);
    utx.commit();

    given()
        .queryParam("studentId", student.getAccountId().toString())
        .when()
        .get("/projects/attendances")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /projects/attendances?projectId=&studentId= - Filter by Enrollment")
  void listByEnrollmentId() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    Account acc = factory.createAccount(factory.createUser(), AccountType.STUDENT);
    Student student = factory.createStudent(acc, course);
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Project project = factory.createProject(entity, creator);
    factory.createEnrollment(student, project);
    factory.createAttendance(project, student);
    utx.commit();

    given()
        .queryParam("projectId", project.getId().toString())
        .queryParam("studentId", student.getAccountId().toString())
        .when()
        .get("/projects/attendances")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("DELETE /projects/attendances/{id} - Success")
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
    Attendance attendance = factory.createAttendance(project, student);
    em.flush();
    utx.commit();

    given()
        .pathParam("id", attendance.getId())
        .when()
        .delete("/projects/attendances/{id}")
        .then()
        .statusCode(200);
  }

  @Test
  @DisplayName("Should return 401 when accessing without authentication")
  void unauthorizedAccess() {
    given().when().get("/projects/attendances").then().statusCode(401);
  }
}
