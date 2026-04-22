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
import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.helpers.TestDataFactory;
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
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("StudentResource Integration Tests")
class StudentResourceTest {

  @Inject TestDataFactory factory;
  @Inject UserTransaction utx;
  @Inject EntityManager em;

  @InjectMock AuditPublisher audit;
  @InjectMock AuthService authService;
  @InjectMock EnrollmentService enrollmentService;

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /academic/students/{id} - Success")
  void getByIdSuccess() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.STUDENT);
    factory.createStudent(account, course);
    em.flush();
    utx.commit();

    given()
        .pathParam("id", account.getId())
        .when()
        .get("/academic/students/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.accountId", is(account.getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /academic/students/{id} - Not Found")
  void getByIdNotFound() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .get("/academic/students/{id}")
        .then()
        .statusCode(404);
  }

  @Test
  @TestSecurity(
      user = "user",
      roles = {"STUDENT"})
  @DisplayName("GET /academic/students - List All")
  void listAll() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.STUDENT);
    factory.createStudent(account, course);
    utx.commit();

    given()
        .when()
        .get("/academic/students")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "user",
      roles = {"STUDENT"})
  @DisplayName("GET /academic/students?courseId= - Filter by Course")
  void listByCourseId() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.STUDENT);
    factory.createStudent(account, course);
    utx.commit();

    given()
        .queryParam("courseId", course.getId().toString())
        .when()
        .get("/academic/students")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /academic/students - Success")
  void createSuccess() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    em.flush();
    utx.commit();

    String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
    StudentCreateRequest req =
        new StudentCreateRequest(
            cpf,
            "Test Student",
            cpf + "@test.com",
            "password123",
            UUID.randomUUID().toString().substring(0, 14).toUpperCase(),
            Campi.JOINVILLE,
            course.getId(),
            new BigDecimal("100"),
            LocalDate.now(),
            LocalDate.now().plusMonths(6));

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/academic/students")
        .then()
        .statusCode(201)
        .body("data.accountId", notNullValue());
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /academic/students/bulk - Success")
  void createBulkSuccess() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    em.flush();
    utx.commit();

    StudentCreateRequest req1 = buildCreateRequest(course.getId());
    StudentCreateRequest req2 = buildCreateRequest(course.getId());

    given()
        .contentType(ContentType.JSON)
        .body(List.of(req1, req2))
        .when()
        .post("/academic/students/bulk")
        .then()
        .statusCode(201)
        .body("data", hasSize(2));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PUT /academic/students/{id} - Success")
  void updateSuccess() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.STUDENT);
    factory.createStudent(account, course);
    em.flush();
    utx.commit();

    StudentUpdateRequest req =
        new StudentUpdateRequest(
            null, null, null, null, null, Campi.JOINVILLE, null, null, null, null);

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", account.getId())
        .body(req)
        .when()
        .put("/academic/students/{id}")
        .then()
        .statusCode(200)
        .body("data.accountId", is(account.getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("DELETE /academic/students/{id} - Success")
  void deleteSuccess() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.STUDENT);
    factory.createStudent(account, course);
    em.flush();
    utx.commit();

    when(enrollmentService.existsAnyByStudentId(account.getId())).thenReturn(false);

    given()
        .pathParam("id", account.getId())
        .when()
        .delete("/academic/students/{id}")
        .then()
        .statusCode(200);
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("GET /academic/students/me - Success")
  void getMeSuccess() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.STUDENT);
    factory.createStudent(account, course);
    em.flush();
    utx.commit();

    when(authService.getCurrentAccountId()).thenReturn(account.getId());

    given()
        .when()
        .get("/academic/students/me")
        .then()
        .statusCode(200)
        .body("data.accountId", is(account.getId().toString()));
  }

  @Test
  @DisplayName("Should return 401 when unauthenticated")
  void unauthorizedAccess() {
    given().when().get("/academic/students").then().statusCode(401);
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("POST /academic/students - Forbidden for STUDENT")
  void createForbiddenForStudent() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    utx.commit();

    StudentCreateRequest req = buildCreateRequest(course.getId());

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/academic/students")
        .then()
        .statusCode(403);
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("DELETE /academic/students/{id} - Forbidden for STUDENT")
  void deleteForbiddenForStudent() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .delete("/academic/students/{id}")
        .then()
        .statusCode(403);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /academic/students/by-cpf/{cpf} - Success")
  void getByCpfSuccess() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.STUDENT);
    factory.createStudent(account, course);
    em.flush();
    utx.commit();

    given()
        .pathParam("cpf", user.getCpf().getValue())
        .when()
        .get("/academic/students/by-cpf/{cpf}")
        .then()
        .statusCode(200)
        .body("data.accountId", is(account.getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /academic/students/by-email/{email} - Success")
  void getByEmailSuccess() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.STUDENT);
    factory.createStudent(account, course);
    em.flush();
    utx.commit();

    given()
        .pathParam("email", account.getEmail().getValue())
        .when()
        .get("/academic/students/by-email/{email}")
        .then()
        .statusCode(200)
        .body("data.accountId", is(account.getId().toString()));
  }

  /* --- helpers --- */

  private StudentCreateRequest buildCreateRequest(UUID courseId) {
    String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
    String reg = UUID.randomUUID().toString().substring(0, 14).toUpperCase();
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
