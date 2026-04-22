package br.org.catolicasc.pug.partner.presenter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.geo.domain.City;
import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.partner.presenter.dtos.StaffCreateRequest;
import br.org.catolicasc.pug.partner.presenter.dtos.StaffUpdateRequest;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
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
@DisplayName("StaffResource Integration Tests")
class StaffResourceTest {

  @Inject TestDataFactory factory;
  @Inject UserTransaction utx;
  @Inject EntityManager em;

  @InjectMock AuthService authService;

  private StaffGraph createStaffGraph() throws Exception {
    utx.begin();
    City city = factory.getAnyCity();
    Entity entity = factory.createEntity(city);
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.PARTNER);
    Staff staff = factory.createStaff(account, entity);
    em.flush();
    utx.commit();
    return new StaffGraph(city, entity, user, account, staff);
  }

  private record StaffGraph(City city, Entity entity, User user, Account account, Staff staff) {}

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /partners/staff/{id} - Success")
  void getByIdSuccess() throws Exception {
    StaffGraph g = createStaffGraph();

    given()
        .pathParam("id", g.account().getId())
        .when()
        .get("/partners/staff/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.account.id", is(g.account().getId().toString()))
        .body("data.entityId", is(g.entity().getId().toString()))
        .body("data.cityId", notNullValue());
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /partners/staff/{id} - Not Found")
  void getByIdNotFound() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .get("/partners/staff/{id}")
        .then()
        .statusCode(404)
        .body("success", is(false))
        .body("error.code", is("STAFF_NOT_FOUND"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /partners/staff/by-email/{email} - Success")
  void getByEmailSuccess() throws Exception {
    StaffGraph g = createStaffGraph();

    given()
        .pathParam("email", g.account().getEmail().getValue())
        .when()
        .get("/partners/staff/by-email/{email}")
        .then()
        .statusCode(200)
        .body("data.account.email", is(g.account().getEmail().getValue()));
  }

  @Test
  @TestSecurity(
      user = "staff",
      roles = {"STAFF"})
  @DisplayName("GET /partners/staff/me - Success")
  void getMeSuccess() throws Exception {
    StaffGraph g = createStaffGraph();
    when(authService.getCurrentAccountId()).thenReturn(g.account().getId());

    given()
        .when()
        .get("/partners/staff/me")
        .then()
        .statusCode(200)
        .body("data.account.id", is(g.account().getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("GET /partners/staff - List All")
  void listAll() throws Exception {
    createStaffGraph();

    given()
        .when()
        .get("/partners/staff")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /partners/staff/by-cpf/{cpf} - Success")
  void listByCpfSuccess() throws Exception {
    StaffGraph g = createStaffGraph();

    given()
        .pathParam("cpf", g.user().getCpf().getValue())
        .when()
        .get("/partners/staff/by-cpf/{cpf}")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)))
        .body("data[0].account.userId", is(g.user().getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("GET /partners/staff/by-entity/{entityId} - Success")
  void listByEntitySuccess() throws Exception {
    StaffGraph g = createStaffGraph();

    given()
        .pathParam("entityId", g.entity().getId())
        .when()
        .get("/partners/staff/by-entity/{entityId}")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /partners/staff - Success")
  void createSuccess() throws Exception {
    utx.begin();
    City city = factory.getAnyCity();
    Entity entity = factory.createEntity(city);
    em.flush();
    utx.commit();

    StaffCreateRequest req =
        new StaffCreateRequest(
            TestBrazilianIdentifierGenerator.generateValidCpf(),
            "New Staff Member",
            "newstaff@pug.com",
            "password123",
            entity.getId());

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/partners/staff")
        .then()
        .statusCode(201)
        .body("data.account.email", is("newstaff@pug.com"))
        .body("data.entityId", is(entity.getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PUT /partners/staff/{id} - Success")
  void updateSuccess() throws Exception {
    StaffGraph g = createStaffGraph();

    StaffUpdateRequest req = new StaffUpdateRequest("Updated Staff Name", null, null);

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", g.account().getId())
        .body(req)
        .when()
        .put("/partners/staff/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.account.id", is(g.account().getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PUT /partners/staff/{id} - Not Found")
  void updateNotFound() {
    StaffUpdateRequest req = new StaffUpdateRequest("Name", null, null);

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .body(req)
        .when()
        .put("/partners/staff/{id}")
        .then()
        .statusCode(404);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PATCH /partners/staff/{id}/deactivate - Success")
  void deactivateSuccess() throws Exception {
    StaffGraph g = createStaffGraph();

    given()
        .pathParam("id", g.account().getId())
        .when()
        .patch("/partners/staff/{id}/deactivate")
        .then()
        .statusCode(200);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("DELETE /partners/staff/{id} - Success")
  void deleteSuccess() throws Exception {
    StaffGraph g = createStaffGraph();

    given()
        .pathParam("id", g.account().getId())
        .when()
        .delete("/partners/staff/{id}")
        .then()
        .statusCode(200);
  }

  @Test
  @DisplayName("Should return 401 when accessing without authentication")
  void unauthorizedAccess() {
    given().when().get("/partners/staff").then().statusCode(401);
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("POST /partners/staff - Forbidden for STUDENT")
  void createForbiddenForStudent() {
    StaffCreateRequest req =
        new StaffCreateRequest(
            TestBrazilianIdentifierGenerator.generateValidCpf(),
            "Forbidden",
            "x@pug.com",
            "pass1234",
            UUID.randomUUID());

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/partners/staff")
        .then()
        .statusCode(403);
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("DELETE /partners/staff/{id} - Forbidden for STUDENT")
  void deleteForbiddenForStudent() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .delete("/partners/staff/{id}")
        .then()
        .statusCode(403);
  }

  @Test
  @TestSecurity(
      user = "staff",
      roles = {"STAFF"})
  @DisplayName("DELETE /partners/staff/{id} - Forbidden for STAFF")
  void deleteForbiddenForStaff() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .delete("/partners/staff/{id}")
        .then()
        .statusCode(403);
  }
}
