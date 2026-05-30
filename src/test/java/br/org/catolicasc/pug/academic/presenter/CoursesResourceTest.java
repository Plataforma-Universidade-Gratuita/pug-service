package br.org.catolicasc.pug.academic.presenter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.presenter.dtos.courses.CourseComplexSearchRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.courses.CourseCreateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.courses.CourseUpdateRequest;
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
@DisplayName("CoursesResource Integration Tests")
class CoursesResourceTest extends BaseResourceTest {

  @InjectMock AuditPublisher audit;

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/academic/courses/{id} - Success")
  void getByIdSuccess() throws Exception {
    School[] school = new School[1];
    Course[] course = new Course[1];
    doInTransaction(
        () -> {
          school[0] = factory.createSchool();
          course[0] = factory.createCourse(school[0]);
        });

    given()
        .pathParam("id", course[0].getId())
        .when()
        .get("/v1/academic/courses/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.id", is(course[0].getId().toString()))
        .body("data.name", is(course[0].getName()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /v1/academic/courses/{id} - Not Found")
  void getByIdNotFound() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .get("/v1/academic/courses/{id}")
        .then()
        .statusCode(404);
  }

  @Test
  @TestSecurity(
      user = "formerStudent",
      roles = {"FORMER_STUDENT"})
  @DisplayName("GET /v1/academic/courses - List All")
  void listAll() throws Exception {
    doInTransaction(
        () -> {
          School school = factory.createSchool();
          factory.createCourse(school);
        });

    given()
        .when()
        .get("/v1/academic/courses")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "formerStudent",
      roles = {"FORMER_STUDENT"})
  @DisplayName("GET /v1/academic/courses?ids= - Filter by IDs")
  void listByIds() throws Exception {
    Course[] course = new Course[1];
    doInTransaction(
        () -> {
          School school = factory.createSchool();
          course[0] = factory.createCourse(school);
        });

    given()
        .queryParam("ids", course[0].getId().toString())
        .when()
        .get("/v1/academic/courses")
        .then()
        .statusCode(200)
        .body("data", hasSize(1))
        .body("data[0].id", is(course[0].getId().toString()));
  }

  @Test
  @TestSecurity(
      user = "formerStudent",
      roles = {"FORMER_STUDENT"})
  @DisplayName("POST /v1/academic/courses/search - Success")
  void searchSuccess() throws Exception {
    School[] school = new School[1];
    Course[] course = new Course[1];
    doInTransaction(
        () -> {
          school[0] = factory.createSchool();
          course[0] = factory.createCourse(school[0]);
        });

    CourseComplexSearchRequest request =
        new CourseComplexSearchRequest(course[0].getName().substring(0, 3), null);

    given()
        .contentType(ContentType.JSON)
        .queryParam("page", 0)
        .queryParam("size", 10)
        .body(request)
        .when()
        .post("/v1/academic/courses/search")
        .then()
        .statusCode(200)
        .body("data.content", hasSize(greaterThanOrEqualTo(1)))
        .body("data.content[0].areaOfExpertise", notNullValue())
        .body("data.content[0].auditInfo", notNullValue());
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/academic/courses - Success")
  void createSuccess() throws Exception {
    School[] school = new School[1];
    doInTransaction(() -> school[0] = factory.createSchool());

    CourseCreateRequest req =
        new CourseCreateRequest(
            "New Course " + UuidCreator.getTimeOrderedEpoch(), school[0].getId());

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/v1/academic/courses")
        .then()
        .statusCode(201)
        .body("data.name", notNullValue())
        .body("data.areaOfExpertise", notNullValue());
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /v1/academic/courses - Duplicate Name")
  void createDuplicate() throws Exception {
    School[] school = new School[1];
    Course[] existing = new Course[1];
    doInTransaction(
        () -> {
          school[0] = factory.createSchool();
          existing[0] = factory.createCourse(school[0]);
        });

    CourseCreateRequest req = new CourseCreateRequest(existing[0].getName(), school[0].getId());

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/v1/academic/courses")
        .then()
        .statusCode(409);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PUT /v1/academic/courses/{id} - Success")
  void updateSuccess() throws Exception {
    Course[] course = new Course[1];
    doInTransaction(
        () -> {
          School school = factory.createSchool();
          course[0] = factory.createCourse(school);
        });

    CourseUpdateRequest req =
        new CourseUpdateRequest("Updated " + UuidCreator.getTimeOrderedEpoch(), null);

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", course[0].getId())
        .body(req)
        .when()
        .put("/v1/academic/courses/{id}")
        .then()
        .statusCode(200)
        .body("data.name", notNullValue());
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("DELETE /v1/academic/courses/{id} - Success")
  void deleteSuccess() throws Exception {
    Course[] course = new Course[1];
    doInTransaction(
        () -> {
          School school = factory.createSchool();
          course[0] = factory.createCourse(school);
        });

    given()
        .pathParam("id", course[0].getId())
        .when()
        .delete("/v1/academic/courses/{id}")
        .then()
        .statusCode(204);
  }

  @Test
  @DisplayName("Should return 401 when unauthenticated")
  void unauthorizedAccess() {
    assertUnauthenticated("/v1/academic/courses");
  }

  @Test
  @TestSecurity(
      user = "formerStudent",
      roles = {"FORMER_STUDENT"})
  @DisplayName("POST /v1/academic/courses - Forbidden for FORMER_STUDENT")
  void createForbiddenForFormerStudent() throws Exception {
    School[] school = new School[1];
    doInTransaction(() -> school[0] = factory.createSchool());

    CourseCreateRequest req = new CourseCreateRequest("Forbidden", school[0].getId());

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/v1/academic/courses")
        .then()
        .statusCode(403);
  }

  @Test
  @TestSecurity(
      user = "formerStudent",
      roles = {"FORMER_STUDENT"})
  @DisplayName("DELETE /v1/academic/courses/{id} - Forbidden for FORMER_STUDENT")
  void deleteForbiddenForFormerStudent() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .delete("/v1/academic/courses/{id}")
        .then()
        .statusCode(403);
  }
}
