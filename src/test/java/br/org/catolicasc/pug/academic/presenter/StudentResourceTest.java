package br.org.catolicasc.pug.academic.presenter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.presenter.dtos.StudentCreateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.StudentUpdateRequest;
import br.org.catolicasc.pug.helpers.BaseResourceTest;
import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.project.service.EnrollmentService;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("StudentResource Integration Tests")
class StudentResourceTest extends BaseResourceTest {

  @InjectMock AuditPublisher audit;
  @InjectMock AuthService authService;
  @InjectMock EnrollmentService enrollmentService;

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/academic/students/{id} - Success")
  void getByIdSuccess() throws Exception {
    Account[] account = new Account[1];
    doInTransaction(
        () -> {
          School school = factory.createSchool();
          Course course = factory.createCourse(school);
          User user = factory.createUser();
          account[0] = factory.createAccount(user, AccountType.STUDENT);
          factory.createStudent(account[0], course);
        });

    given()
        .pathParam("id", account[0].getId())
        .when()
        .get("/v1/academic/students/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.accountId", is(account[0].getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/academic/students/{id} - Not Found")
  void getByIdNotFound() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .get("/v1/academic/students/{id}")
        .then()
        .statusCode(404);
  }

  @Test
  @TestSecurity(
      user = "user",
      roles = {"STUDENT"})
  @DisplayName("GET /v1/academic/students - List All")
  void listAll() throws Exception {
    doInTransaction(
        () -> {
          School school = factory.createSchool();
          Course course = factory.createCourse(school);
          User user = factory.createUser();
          Account account = factory.createAccount(user, AccountType.STUDENT);
          factory.createStudent(account, course);
        });

    given()
        .when()
        .get("/v1/academic/students")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "user",
      roles = {"STUDENT"})
  @DisplayName("GET /v1/academic/students?courseId= - Filter by Course")
  void listByCourseId() throws Exception {
    Course[] course = new Course[1];
    doInTransaction(
        () -> {
          School school = factory.createSchool();
          course[0] = factory.createCourse(school);
          User user = factory.createUser();
          Account account = factory.createAccount(user, AccountType.STUDENT);
          factory.createStudent(account, course[0]);
        });

    given()
        .queryParam("courseId", course[0].getId().toString())
        .when()
        .get("/v1/academic/students")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/academic/students - Success")
  void createSuccess() throws Exception {
    Course[] course = new Course[1];
    doInTransaction(
        () -> {
          School school = factory.createSchool();
          course[0] = factory.createCourse(school);
        });

    String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
    StudentCreateRequest req =
        new StudentCreateRequest(
            cpf,
            "Test Student",
            cpf + "@test.com",
            "password123",
            UuidCreator.getTimeOrderedEpoch().toString().substring(24).toUpperCase(),
            Campi.JOINVILLE,
            course[0].getId(),
            new BigDecimal("100"),
            LocalDate.now(),
            LocalDate.now().plusMonths(6));

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/v1/academic/students")
        .then()
        .statusCode(201)
        .body("data.accountId", notNullValue());
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/academic/students/bulk - Success")
  void createBulkSuccess() throws Exception {
    Course[] course = new Course[1];
    doInTransaction(
        () -> {
          School school = factory.createSchool();
          course[0] = factory.createCourse(school);
        });

    StudentCreateRequest req1 = buildCreateRequest(course[0].getId());
    StudentCreateRequest req2 = buildCreateRequest(course[0].getId());

    given()
        .contentType(ContentType.JSON)
        .body(List.of(req1, req2))
        .when()
        .post("/v1/academic/students/bulk")
        .then()
        .statusCode(201)
        .body("data", hasSize(2));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PUT /v1/academic/students/{id} - Success")
  void updateSuccess() throws Exception {
    Account[] account = new Account[1];
    doInTransaction(
        () -> {
          School school = factory.createSchool();
          Course course = factory.createCourse(school);
          User user = factory.createUser();
          account[0] = factory.createAccount(user, AccountType.STUDENT);
          factory.createStudent(account[0], course);
        });

    StudentUpdateRequest req =
        new StudentUpdateRequest(
            null, null, null, null, null, Campi.JOINVILLE, null, null, null, null);

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", account[0].getId())
        .body(req)
        .when()
        .put("/v1/academic/students/{id}")
        .then()
        .statusCode(200)
        .body("data.accountId", is(account[0].getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("DELETE /v1/academic/students/{id} - Success")
  void deleteSuccess() throws Exception {
    Account[] account = new Account[1];
    doInTransaction(
        () -> {
          School school = factory.createSchool();
          Course course = factory.createCourse(school);
          User user = factory.createUser();
          account[0] = factory.createAccount(user, AccountType.STUDENT);
          factory.createStudent(account[0], course);
        });

    when(enrollmentService.existsAnyByStudentId(account[0].getId())).thenReturn(false);

    given()
        .pathParam("id", account[0].getId())
        .when()
        .delete("/v1/academic/students/{id}")
        .then()
        .statusCode(200);
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("GET /v1/academic/students/me - Success")
  void getMeSuccess() throws Exception {
    Account[] account = new Account[1];
    doInTransaction(
        () -> {
          School school = factory.createSchool();
          Course course = factory.createCourse(school);
          User user = factory.createUser();
          account[0] = factory.createAccount(user, AccountType.STUDENT);
          factory.createStudent(account[0], course);
        });

    when(authService.getCurrentAccountId()).thenReturn(account[0].getId());

    given()
        .when()
        .get("/v1/academic/students/me")
        .then()
        .statusCode(200)
        .body("data.accountId", is(account[0].getId().toString()));
  }

  @Test
  @DisplayName("Should return 401 when unauthenticated")
  void unauthorizedAccess() {
    assertUnauthenticated("/v1/academic/students");
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("POST /v1/academic/students - Forbidden for STUDENT")
  void createForbiddenForStudent() throws Exception {
    Course[] course = new Course[1];
    doInTransaction(
        () -> {
          School school = factory.createSchool();
          course[0] = factory.createCourse(school);
        });

    StudentCreateRequest req = buildCreateRequest(course[0].getId());

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/v1/academic/students")
        .then()
        .statusCode(403);
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("DELETE /v1/academic/students/{id} - Forbidden for STUDENT")
  void deleteForbiddenForStudent() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .delete("/v1/academic/students/{id}")
        .then()
        .statusCode(403);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/academic/students?cpf= - Success")
  void getByCpfSuccess() throws Exception {
    User[] user = new User[1];
    Account[] account = new Account[1];
    doInTransaction(
        () -> {
          School school = factory.createSchool();
          Course course = factory.createCourse(school);
          user[0] = factory.createUser();
          account[0] = factory.createAccount(user[0], AccountType.STUDENT);
          factory.createStudent(account[0], course);
        });

    given()
        .queryParam("cpf", user[0].getCpf().getValue())
        .when()
        .get("/v1/academic/students")
        .then()
        .statusCode(200)
        .body("data.accountId", is(account[0].getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/academic/students?email= - Success")
  void getByEmailSuccess() throws Exception {
    Account[] account = new Account[1];
    doInTransaction(
        () -> {
          School school = factory.createSchool();
          Course course = factory.createCourse(school);
          User user = factory.createUser();
          account[0] = factory.createAccount(user, AccountType.STUDENT);
          factory.createStudent(account[0], course);
        });

    given()
        .queryParam("email", account[0].getEmail().getValue())
        .when()
        .get("/v1/academic/students")
        .then()
        .statusCode(200)
        .body("data.accountId", is(account[0].getId().toString()));
  }

  /* --- helpers --- */

  private StudentCreateRequest buildCreateRequest(UUID courseId) {
    String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
    String reg = UuidCreator.getTimeOrderedEpoch().toString().substring(24).toUpperCase();
    return new StudentCreateRequest(
        cpf,
        "Test Student " + cpf,
        cpf + "@test.com",
        "password123",
        reg,
        Campi.JOINVILLE,
        courseId,
        new BigDecimal("100"),
        LocalDate.now(),
        LocalDate.now().plusMonths(6));
  }
}
