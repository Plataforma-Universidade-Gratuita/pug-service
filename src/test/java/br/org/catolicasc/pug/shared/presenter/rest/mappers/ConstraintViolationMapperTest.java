package br.org.catolicasc.pug.shared.presenter.rest.mappers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ConstraintViolationMapperTest {

  @Path("/test-bean-val")
  public static class TriggerResource {
    @POST
    public void trigger(@NotBlank String name) {
      /* triggers validation */
    }
  }

  @Test
  @DisplayName("Should translate ConstraintViolationException to 422")
  void testMapping() {
    given()
        .contentType("application/json")
        .body("") // empty string for @NotBlank field
        .when()
        .post("/test-bean-val")
        .then()
        .statusCode(422)
        .body("error.code", is("VALIDATION_ERROR"));
  }
}
