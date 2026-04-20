package br.org.catolicasc.pug.shared.presenter.rest.mappers;

import br.org.catolicasc.pug.shared.domain.enums.SharedFieldErrorCodes;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class AppValidationExceptionMapperTest {

    @Path("/test-validation")
    public static class ValidationTriggerResource {
        @GET
        public void trigger() {
            throw new AppValidationException(SharedFieldErrorCodes.INVALID_NAME_BLANK);
        }
    }

    @Test
    @DisplayName("Should translate AppValidationException to HTTP 400 with structured JSON")
    void testMapping() {
        given()
                .when().get("/test-validation")
                .then()
                .statusCode(400)
                .body("success", is(false))
                .body("error.code", is("VALIDATION_ERROR"))
                .body("error.details[0].field", is("name"))
                .body("error.details[0].errors[0].code", is("INVALID_NAME_BLANK"));
    }
}