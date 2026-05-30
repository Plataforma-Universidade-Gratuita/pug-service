package br.org.catolicasc.pug.project.presenter;

import static br.org.catolicasc.pug.helpers.builders.requests.ProjectCreateRequestBuilder.aProjectCreateRequest;
import static br.org.catolicasc.pug.helpers.builders.requests.ProjectUpdateRequestBuilder.aProjectUpdateRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.helpers.BaseResourceTest;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("ProjectsResource Integration Tests")
class ProjectResourceTest extends BaseResourceTest {

  @InjectMock AuthService authService;

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  void getByIdSuccess() throws Exception {
    Project[] project = new Project[1];
    doInTransaction(
        () -> {
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
        });

    given()
        .pathParam("id", project[0].getId())
        .when()
        .get("/v1/projects/{id}")
        .then()
        .statusCode(200)
        .body("data.id", is(project[0].getId().toString()))
        .body("data.entity.id", is(project[0].getEntityId().toString()))
        .body(
            "data.projectInfo.createdBy",
            is(project[0].getProjectInfo().getCreatedBy().toString()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  void listAll() throws Exception {
    doInTransaction(
        () -> {
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          factory.createProject(entity, creator);
        });

    given()
        .when()
        .get("/v1/projects")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  void listAllByIds() throws Exception {
    Project[] project = new Project[1];
    doInTransaction(
        () -> {
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
        });

    given()
        .queryParam("ids", project[0].getId())
        .when()
        .get("/v1/projects")
        .then()
        .statusCode(200)
        .body("data", hasSize(1));
  }

  @Test
  @TestSecurity(
      user = "staff",
      roles = {"STAFF"})
  void createSuccess() throws Exception {
    Account[] staffAccount = new Account[1];
    Entity[] entity = new Entity[1];
    doInTransaction(
        () -> {
          entity[0] = factory.createEntity(factory.getAnyCity());
          staffAccount[0] = factory.createAccount(factory.createUser(), AccountType.PARTNER);
        });

    when(authService.getCurrentAccountId()).thenReturn(staffAccount[0].getId());

    given()
        .contentType(ContentType.JSON)
        .body(aProjectCreateRequest().withEntityId(entity[0].getId()).build())
        .when()
        .post("/v1/projects")
        .then()
        .statusCode(201)
        .body("data.entity.id", is(entity[0].getId().toString()))
        .body("data.status.status", is("PLANNED"));
  }

  @Test
  @TestSecurity(
      user = "staff",
      roles = {"STAFF"})
  void updateSuccess() throws Exception {
    Project[] project = new Project[1];
    doInTransaction(
        () -> {
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
        });

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", project[0].getId())
        .body(aProjectUpdateRequest().withDescription("Updated Description").build())
        .when()
        .put("/v1/projects/{id}")
        .then()
        .statusCode(200)
        .body("data.description", is("Updated Description"));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  void updateStatusSuccess() throws Exception {
    Project[] project = new Project[1];
    doInTransaction(
        () -> {
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
        });

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", project[0].getId())
        .body("\"IN_PROGRESS\"")
        .when()
        .patch("/v1/projects/{id}/status")
        .then()
        .statusCode(200)
        .body("data.status.status", is("IN_PROGRESS"));
  }

  @Test
  @TestSecurity(
      user = "staff",
      roles = {"STAFF"})
  void listByCreator() throws Exception {
    Account[] creator = new Account[1];
    doInTransaction(
        () -> {
          Entity entity = factory.createEntity(factory.getAnyCity());
          creator[0] = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          factory.createProject(entity, creator[0]);
        });

    given()
        .pathParam("createdById", creator[0].getId())
        .when()
        .get("/v1/projects/creators/{createdById}")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  void searchSuccess() throws Exception {
    Entity[] entity = new Entity[1];
    doInTransaction(
        () -> {
          entity[0] = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          factory.createProject(entity[0], creator);
        });

    given()
        .contentType(ContentType.JSON)
        .queryParam("page", 0)
        .queryParam("size", 1)
        .body(
            """
            {
              "entityIds": ["%s"]
            }
            """
                .formatted(entity[0].getId()))
        .when()
        .post("/v1/projects/search")
        .then()
        .statusCode(200)
        .body("data.content", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  void unauthorizedAccess() {
    assertUnauthenticated("/v1/projects");
  }
}
