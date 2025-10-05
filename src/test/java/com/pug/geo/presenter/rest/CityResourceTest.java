package com.pug.geo.presenter.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.geo.domain.City;
import com.pug.geo.usecase.get.RetrieveCitiesByPatternQuery;
import com.pug.geo.usecase.get.RetrieveCityByIbgeCodeQuery;
import com.pug.geo.usecase.get.RetrieveCityHandler;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CityResourceTest {

  @InjectMock RetrieveCityHandler handler;

  private static City city(String name, String ibge) {
    return City.builder().id(UUID.randomUUID()).name(name).ibgeCode(ibge).build();
  }

  @Test
  void listOkMapsToApiResponseAndUsesHandler() {
    when(handler.handle(
            (RetrieveCitiesByPatternQuery)
                argThat(
                    q ->
                        q instanceof RetrieveCitiesByPatternQuery(String query, Integer limit)
                            && "flo".equals(query)
                            && Integer.valueOf(2).equals(limit))))
        .thenReturn(List.of(city("Florianópolis", "4205407"), city("Floresta Alta", "1234567")));

    given()
        .when()
        .get("/cities?q=flo&limit=2")
        .then()
        .statusCode(200)
        .body("success", equalTo(true))
        .body("data", hasSize(2))
        .body("data[0].name", notNullValue())
        .body("data[0].ibgeCode", notNullValue());

    verify(handler)
        .handle(
            (RetrieveCitiesByPatternQuery)
                argThat(
                    q ->
                        q instanceof RetrieveCitiesByPatternQuery(String query, Integer limit)
                            && "flo".equals(query)
                            && Integer.valueOf(2).equals(limit)));
    verifyNoMoreInteractions(handler, handler);
  }

  @Test
  void listLimitValidationViolationReturns422() {
    given().when().get("/cities?limit=0").then().statusCode(422);

    verifyNoInteractions(handler, handler);
  }

  @Test
  void getByIbgeCodeOkMapsToApiResponseAndUsesHandler() {
    when(handler.handle(
            (RetrieveCityByIbgeCodeQuery)
                argThat(
                    q ->
                        q instanceof RetrieveCityByIbgeCodeQuery(String ibgeCode)
                            && "4205407".equals(ibgeCode))))
        .thenReturn(city("Florianópolis", "4205407"));

    given()
        .when()
        .get("/cities/4205407")
        .then()
        .statusCode(200)
        .body("success", equalTo(true))
        .body("data.name", equalTo("Florianópolis"))
        .body("data.ibgeCode", equalTo("4205407"));

    verify(handler)
        .handle(
            (RetrieveCityByIbgeCodeQuery)
                argThat(
                    q ->
                        q instanceof RetrieveCityByIbgeCodeQuery(String ibgeCode)
                            && "4205407".equals(ibgeCode)));
    verifyNoMoreInteractions(handler, handler);
  }
}
