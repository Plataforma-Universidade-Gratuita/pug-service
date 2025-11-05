package com.pug.shared.http;

import io.quarkus.test.junit.QuarkusTest;

// import jakarta.ws.rs.core.MediaType;
// import jakarta.ws.rs.core.Response;
// import org.junit.jupiter.api.Test;
//
//
// import static io.restassured.RestAssured.given;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class CorrelationFilterTest {
  // TODO: UPDATE AFTER ANY ENDPOINT IS DEFINED

  //    @Test
  //    public void testCorrelationIdGeneratedForRequest() {
  //        Response response =
  //                (Response) given()
  //                        .contentType(MediaType.APPLICATION_JSON)
  //                        .when().get("/some-endpoint")
  //                        .then()
  //                        .statusCode(200)
  //                        .extract().response();
  //
  //        String correlationId = response.getHeaderString("X-Correlation-Id");
  //        assertNotNull(correlationId, "The response should include a Correlation ID");
  //        assertFalse(correlationId.isEmpty(), "The Correlation ID should not be empty");
  //    }
  //
  //    @Test
  //    public void testCorrelationIdInRequestAndResponse() {
  //        String requestCorrelationId = java.util.UUID.randomUUID().toString();
  //
  //        Response response =
  //                (Response) given()
  //                        .header("X-Correlation-Id", requestCorrelationId)
  //                        .contentType(MediaType.APPLICATION_JSON)
  //                        .when().get("/some-endpoint")
  //                        .then()
  //                        .statusCode(200)
  //                        .extract().response();
  //
  //        String responseCorrelationId = response.getHeaderString("X-Correlation-Id");
  //        assertEquals(requestCorrelationId, responseCorrelationId, "The Correlation ID should be
  // the same in the response as in the request");
  //    }
}
