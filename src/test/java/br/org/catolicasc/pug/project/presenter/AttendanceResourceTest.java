package br.org.catolicasc.pug.project.presenter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.helpers.BaseResourceTest;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AttendanceResource Integration Tests")
class AttendanceResourceTest extends BaseResourceTest {

  private record AttendanceGraph(
      Project project, FormerStudent formerStudent, Attendance attendance) {}

  private AttendanceGraph createAttendanceGraph() throws Exception {
    Project[] project = new Project[1];
    FormerStudent[] formerStudent = new FormerStudent[1];
    Attendance[] attendance = new Attendance[1];
    doInTransaction(
        () -> {
          School school = factory.createSchool();
          Course course = factory.createCourse(school);
          Account acc = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
          formerStudent[0] = factory.createStudent(acc, course);
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
          factory.createEnrollment(formerStudent[0], project[0]);
          attendance[0] = factory.createAttendance(project[0], formerStudent[0]);
        });
    return new AttendanceGraph(project[0], formerStudent[0], attendance[0]);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/projects/attendances/{id} - Success")
  void getByIdSuccess() throws Exception {
    AttendanceGraph g = createAttendanceGraph();

    given()
        .pathParam("id", g.attendance().getId())
        .when()
        .get("/v1/projects/attendances/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.id", is(g.attendance().getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/projects/attendances/{id} - Not Found")
  void getByIdNotFound() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .get("/v1/projects/attendances/{id}")
        .then()
        .statusCode(404);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/projects/attendances - List All")
  void listAll() throws Exception {
    createAttendanceGraph();

    given()
        .when()
        .get("/v1/projects/attendances")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/projects/attendances?projectId= - Filter by Project")
  void listByProjectId() throws Exception {
    AttendanceGraph g = createAttendanceGraph();

    given()
        .queryParam("projectId", g.project().getId().toString())
        .when()
        .get("/v1/projects/attendances")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/projects/attendances?studentId= - Filter by FormerStudent")
  void listByStudentId() throws Exception {
    AttendanceGraph g = createAttendanceGraph();

    given()
        .queryParam("studentId", g.formerStudent().getAccountId().toString())
        .when()
        .get("/v1/projects/attendances")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/projects/attendances?projectId=&studentId= - Filter by Enrollment")
  void listByEnrollmentId() throws Exception {
    AttendanceGraph g = createAttendanceGraph();

    given()
        .queryParam("projectId", g.project().getId().toString())
        .queryParam("studentId", g.formerStudent().getAccountId().toString())
        .when()
        .get("/v1/projects/attendances")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("DELETE /v1/projects/attendances/{id} - Success")
  void deleteSuccess() throws Exception {
    AttendanceGraph g = createAttendanceGraph();

    given()
        .pathParam("id", g.attendance().getId())
        .when()
        .delete("/v1/projects/attendances/{id}")
        .then()
        .statusCode(204);
  }

  @Test
  @DisplayName("Should return 401 when accessing without authentication")
  void unauthorizedAccess() {
    assertUnauthenticated("/v1/projects/attendances");
  }
}
