package br.org.catolicasc.pug.academic.presenter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.presenter.dtos.SchoolCreateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.SchoolUpdateRequest;
import br.org.catolicasc.pug.helpers.BaseResourceTest;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("SchoolResource Integration Tests")
class SchoolResourceTest extends BaseResourceTest {

  @InjectMock AuditPublisher audit;

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/academic/schools/{id} - Success")
  void getByIdSuccess() throws Exception {
    School[] school = new School[1];
    doInTransaction(() -> school[0] = factory.createSchool());

    given()
        .pathParam("id", school[0].getId())
        .when()
        .get("/v1/academic/schools/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.id", is(school[0].getId().toString()))
        .body("data.name", is(school[0].getName()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/academic/schools/{id} - Not Found")
  void getByIdNotFound() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .get("/v1/academic/schools/{id}")
        .then()
        .statusCode(404)
        .body("success", is(false));
  }

  @Test
  @TestSecurity(
      user = "user",
      roles = {"STUDENT"})
  @DisplayName("GET /v1/academic/schools - List All")
  void listAll() throws Exception {
    doInTransaction(() -> factory.createSchool());

    given()
        .when()
        .get("/v1/academic/schools")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "user",
      roles = {"STUDENT"})
  @DisplayName("GET /v1/academic/schools?q=xxx - Search")
  void searchByName() throws Exception {
    doInTransaction(() -> factory.createSchool());

    given()
        .queryParam("q", "NonExistentSchoolName")
        .when()
        .get("/v1/academic/schools")
        .then()
        .statusCode(200);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/academic/schools - Success")
  void createSuccess() {
    SchoolCreateRequest req =
        new SchoolCreateRequest("New School " + UuidCreator.getTimeOrderedEpoch());

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/v1/academic/schools")
        .then()
        .statusCode(201)
        .body("data.name", notNullValue());
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/academic/schools - Duplicate Name")
  void createDuplicate() throws Exception {
    School[] existing = new School[1];
    doInTransaction(() -> existing[0] = factory.createSchool());

    SchoolCreateRequest req = new SchoolCreateRequest(existing[0].getName());

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/v1/academic/schools")
        .then()
        .statusCode(409);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PUT /v1/academic/schools/{id} - Success")
  void updateSuccess() throws Exception {
    School[] school = new School[1];
    doInTransaction(() -> school[0] = factory.createSchool());

    SchoolUpdateRequest req =
        new SchoolUpdateRequest("Updated " + UuidCreator.getTimeOrderedEpoch());

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", school[0].getId())
        .body(req)
        .when()
        .put("/v1/academic/schools/{id}")
        .then()
        .statusCode(200)
        .body("data.name", notNullValue());
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("DELETE /v1/academic/schools/{id} - Success")
  void deleteSuccess() throws Exception {
    School[] school = new School[1];
    doInTransaction(() -> school[0] = factory.createSchool());

    given()
        .pathParam("id", school[0].getId())
        .when()
        .delete("/v1/academic/schools/{id}")
        .then()
        .statusCode(204);
  }

  @Test
  @DisplayName("Should return 401 when unauthenticated")
  void unauthorizedAccess() {
    assertUnauthenticated("/v1/academic/schools");
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("POST /v1/academic/schools - Forbidden for STUDENT")
  void createForbiddenForStudent() {
    SchoolCreateRequest req = new SchoolCreateRequest("Forbidden School");

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/v1/academic/schools")
        .then()
        .statusCode(403);
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("DELETE /v1/academic/schools/{id} - Forbidden for STUDENT")
  void deleteForbiddenForStudent() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .delete("/v1/academic/schools/{id}")
        .then()
        .statusCode(403);
  }
}
