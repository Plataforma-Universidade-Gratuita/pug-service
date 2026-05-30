package br.org.catolicasc.pug.academic.presenter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise.AreaOfExpertiseComplexSearchRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise.AreaOfExpertiseCreateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise.AreaOfExpertiseUpdateRequest;
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
@DisplayName("AreasOfExpertiseResource Integration Tests")
class AreasOfExpertiseResourceTest extends BaseResourceTest {

  @InjectMock AuditPublisher audit;

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/academic/areas-of-expertise/{id} - Success")
  void getByIdSuccess() throws Exception {
    AreaOfExpertise[] area = new AreaOfExpertise[1];
    doInTransaction(() -> area[0] = factory.createAreaOfExpertise());

    given()
        .pathParam("id", area[0].getId())
        .when()
        .get("/v1/academic/areas-of-expertise/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.id", is(area[0].getId().toString()))
        .body("data.name", is(area[0].getName()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/academic/areas-of-expertise/{id} - Not Found")
  void getByIdNotFound() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .get("/v1/academic/areas-of-expertise/{id}")
        .then()
        .statusCode(404)
        .body("success", is(false));
  }

  @Test
  @TestSecurity(
      user = "formerStudent",
      roles = {"FORMER_STUDENT"})
  @DisplayName("GET /v1/academic/areas-of-expertise - List All")
  void listAll() throws Exception {
    doInTransaction(() -> factory.createAreaOfExpertise());

    given()
        .when()
        .get("/v1/academic/areas-of-expertise")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "formerStudent",
      roles = {"FORMER_STUDENT"})
  @DisplayName("GET /v1/academic/areas-of-expertise?ids= - Filter by IDs")
  void listAllByIds() throws Exception {
    AreaOfExpertise[] area = new AreaOfExpertise[1];
    doInTransaction(() -> area[0] = factory.createAreaOfExpertise());

    given()
        .queryParam("ids", area[0].getId().toString())
        .when()
        .get("/v1/academic/areas-of-expertise")
        .then()
        .statusCode(200)
        .body("data", hasSize(1))
        .body("data[0].id", is(area[0].getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "formerStudent",
      roles = {"FORMER_STUDENT"})
  @DisplayName("POST /v1/academic/areas-of-expertise/search - Success")
  void searchSuccess() throws Exception {
    AreaOfExpertise[] area = new AreaOfExpertise[1];
    doInTransaction(() -> area[0] = factory.createAreaOfExpertise());

    AreaOfExpertiseComplexSearchRequest request =
        new AreaOfExpertiseComplexSearchRequest(area[0].getName().substring(0, 3));

    given()
        .contentType(ContentType.JSON)
        .queryParam("page", 0)
        .queryParam("size", 10)
        .body(request)
        .when()
        .post("/v1/academic/areas-of-expertise/search")
        .then()
        .statusCode(200)
        .body("data.content", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/academic/areas-of-expertise - Success")
  void createSuccess() {
    AreaOfExpertiseCreateRequest req =
        new AreaOfExpertiseCreateRequest("New Area " + UuidCreator.getTimeOrderedEpoch());

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/v1/academic/areas-of-expertise")
        .then()
        .statusCode(201)
        .body("data.name", notNullValue());
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/academic/areas-of-expertise - Duplicate Name")
  void createDuplicate() throws Exception {
    AreaOfExpertise[] existing = new AreaOfExpertise[1];
    doInTransaction(() -> existing[0] = factory.createAreaOfExpertise());

    AreaOfExpertiseCreateRequest req = new AreaOfExpertiseCreateRequest(existing[0].getName());

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/v1/academic/areas-of-expertise")
        .then()
        .statusCode(409);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PUT /v1/academic/areas-of-expertise/{id} - Success")
  void updateSuccess() throws Exception {
    AreaOfExpertise[] area = new AreaOfExpertise[1];
    doInTransaction(() -> area[0] = factory.createAreaOfExpertise());

    AreaOfExpertiseUpdateRequest req =
        new AreaOfExpertiseUpdateRequest("Updated " + UuidCreator.getTimeOrderedEpoch());

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", area[0].getId())
        .body(req)
        .when()
        .put("/v1/academic/areas-of-expertise/{id}")
        .then()
        .statusCode(200)
        .body("data.name", notNullValue());
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("DELETE /v1/academic/areas-of-expertise/{id} - Success")
  void deleteSuccess() throws Exception {
    AreaOfExpertise[] area = new AreaOfExpertise[1];
    doInTransaction(() -> area[0] = factory.createAreaOfExpertise());

    given()
        .pathParam("id", area[0].getId())
        .when()
        .delete("/v1/academic/areas-of-expertise/{id}")
        .then()
        .statusCode(204);
  }

  @Test
  @DisplayName("Should return 401 when unauthenticated")
  void unauthorizedAccess() {
    assertUnauthenticated("/v1/academic/areas-of-expertise");
  }
}
