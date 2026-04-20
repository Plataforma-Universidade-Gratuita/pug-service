package br.org.catolicasc.pug.shared.presenter.rest.mappers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class NotAuthorizedExceptionMapperTest {

  @Path("/test-unauthorized")
  public static class TriggerResource {
    @GET
    public void trigger() {
      throw new NotAuthorizedException("Security check failed");
    }
  }

  @Test
  @DisplayName("Should translate NotAuthorizedException to HTTP 401 Unauthorized")
  void testMapping() {
    given()
        .when()
        .get("/test-unauthorized")
        .then()
        .statusCode(401)
        .body("success", is(false))
        .body("error.code", is("UNAUTHORIZED_ERROR"))
        .body("error.message", not(containsString("Security check failed")));
  }
}
