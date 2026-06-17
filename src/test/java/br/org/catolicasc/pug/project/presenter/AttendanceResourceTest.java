package br.org.catolicasc.pug.project.presenter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.helpers.BaseResourceTest;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import br.org.catolicasc.pug.project.presenter.dtos.attendance.AttendanceComplexSearchRequest;
import br.org.catolicasc.pug.project.presenter.dtos.attendance.AttendanceCreateRequest;
import br.org.catolicasc.pug.project.presenter.dtos.attendance.AttendanceValidateRequest;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AttendancesResource Integration Tests")
class AttendanceResourceTest extends BaseResourceTest {

  @InjectMock AuthService authService;

  private record AttendanceGraph(
      Project project, FormerStudent formerStudent, Attendance attendance) {}

  private AttendanceGraph createAttendanceGraph() throws Exception {
    Project[] project = new Project[1];
    FormerStudent[] formerStudent = new FormerStudent[1];
    Attendance[] attendance = new Attendance[1];
    doInTransaction(
        () -> {
          AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
          Course course = factory.createCourse(areaOfExpertise);
          Account acc = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
          formerStudent[0] = factory.createStudent(acc, course);
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
          project[0] = project[0].start();
          factory.updateProject(project[0]);
          factory.createApprovedEnrollment(formerStudent[0], project[0]);
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
    AttendanceGraph graph = createAttendanceGraph();

    given()
        .pathParam("id", graph.attendance().getId())
        .when()
        .get("/v1/projects/attendances/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.id", is(graph.attendance().getId().toString()))
        .body("data.status.status", is("WAITING"));
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
  @DisplayName("GET /v1/projects/attendances?ids= - Filter by IDs")
  void listAllByIds() throws Exception {
    AttendanceGraph graph = createAttendanceGraph();

    given()
        .queryParam("ids", graph.attendance().getId().toString())
        .when()
        .get("/v1/projects/attendances")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)))
        .body("data[0].id", is(graph.attendance().getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/projects/attendances/search - Success")
  void searchSuccess() throws Exception {
    AttendanceGraph graph = createAttendanceGraph();

    given()
        .contentType(ContentType.JSON)
        .queryParam("page", 0)
        .queryParam("size", 25)
        .body(
            new AttendanceComplexSearchRequest(
                List.of(graph.project().getId()),
                List.of(graph.formerStudent().getAccountId()),
                List.of(AttendanceStatus.WAITING),
                List.of(),
                null,
                null,
                null,
                null))
        .when()
        .post("/v1/projects/attendances/search")
        .then()
        .statusCode(200)
        .body("data.content", hasSize(greaterThanOrEqualTo(1)))
        .body("data.content[0].status.status", is("WAITING"))
        .body("data.content[0].project.id", is(graph.project().getId().toString()))
        .body(
            "data.content[0].student.account.id",
            is(graph.formerStudent().getAccountId().toString()));
  }

  @Test
  @TestSecurity(
      user = "staff",
      roles = {"PARTNER"})
  @DisplayName("POST /v1/projects/attendances - Success")
  void createSuccess() throws Exception {
    AttendanceGraph graph = createAttendanceGraph();

    AttendanceCreateRequest request =
        new AttendanceCreateRequest(
            graph.project().getId(), graph.formerStudent().getAccountId(), BigDecimal.TEN);

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/v1/projects/attendances")
        .then()
        .statusCode(201)
        .body("data.projectId", is(graph.project().getId().toString()))
        .body("data.formerStudentId", is(graph.formerStudent().getAccountId().toString()));
  }

  @Test
  @TestSecurity(
      user = "staff",
      roles = {"PARTNER"})
  @DisplayName("PATCH /v1/projects/attendances/{id}/validate - Success")
  void validateSuccess() throws Exception {
    AttendanceGraph graph = createAttendanceGraph();
    Account[] validator = new Account[1];
    doInTransaction(
        () -> validator[0] = factory.createAccount(factory.createUser(), AccountType.ADMIN));

    when(authService.getCurrentAccountId()).thenReturn(validator[0].getId());
    doNothing().when(authService).requireCurrentAccountNotOfType(AccountType.FORMER_STUDENT);

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", graph.attendance().getId())
        .body(
            new AttendanceValidateRequest(
                AttendanceStatus.PRESENT,
                graph.attendance().getQrValidationInfo().getQrValidationHash()))
        .when()
        .patch("/v1/projects/attendances/{id}/validate")
        .then()
        .statusCode(200)
        .body("data.id", is(graph.attendance().getId().toString()))
        .body("data.status.status", is("PRESENT"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("DELETE /v1/projects/attendances/{id} - Success")
  void deleteSuccess() throws Exception {
    AttendanceGraph graph = createAttendanceGraph();

    given()
        .pathParam("id", graph.attendance().getId())
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
