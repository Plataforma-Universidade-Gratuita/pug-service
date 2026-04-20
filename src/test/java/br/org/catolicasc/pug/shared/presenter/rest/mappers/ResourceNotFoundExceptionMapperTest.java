package br.org.catolicasc.pug.shared.presenter.rest.mappers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import br.org.catolicasc.pug.identity.domain.enums.IdentityErrorCodes;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ResourceNotFoundExceptionMapperTest {

  @Path("/test-not-found")
  public static class TriggerResource {
    @GET
    public void trigger() {
      throw new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND);
    }
  }

  @Test
  @DisplayName("Should translate ResourceNotFoundException to HTTP 404 Not Found")
  void testMapping() {
    given()
        .when()
        .get("/test-not-found")
        .then()
        .statusCode(404)
        .body("success", is(false))
        .body("error.code", is("USER_NOT_FOUND"));
  }
}
