package br.org.catolicasc.pug.geo.presenter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import br.org.catolicasc.pug.geo.infra.persistence.CityEntity;
import br.org.catolicasc.pug.helpers.BaseResourceTest;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("CitiesReadOnlyResource Integration Tests")
class CitiesReadOnlyResourceTest extends BaseResourceTest {

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/geo/cities/{id} - Success")
  void getByIdSuccess() {
    var city =
        em.createQuery("from CityEntity", CityEntity.class).setMaxResults(1).getSingleResult();

    given()
        .pathParam("id", city.getId())
        .when()
        .get("/v1/geo/cities/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.id", is(city.getId().toString()))
        .body("data.name", is(city.getName()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/geo/cities/{id} - Not Found")
  void getByIdNotFound() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .get("/v1/geo/cities/{id}")
        .then()
        .statusCode(404)
        .body("success", is(false))
        .body("error.code", is("CITY_NOT_FOUND"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/geo/cities - List All")
  void listCities() {
    given()
        .when()
        .get("/v1/geo/cities")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThan(200)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/geo/cities/search - Paginated Search")
  void searchCities() {
    given()
        .queryParam("page", 0)
        .queryParam("size", 10)
        .contentType("application/json")
        .body("{\"name\":\"Joinville\"}")
        .when()
        .post("/v1/geo/cities/search")
        .then()
        .statusCode(200)
        .body("data.content", hasSize(greaterThan(0)))
        .body("data.page", is(0))
        .body("data.size", is(10))
        .body("data.totalElements", greaterThan(0));
  }

  @Test
  @DisplayName("Should return 401 when accessing without security")
  void unauthorizedAccess() {
    assertUnauthenticated("/v1/geo/cities");
  }
}
