package br.org.catolicasc.pug.academic.presenter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.domain.vos.AcademicRegistration;
import br.org.catolicasc.pug.academic.domain.vos.CounterpartHours;
import br.org.catolicasc.pug.academic.domain.vos.Period;
import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentView;
import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentComplexSearchRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentCreateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentUpdateRequest;
import br.org.catolicasc.pug.academic.service.FormerStudentsReadService;
import br.org.catolicasc.pug.academic.service.FormerStudentsService;
import br.org.catolicasc.pug.identity.presenter.dtos.accounts.AccountStatusRequest;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("FormerStudentsResource Integration Tests")
class FormerStudentsResourceTest {

  @InjectMock FormerStudentsService writeService;
  @InjectMock FormerStudentsReadService readService;
  @InjectMock AuthService authService;

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/academic/former-students/{id} - Success")
  void getByIdSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    when(readService.getViewByAccountId(id)).thenReturn(view(id));

    given()
        .pathParam("id", id)
        .when()
        .get("/v1/academic/former-students/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.accountId", is(id.toString()))
        .body("data.academicRegistration", is("REG123"));
  }

  @Test
  @TestSecurity(
      user = "formerStudent",
      roles = {"FORMER_STUDENT"})
  @DisplayName("GET /v1/academic/former-students/me - Success")
  void getMeSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    when(authService.getCurrentAccountId()).thenReturn(id);
    when(readService.getViewByAccountId(id)).thenReturn(view(id));

    given()
        .when()
        .get("/v1/academic/former-students/me")
        .then()
        .statusCode(200)
        .body("data.accountId", is(id.toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/academic/former-students - List All")
  void listAll() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    when(readService.listViews()).thenReturn(List.of(view(id)));

    given()
        .when()
        .get("/v1/academic/former-students")
        .then()
        .statusCode(200)
        .body("data", hasSize(1));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/academic/former-students?ids= - Filter by IDs")
  void listByIds() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    when(readService.listViewsByIds(List.of(id))).thenReturn(List.of(view(id)));

    given()
        .queryParam("ids", id.toString())
        .when()
        .get("/v1/academic/former-students")
        .then()
        .statusCode(200)
        .body("data", hasSize(1))
        .body("data[0].accountId", is(id.toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/academic/former-students/search - Success")
  void searchSuccess() {
    FormerStudentComplexSearchRequest request =
        new FormerStudentComplexSearchRequest(
            null, null, null, null, null, null, null, true, null, null, true, null, null);

    when(readService.search(any(), any())).thenReturn(new PageResult<>(List.of(), 0, 10, 0, 0));

    given()
        .contentType(ContentType.JSON)
        .queryParam("page", 0)
        .queryParam("size", 10)
        .body(request)
        .when()
        .post("/v1/academic/former-students/search")
        .then()
        .statusCode(200)
        .body("data.content", hasSize(greaterThanOrEqualTo(0)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/academic/former-students/search - Null Request")
  void searchNullRequest() {
    when(readService.search(any(), any())).thenReturn(new PageResult<>(List.of(), 0, 25, 0, 0));

    given()
        .contentType(ContentType.JSON)
        .when()
        .post("/v1/academic/former-students/search")
        .then()
        .statusCode(200)
        .body("data.content", hasSize(0));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/academic/former-students - Success")
  void createSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    when(writeService.save(any())).thenReturn(student(id));
    when(readService.getViewByAccountId(id)).thenReturn(view(id));

    FormerStudentCreateRequest request =
        new FormerStudentCreateRequest(
            "12345678901",
            "Student",
            "student@example.com",
            "REG123",
            Campi.JOINVILLE,
            UuidCreator.getTimeOrderedEpoch(),
            new BigDecimal("100"),
            LocalDate.now(),
            LocalDate.now().plusMonths(6));

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/v1/academic/former-students")
        .then()
        .statusCode(201)
        .body("data.accountId", is(id.toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/academic/former-students/bulk - Success")
  void createBulkSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    when(writeService.saveInBulk(any())).thenReturn(List.of(student(id)));
    when(readService.listViewsByIds(any())).thenReturn(List.of(view(id)));

    FormerStudentCreateRequest request =
        new FormerStudentCreateRequest(
            "12345678901",
            "Student",
            "student@example.com",
            "REG123",
            Campi.JOINVILLE,
            UuidCreator.getTimeOrderedEpoch(),
            new BigDecimal("100"),
            LocalDate.now(),
            LocalDate.now().plusMonths(6));

    given()
        .contentType(ContentType.JSON)
        .body(List.of(request))
        .when()
        .post("/v1/academic/former-students/bulk")
        .then()
        .statusCode(201)
        .body("data", hasSize(1));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PUT /v1/academic/former-students/{id} - Success")
  void updateSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    when(writeService.update(any(), any())).thenReturn(student(id));
    when(readService.getViewByAccountId(id)).thenReturn(view(id));

    FormerStudentUpdateRequest request =
        new FormerStudentUpdateRequest(
            "Updated",
            null,
            "updated@example.com",
            "REG999",
            Campi.JARAGUA_DO_SUL,
            null,
            null,
            null,
            null);

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", id)
        .body(request)
        .when()
        .put("/v1/academic/former-students/{id}")
        .then()
        .statusCode(200)
        .body("data.accountId", is(id.toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PATCH /v1/academic/former-students/{id}/status - Success")
  void updateStatusSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    when(writeService.updateStatus(id, false)).thenReturn(student(id));

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", id)
        .body(new AccountStatusRequest(false))
        .when()
        .patch("/v1/academic/former-students/{id}/status")
        .then()
        .statusCode(204);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("DELETE /v1/academic/former-students/{id} - Success")
  void deleteSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    when(writeService.delete(id)).thenReturn(true);

    given()
        .pathParam("id", id)
        .when()
        .delete("/v1/academic/former-students/{id}")
        .then()
        .statusCode(204);
  }

  @Test
  @DisplayName("Should return 401 when unauthenticated")
  void unauthorizedAccess() {
    given().when().get("/v1/academic/former-students").then().statusCode(401);
  }

  @Test
  @TestSecurity(
      user = "formerStudent",
      roles = {"FORMER_STUDENT"})
  @DisplayName("POST /v1/academic/former-students - Forbidden for FORMER_STUDENT")
  void createForbiddenForFormerStudent() {
    given()
        .contentType(ContentType.JSON)
        .body("{}")
        .when()
        .post("/v1/academic/former-students")
        .then()
        .statusCode(403);
  }

  private FormerStudentView view(UUID id) {
    OffsetDateTime now = OffsetDateTime.now();
    LocalDate today = LocalDate.now();
    return new FormerStudentView(
        id,
        "REG123",
        Campi.JOINVILLE,
        UuidCreator.getTimeOrderedEpoch(),
        new BigDecimal("100"),
        BigDecimal.ZERO,
        false,
        today,
        today.plusMonths(6),
        now,
        now);
  }

  private FormerStudent student(UUID id) {
    return FormerStudent.factory(
        id,
        AcademicRegistration.factory("REG123"),
        Campi.JOINVILLE,
        UuidCreator.getTimeOrderedEpoch(),
        CounterpartHours.factory(new BigDecimal("100"), BigDecimal.ZERO, false),
        Period.factory(LocalDate.now(), LocalDate.now().plusMonths(6)));
  }
}
