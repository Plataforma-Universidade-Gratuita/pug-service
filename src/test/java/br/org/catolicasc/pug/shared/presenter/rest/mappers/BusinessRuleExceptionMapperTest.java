package br.org.catolicasc.pug.shared.presenter.rest.mappers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import br.org.catolicasc.pug.identity.domain.enums.IdentityErrorCodes;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class BusinessRuleExceptionMapperTest {

  @Path("/test-business-rule")
  public static class TriggerResource {
    @GET
    public void trigger() {
      throw new BusinessRuleException(IdentityErrorCodes.ACCOUNT_ALREADY_EXISTS);
    }
  }

  @Test
  @DisplayName("Should translate BusinessRuleException to HTTP 422 Unprocessable Entity")
  void testMapping() {
    given()
        .when()
        .get("/test-business-rule")
        .then()
        .statusCode(422)
        .body("success", is(false))
        .body("error.code", is("ACCOUNT_ALREADY_EXISTS"));
  }
}
