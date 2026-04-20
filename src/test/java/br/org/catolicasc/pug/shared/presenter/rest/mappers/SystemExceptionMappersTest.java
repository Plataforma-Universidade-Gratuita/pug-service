package br.org.catolicasc.pug.shared.presenter.rest.mappers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class SystemExceptionMappersTest {

  @Path("/test-system-errors")
  public static class TriggerResource {
    @GET
    @Path("/uncaught")
    public void triggerUncaught() {
      throw new RuntimeException("Unexpected boom!");
    }

    @GET
    @Path("/persistence")
    public void triggerPersistence() {
      throw new PersistenceException(new ConstraintViolationException("msg", null, "uq_users_cpf"));
    }
  }

  @Test
  @DisplayName("UncaughtExceptionMapper: Should map to 500")
  void testUncaught() {
    given()
        .when()
        .get("/test-system-errors/uncaught")
        .then()
        .statusCode(500)
        .body("error.code", is("INTERNAL_ERROR"));
  }

  @Test
  @DisplayName("PersistenceExceptionMapper: Should map unique constraint to 409")
  void testPersistence() {
    given()
        .when()
        .get("/test-system-errors/persistence")
        .then()
        .statusCode(409)
        .body("error.code", is("DUPLICATED_RESOURCE_ERROR"));
  }
}
