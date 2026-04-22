package br.org.catolicasc.pug.academic.presenter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.presenter.dtos.SchoolCreateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.SchoolUpdateRequest;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("SchoolResource Integration Tests")
class SchoolResourceTest {

  @Inject TestDataFactory factory;
  @Inject UserTransaction utx;
  @Inject EntityManager em;

  @InjectMock AuditPublisher audit;

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /academic/schools/{id} - Success")
  void getByIdSuccess() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    em.flush();
    utx.commit();

    given()
        .pathParam("id", school.getId())
        .when()
        .get("/academic/schools/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.id", is(school.getId().toString()))
        .body("data.name", is(school.getName()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /academic/schools/{id} - Not Found")
  void getByIdNotFound() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .get("/academic/schools/{id}")
        .then()
        .statusCode(404)
        .body("success", is(false));
  }

  @Test
  @TestSecurity(
      user = "user",
      roles = {"STUDENT"})
  @DisplayName("GET /academic/schools - List All")
  void listAll() throws Exception {
    utx.begin();
    factory.createSchool();
    utx.commit();

    given()
        .when()
        .get("/academic/schools")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "user",
      roles = {"STUDENT"})
  @DisplayName("GET /academic/schools?q=xxx - Search")
  void searchByName() throws Exception {
    utx.begin();
    factory.createSchool();
    utx.commit();

    given()
        .queryParam("q", "NonExistentSchoolName")
        .when()
        .get("/academic/schools")
        .then()
        .statusCode(200);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /academic/schools - Success")
  void createSuccess() {
    SchoolCreateRequest req = new SchoolCreateRequest("New School " + UUID.randomUUID());

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/academic/schools")
        .then()
        .statusCode(201)
        .body("data.name", notNullValue());
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /academic/schools - Duplicate Name")
  void createDuplicate() throws Exception {
    utx.begin();
    School existing = factory.createSchool();
    em.flush();
    utx.commit();

    SchoolCreateRequest req = new SchoolCreateRequest(existing.getName());

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/academic/schools")
        .then()
        .statusCode(409);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PUT /academic/schools/{id} - Success")
  void updateSuccess() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    em.flush();
    utx.commit();

    SchoolUpdateRequest req = new SchoolUpdateRequest("Updated " + UUID.randomUUID());

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", school.getId())
        .body(req)
        .when()
        .put("/academic/schools/{id}")
        .then()
        .statusCode(200)
        .body("data.name", notNullValue());
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("DELETE /academic/schools/{id} - Success")
  void deleteSuccess() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    em.flush();
    utx.commit();

    given()
        .pathParam("id", school.getId())
        .when()
        .delete("/academic/schools/{id}")
        .then()
        .statusCode(200);
  }

  @Test
  @DisplayName("Should return 401 when unauthenticated")
  void unauthorizedAccess() {
    given().when().get("/academic/schools").then().statusCode(401);
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("POST /academic/schools - Forbidden for STUDENT")
  void createForbiddenForStudent() {
    SchoolCreateRequest req = new SchoolCreateRequest("Forbidden School");

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/academic/schools")
        .then()
        .statusCode(403);
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("DELETE /academic/schools/{id} - Forbidden for STUDENT")
  void deleteForbiddenForStudent() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .delete("/academic/schools/{id}")
        .then()
        .statusCode(403);
  }
}
