package br.org.catolicasc.pug.academic.presenter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.presenter.dtos.CourseCreateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.CourseUpdateRequest;
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
@DisplayName("CourseResource Integration Tests")
class CourseResourceTest {

  @Inject TestDataFactory factory;
  @Inject UserTransaction utx;
  @Inject EntityManager em;

  @InjectMock AuditPublisher audit;

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /academic/courses/{id} - Success")
  void getByIdSuccess() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    em.flush();
    utx.commit();

    given()
        .pathParam("id", course.getId())
        .when()
        .get("/academic/courses/{id}")
        .then()
        .statusCode(200)
        .body("success", is(true))
        .body("data.id", is(course.getId().toString()))
        .body("data.name", is(course.getName()));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("GET /academic/courses/{id} - Not Found")
  void getByIdNotFound() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .get("/academic/courses/{id}")
        .then()
        .statusCode(404);
  }

  @Test
  @TestSecurity(
      user = "user",
      roles = {"STUDENT"})
  @DisplayName("GET /academic/courses - List All")
  void listAll() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    factory.createCourse(school);
    utx.commit();

    given()
        .when()
        .get("/academic/courses")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "user",
      roles = {"STUDENT"})
  @DisplayName("GET /academic/courses?schoolId= - Filter by School")
  void listBySchoolId() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    factory.createCourse(school);
    utx.commit();

    given()
        .queryParam("schoolId", school.getId().toString())
        .when()
        .get("/academic/courses")
        .then()
        .statusCode(200)
        .body("data", hasSize(greaterThanOrEqualTo(1)));
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /academic/courses - Success")
  void createSuccess() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    em.flush();
    utx.commit();

    CourseCreateRequest req =
        new CourseCreateRequest("New Course " + UUID.randomUUID(), school.getId());

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/academic/courses")
        .then()
        .statusCode(201)
        .body("data.name", notNullValue())
        .body("data.school", notNullValue());
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("POST /academic/courses - Duplicate Name")
  void createDuplicate() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course existing = factory.createCourse(school);
    em.flush();
    utx.commit();

    CourseCreateRequest req = new CourseCreateRequest(existing.getName(), school.getId());

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/academic/courses")
        .then()
        .statusCode(409);
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("PUT /academic/courses/{id} - Success")
  void updateSuccess() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    em.flush();
    utx.commit();

    CourseUpdateRequest req = new CourseUpdateRequest("Updated " + UUID.randomUUID(), null);

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", course.getId())
        .body(req)
        .when()
        .put("/academic/courses/{id}")
        .then()
        .statusCode(200)
        .body("data.name", notNullValue());
  }

  @Test
  @TestSecurity(
      user = "admin",
      roles = {"ADMIN"})
  @DisplayName("DELETE /academic/courses/{id} - Success")
  void deleteSuccess() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    em.flush();
    utx.commit();

    given()
        .pathParam("id", course.getId())
        .when()
        .delete("/academic/courses/{id}")
        .then()
        .statusCode(200);
  }

  @Test
  @DisplayName("Should return 401 when unauthenticated")
  void unauthorizedAccess() {
    given().when().get("/academic/courses").then().statusCode(401);
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("POST /academic/courses - Forbidden for STUDENT")
  void createForbiddenForStudent() throws Exception {
    utx.begin();
    School school = factory.createSchool();
    utx.commit();

    CourseCreateRequest req = new CourseCreateRequest("Forbidden", school.getId());

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/academic/courses")
        .then()
        .statusCode(403);
  }

  @Test
  @TestSecurity(
      user = "student",
      roles = {"STUDENT"})
  @DisplayName("DELETE /academic/courses/{id} - Forbidden for STUDENT")
  void deleteForbiddenForStudent() {
    given()
        .pathParam("id", UuidCreator.getTimeOrderedEpoch())
        .when()
        .delete("/academic/courses/{id}")
        .then()
        .statusCode(403);
  }
}
