package br.org.catolicasc.pug.project.presenter;

import static br.org.catolicasc.pug.helpers.builders.requests.ProjectAreaOfExpertiseRequestBuilder.aProjectAreaOfExpertiseRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.helpers.BaseResourceTest;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("ProjectsAreaOfExpertisesResource Integration Tests")
class ProjectAreaOfExpertiseResourceTest extends BaseResourceTest {

  private record Graph(Project project, AreaOfExpertise areaOfExpertise) {}

  private Graph createGraph() throws Exception {
    Project[] project = new Project[1];
    AreaOfExpertise[] areaOfExpertise = new AreaOfExpertise[1];
    doInTransaction(
        () -> {
          Entity entity = factory.createEntity(factory.getAnyCity());
          Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
          project[0] = factory.createProject(entity, creator);
          areaOfExpertise[0] = factory.createAreaOfExpertise();
        });
    return new Graph(project[0], areaOfExpertise[0]);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  void createAssociations() throws Exception {
    Graph g = createGraph();

    given()
        .contentType(ContentType.JSON)
        .pathParam("projectId", g.project().getId())
        .body(
            aProjectAreaOfExpertiseRequest()
                .withAreaOfExpertiseIds(List.of(g.areaOfExpertise().getId()))
                .build())
        .when()
        .post("/v1/projects/{projectId}/areas-of-expertise")
        .then()
        .statusCode(204);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  void listAreasOfExpertiseByProjectId() throws Exception {
    Graph g = createGraph();
    createAssociation(g);

    given()
        .pathParam("projectId", g.project().getId())
        .when()
        .get("/v1/projects/{projectId}/areas-of-expertise")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  void listProjectsByAreaOfExpertiseId() throws Exception {
    Graph g = createGraph();
    createAssociation(g);

    given()
        .pathParam("areaOfExpertiseId", g.areaOfExpertise().getId())
        .when()
        .get("/v1/academic/areas-of-expertise/{areaOfExpertiseId}/projects")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  void deleteAssociation() throws Exception {
    Graph g = createGraph();
    createAssociation(g);

    given()
        .pathParam("projectId", g.project().getId())
        .pathParam("areaOfExpertiseId", g.areaOfExpertise().getId())
        .when()
        .delete("/v1/projects/{projectId}/areas-of-expertise/{areaOfExpertiseId}")
        .then()
        .statusCode(204);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  void deleteAllByProject() throws Exception {
    Graph g = createGraph();
    createAssociation(g);

    given()
        .pathParam("projectId", g.project().getId())
        .when()
        .delete("/v1/projects/{projectId}/areas-of-expertise")
        .then()
        .statusCode(204);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  void deleteAllByAreaOfExpertise() throws Exception {
    Graph g = createGraph();
    createAssociation(g);

    given()
        .pathParam("areaOfExpertiseId", g.areaOfExpertise().getId())
        .when()
        .delete("/v1/academic/areas-of-expertise/{areaOfExpertiseId}/projects")
        .then()
        .statusCode(204);
  }

  @Test
  void unauthorizedAccess() {
    given()
        .pathParam("projectId", UuidCreator.getTimeOrderedEpoch())
        .when()
        .get("/v1/projects/{projectId}/areas-of-expertise")
        .then()
        .statusCode(401);
  }

  private void createAssociation(Graph g) {
    given()
        .contentType(ContentType.JSON)
        .pathParam("projectId", g.project().getId())
        .body(
            aProjectAreaOfExpertiseRequest()
                .withAreaOfExpertiseIds(List.of(g.areaOfExpertise().getId()))
                .build())
        .post("/v1/projects/{projectId}/areas-of-expertise");
  }
}
