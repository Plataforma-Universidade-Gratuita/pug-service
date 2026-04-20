package br.org.catolicasc.pug.shared.presenter.rest.mappers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import br.org.catolicasc.pug.identity.domain.enums.IdentityErrorCodes;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class DuplicateResourceExceptionMapperTest {

  @Path("/test-duplicate")
  public static class TriggerResource {
    @GET
    public void trigger() {
      throw new DuplicateResourceException(IdentityErrorCodes.USER_ALREADY_EXISTS);
    }
  }

  @Test
  @DisplayName("Should translate DuplicateResourceException to HTTP 409 Conflict")
  void testMapping() {
    given()
        .when()
        .get("/test-duplicate")
        .then()
        .statusCode(409)
        .body("success", is(false))
        .body("error.code", is("USER_ALREADY_EXISTS"));
  }
}
