package br.org.catolicasc.pug.partner.presenter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import br.org.catolicasc.pug.geo.domain.City;
import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.partner.presenter.dtos.EntityCreateRequest;
import br.org.catolicasc.pug.partner.presenter.dtos.EntityUpdateRequest;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("EntityResource Integration Tests")
class EntityResourceTest {

  @Inject TestDataFactory factory;
  @Inject UserTransaction utx;
  @Inject EntityManager em;

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /partner/entities/{id} - Success")
  void getByIdSuccess() throws Exception {
    utx.begin();
    City city = factory.getAnyCity();
    Entity entity = factory.createEntity(city);
    em.flush();
    utx.commit();

    given()
        .pathParam("id", entity.getId())
        .when()
        .get("/partner/entities/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.id", is(entity.getId().toString()))
        .body("data.cnpjFormatted", notNullValue())
        .body("data.name", is(entity.getName()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /partner/entities/{id} - Not Found")
  void getByIdNotFound() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .get("/partner/entities/{id}")
        .then()
        .statusCode(404)
        .body("success", is(false))
        .body("error.code", is("ENTITY_NOT_FOUND"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /partner/entities/by-cnpj/{cnpj} - Success")
  void getByCnpjSuccess() throws Exception {
    utx.begin();
    City city = factory.getAnyCity();
    Entity entity = factory.createEntity(city);
    em.flush();
    utx.commit();

    given()
        .pathParam("cnpj", entity.getCnpj().getValue())
        .when()
        .get("/partner/entities/by-cnpj/{cnpj}")
        .then()
        .statusCode(200)
        .body("data.cnpj", is(entity.getCnpj().getValue()));
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("GET /partner/entities - List All")
  void listAll() throws Exception {
    utx.begin();
    City city = factory.getAnyCity();
    factory.createEntity(city);
    utx.commit();

    given()
        .when()
        .get("/partner/entities")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /partner/entities?cityId= - Filter by City")
  void listByCityId() throws Exception {
    utx.begin();
    City city = factory.getAnyCity();
    factory.createEntity(city);
    utx.commit();

    given()
        .queryParam("cityId", city.getId().toString())
        .when()
        .get("/partner/entities")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /partner/entities/cities - List Cities")
  void listCities() throws Exception {
    utx.begin();
    City city = factory.getAnyCity();
    factory.createEntity(city);
    utx.commit();

    given()
        .when()
        .get("/partner/entities/cities")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /partner/entities - Success")
  void createSuccess() throws Exception {
    utx.begin();
    City city = factory.getAnyCity();
    utx.commit();

    String cnpj = TestBrazilianIdentifierGenerator.generateValidCnpj();
    EntityCreateRequest req =
        new EntityCreateRequest(cnpj, "New Entity Corp", city.getId(), "Rua Test, 123");

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/partner/entities")
        .then()
        .statusCode(201)
        .body("data.name", is("New Entity Corp"))
        .body("data.cnpj", is(cnpj))
        .body("data.cnpjFormatted", notNullValue());
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /partner/entities - Duplicate CNPJ")
  void createDuplicate() throws Exception {
    utx.begin();
    City city = factory.getAnyCity();
    Entity existing = factory.createEntity(city);
    em.flush();
    utx.commit();

    EntityCreateRequest req =
        new EntityCreateRequest(
            existing.getCnpj().getValue(), "Duplicate Corp", city.getId(), "Rua Dup, 1");

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/partner/entities")
        .then()
        .statusCode(409);
  }

  @Test
  @TestSecurity(
      user = "staff",
      roles = {"STAFF"})
  @DisplayName("PUT /partner/entities/{id} - Success")
  void updateSuccess() throws Exception {
    utx.begin();
    City city = factory.getAnyCity();
    Entity entity = factory.createEntity(city);
    em.flush();
    utx.commit();

    EntityUpdateRequest req = new EntityUpdateRequest("Updated Name", null, null);

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", entity.getId())
        .body(req)
        .when()
        .put("/partner/entities/{id}")
        .then()
        .statusCode(200)
        .body("data.name", is("Updated Name"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PUT /partner/entities/{id} - Not Found")
  void updateNotFound() {
    EntityUpdateRequest req = new EntityUpdateRequest("Name", null, null);

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .body(req)
        .when()
        .put("/partner/entities/{id}")
        .then()
        .statusCode(404);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("DELETE /partner/entities/{id} - Success")
  void deleteSuccess() throws Exception {
    utx.begin();
    City city = factory.getAnyCity();
    Entity entity = factory.createEntity(city);
    em.flush();
    utx.commit();

    given()
        .pathParam("id", entity.getId())
        .when()
        .delete("/partner/entities/{id}")
        .then()
        .statusCode(200);
  }

  @Test
  @DisplayName("Should return 401 when accessing without authentication")
  void unauthorizedAccess() {
    given().when().get("/partner/entities").then().statusCode(401);
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("POST /partner/entities - Forbidden for STUDENT")
  void createForbiddenForStudent() throws Exception {
    utx.begin();
    City city = factory.getAnyCity();
    utx.commit();

    EntityCreateRequest req =
        new EntityCreateRequest(
            TestBrazilianIdentifierGenerator.generateValidCnpj(),
            "Forbidden Corp",
            city.getId(),
            "Rua X");

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/partner/entities")
        .then()
        .statusCode(403);
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("DELETE /partner/entities/{id} - Forbidden for STUDENT")
  void deleteForbiddenForStudent() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .delete("/partner/entities/{id}")
        .then()
        .statusCode(403);
  }

  @Test
  @TestSecurity(
      user = "staff",
      roles = {"STAFF"})
  @DisplayName("DELETE /partner/entities/{id} - Forbidden for STAFF")
  void deleteForbiddenForStaff() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .delete("/partner/entities/{id}")
        .then()
        .statusCode(403);
  }
}
