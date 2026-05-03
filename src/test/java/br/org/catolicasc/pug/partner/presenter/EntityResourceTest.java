package br.org.catolicasc.pug.partner.presenter;

import static br.org.catolicasc.pug.helpers.builders.requests.EntityCreateRequestBuilder.anEntityCreateRequest;
import static br.org.catolicasc.pug.helpers.builders.requests.EntityUpdateRequestBuilder.anEntityUpdateRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import br.org.catolicasc.pug.geo.domain.City;
import br.org.catolicasc.pug.helpers.BaseResourceTest;
import br.org.catolicasc.pug.partner.domain.Entity;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("EntityResource Integration Tests")
class EntityResourceTest extends BaseResourceTest {

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/partners/entities/{id} - Success")
  void getByIdSuccess() throws Exception {
    Entity[] entity = new Entity[1];
    doInTransaction(
        () -> {
          City city = factory.getAnyCity();
          entity[0] = factory.createEntity(city);
        });

    given()
        .pathParam("id", entity[0].getId())
        .when()
        .get("/v1/partners/entities/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.id", is(entity[0].getId().toString()))
        .body("data.cnpjFormatted", notNullValue())
        .body("data.name", is(entity[0].getName()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/partners/entities/{id} - Not Found")
  void getByIdNotFound() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .get("/v1/partners/entities/{id}")
        .then()
        .statusCode(404)
        .body("success", is(false))
        .body("error.code", is("ENTITY_NOT_FOUND"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/partners/entities?cnpj= - Success")
  void getByCnpjSuccess() throws Exception {
    Entity[] entity = new Entity[1];
    doInTransaction(
        () -> {
          City city = factory.getAnyCity();
          entity[0] = factory.createEntity(city);
        });

    given()
        .queryParam("cnpj", entity[0].getCnpj().getValue())
        .when()
        .get("/v1/partners/entities")
        .then()
        .statusCode(200)
        .body("data.cnpj", is(entity[0].getCnpj().getValue()));
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("GET /v1/partners/entities - List All")
  void listAll() throws Exception {
    doInTransaction(
        () -> {
          City city = factory.getAnyCity();
          factory.createEntity(city);
        });

    given()
        .when()
        .get("/v1/partners/entities")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/partners/entities?cityId= - Filter by City")
  void listByCityId() throws Exception {
    City[] city = new City[1];
    doInTransaction(
        () -> {
          city[0] = factory.getAnyCity();
          factory.createEntity(city[0]);
        });

    given()
        .queryParam("cityId", city[0].getId().toString())
        .when()
        .get("/v1/partners/entities")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/partners/entities/cities - List Cities")
  void listCities() throws Exception {
    doInTransaction(
        () -> {
          City city = factory.getAnyCity();
          factory.createEntity(city);
        });

    given()
        .when()
        .get("/v1/partners/entities/cities")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/partners/entities - Success")
  void createSuccess() throws Exception {
    City[] city = new City[1];
    doInTransaction(() -> city[0] = factory.getAnyCity());

    var req = anEntityCreateRequest().withCityId(city[0].getId()).build();

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/v1/partners/entities")
        .then()
        .statusCode(201)
        .body("data.name", is(req.name()))
        .body("data.cnpj", is(req.cnpjString()))
        .body("data.cnpjFormatted", notNullValue());
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/partners/entities - Duplicate CNPJ")
  void createDuplicate() throws Exception {
    Entity[] existing = new Entity[1];
    City[] city = new City[1];
    doInTransaction(
        () -> {
          city[0] = factory.getAnyCity();
          existing[0] = factory.createEntity(city[0]);
        });

    var req =
        anEntityCreateRequest()
            .withCnpj(existing[0].getCnpj().getValue())
            .withCityId(city[0].getId())
            .build();

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/v1/partners/entities")
        .then()
        .statusCode(409);
  }

  @Test
  @TestSecurity(
      user = "staff",
      roles = {"STAFF"})
  @DisplayName("PUT /v1/partners/entities/{id} - Success")
  void updateSuccess() throws Exception {
    Entity[] entity = new Entity[1];
    doInTransaction(
        () -> {
          City city = factory.getAnyCity();
          entity[0] = factory.createEntity(city);
        });

    var req = anEntityUpdateRequest().withName("Updated Name").build();

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", entity[0].getId())
        .body(req)
        .when()
        .put("/v1/partners/entities/{id}")
        .then()
        .statusCode(200)
        .body("data.name", is("Updated Name"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PUT /v1/partners/entities/{id} - Not Found")
  void updateNotFound() {
    var req = anEntityUpdateRequest().build();

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .body(req)
        .when()
        .put("/v1/partners/entities/{id}")
        .then()
        .statusCode(404);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("DELETE /v1/partners/entities/{id} - Success")
  void deleteSuccess() throws Exception {
    Entity[] entity = new Entity[1];
    doInTransaction(
        () -> {
          City city = factory.getAnyCity();
          entity[0] = factory.createEntity(city);
        });

    given()
        .pathParam("id", entity[0].getId())
        .when()
        .delete("/v1/partners/entities/{id}")
        .then()
        .statusCode(204);
  }

  @Test
  @DisplayName("Should return 401 when accessing without authentication")
  void unauthorizedAccess() {
    assertUnauthenticated("/v1/partners/entities");
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("POST /v1/partners/entities - Forbidden for STUDENT")
  void createForbiddenForStudent() throws Exception {
    City[] city = new City[1];
    doInTransaction(() -> city[0] = factory.getAnyCity());

    var req = anEntityCreateRequest().withCityId(city[0].getId()).build();

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/v1/partners/entities")
        .then()
        .statusCode(403);
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("DELETE /v1/partners/entities/{id} - Forbidden for STUDENT")
  void deleteForbiddenForStudent() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .delete("/v1/partners/entities/{id}")
        .then()
        .statusCode(403);
  }

  @Test
  @TestSecurity(
      user = "staff",
      roles = {"STAFF"})
  @DisplayName("DELETE /v1/partners/entities/{id} - Forbidden for STAFF")
  void deleteForbiddenForStaff() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .delete("/v1/partners/entities/{id}")
        .then()
        .statusCode(403);
  }
}


