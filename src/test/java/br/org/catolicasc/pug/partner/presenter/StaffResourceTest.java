package br.org.catolicasc.pug.partner.presenter;

import static br.org.catolicasc.pug.helpers.builders.requests.StaffCreateRequestBuilder.aStaffCreateRequest;
import static br.org.catolicasc.pug.helpers.builders.requests.StaffUpdateRequestBuilder.aStaffUpdateRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.geo.domain.City;
import br.org.catolicasc.pug.helpers.BaseResourceTest;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("StaffResource Integration Tests")
class StaffResourceTest extends BaseResourceTest {

  @InjectMock AuthService authService;

  private record StaffGraph(City city, Entity entity, User user, Account account, Staff staff) {}

  private StaffGraph createStaffGraph() throws Exception {
    City[] city = new City[1];
    Entity[] entity = new Entity[1];
    User[] user = new User[1];
    Account[] account = new Account[1];
    Staff[] staff = new Staff[1];
    doInTransaction(
        () -> {
          city[0] = factory.getAnyCity();
          entity[0] = factory.createEntity(city[0]);
          user[0] = factory.createUser();
          account[0] = factory.createAccount(user[0], AccountType.PARTNER);
          staff[0] = factory.createStaff(account[0], entity[0]);
        });
    return new StaffGraph(city[0], entity[0], user[0], account[0], staff[0]);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/partners/staff/{id} - Success")
  void getByIdSuccess() throws Exception {
    StaffGraph graph = createStaffGraph();

    given()
        .pathParam("id", graph.account().getId())
        .when()
        .get("/v1/partners/staff/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.account.id", is(graph.account().getId().toString()))
        .body("data.entityId", is(graph.entity().getId().toString()))
        .body("data.cityId", notNullValue());
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/partners/staff/{id} - Not Found")
  void getByIdNotFound() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .get("/v1/partners/staff/{id}")
        .then()
        .statusCode(404)
        .body("success", is(false))
        .body("error.code", is("STAFF_NOT_FOUND"));
  }

  @Test
  @TestSecurity(
      user = "staff",
      roles = {"PARTNER"})
  @DisplayName("GET /v1/partners/staff/me - Success")
  void getMeSuccess() throws Exception {
    StaffGraph graph = createStaffGraph();
    when(authService.getCurrentAccountId()).thenReturn(graph.account().getId());

    given()
        .when()
        .get("/v1/partners/staff/me")
        .then()
        .statusCode(200)
        .body("data.account.id", is(graph.account().getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "formerStudent",
      roles = {"FORMER_STUDENT"})
  @DisplayName("GET /v1/partners/staff - List All")
  void listAll() throws Exception {
    createStaffGraph();

    given()
        .when()
        .get("/v1/partners/staff")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/partners/staff?ids= - Filter By Ids")
  void listByIdsSuccess() throws Exception {
    StaffGraph graph = createStaffGraph();

    given()
        .queryParam("ids", graph.account().getId())
        .when()
        .get("/v1/partners/staff")
        .then()
        .statusCode(200)
        .body("data", hasSize(1))
        .body("data[0].account.id", is(graph.account().getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/partners/staff/search - Paginated Search")
  void searchSuccess() throws Exception {
    StaffGraph graph = createStaffGraph();

    given()
        .queryParam("page", 0)
        .queryParam("size", 10)
        .contentType(ContentType.JSON)
        .body(
            """
            {
              "name": "%s",
              "cpf": "%s",
              "email": "%s",
              "entityIds": ["%s"]
            }
            """
                .formatted(
                    graph.user().getName().split(" ")[0],
                    graph.user().getCpf().getValue().substring(0, 3),
                    graph.account().getEmail().getValue().substring(0, 4),
                    graph.entity().getId()))
        .when()
        .post("/v1/partners/staff/search")
        .then()
        .statusCode(200)
        .body("data.content", hasSize(1))
        .body("data.content[0].account.id", is(graph.account().getId().toString()))
        .body("data.content[0].account.user.id", is(graph.user().getId().toString()))
        .body("data.content[0].entity.id", is(graph.entity().getId().toString()))
        .body("data.content[0].entity.name", is(graph.entity().getName()))
        .body("data.page", is(0))
        .body("data.size", is(10));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/partners/staff - Success")
  void createSuccess() throws Exception {
    Entity[] entity = new Entity[1];
    doInTransaction(
        () -> {
          City city = factory.getAnyCity();
          entity[0] = factory.createEntity(city);
        });

    var request = aStaffCreateRequest().withEntityId(entity[0].getId()).build();

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/v1/partners/staff")
        .then()
        .statusCode(201)
        .body("data.entityId", is(entity[0].getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PUT /v1/partners/staff/{id} - Success")
  void updateSuccess() throws Exception {
    StaffGraph graph = createStaffGraph();
    Entity[] targetEntity = new Entity[1];
    doInTransaction(() -> targetEntity[0] = factory.createEntity(factory.getAnyCity()));

    var request =
        aStaffUpdateRequest()
            .withName("Updated Staff Name")
            .withEntityId(targetEntity[0].getId())
            .build();

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", graph.account().getId())
        .body(request)
        .when()
        .put("/v1/partners/staff/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.account.id", is(graph.account().getId().toString()))
        .body("data.entityId", is(targetEntity[0].getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PUT /v1/partners/staff/{id} - Not Found")
  void updateNotFound() {
    var request = aStaffUpdateRequest().build();

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .body(request)
        .when()
        .put("/v1/partners/staff/{id}")
        .then()
        .statusCode(404);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PATCH /v1/partners/staff/{id}/status - Success")
  void updateStatusSuccess() throws Exception {
    StaffGraph graph = createStaffGraph();

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", graph.account().getId())
        .body("{\"active\": false}")
        .when()
        .patch("/v1/partners/staff/{id}/status")
        .then()
        .statusCode(204);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("DELETE /v1/partners/staff/{id} - Success")
  void deleteSuccess() throws Exception {
    StaffGraph graph = createStaffGraph();

    given()
        .pathParam("id", graph.account().getId())
        .when()
        .delete("/v1/partners/staff/{id}")
        .then()
        .statusCode(204);
  }

  @Test
  @DisplayName("Should return 401 when accessing without authentication")
  void unauthorizedAccess() {
    assertUnauthenticated("/v1/partners/staff");
  }

  @Test
  @TestSecurity(
      user = "formerStudent",
      roles = {"FORMER_STUDENT"})
  @DisplayName("POST /v1/partners/staff - Forbidden for FORMER_STUDENT")
  void createForbiddenForStudent() {
    var request = aStaffCreateRequest().build();

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/v1/partners/staff")
        .then()
        .statusCode(403);
  }

  @Test
  @TestSecurity(
      user = "formerStudent",
      roles = {"FORMER_STUDENT"})
  @DisplayName("DELETE /v1/partners/staff/{id} - Forbidden for FORMER_STUDENT")
  void deleteForbiddenForStudent() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .delete("/v1/partners/staff/{id}")
        .then()
        .statusCode(403);
  }

  @Test
  @TestSecurity(
      user = "staff",
      roles = {"PARTNER"})
  @DisplayName("DELETE /v1/partners/staff/{id} - Forbidden for PARTNER")
  void deleteForbiddenForStaff() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .delete("/v1/partners/staff/{id}")
        .then()
        .statusCode(403);
  }
}
