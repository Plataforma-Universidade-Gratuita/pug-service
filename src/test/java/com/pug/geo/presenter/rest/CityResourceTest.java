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
import com.pug.geo.usecase.get.byIbgeCode.GetCityByIbgeCodeHandler;
import com.pug.geo.usecase.get.byIbgeCode.GetCityByIbgeCodeQuery;
import com.pug.geo.usecase.get.byPattern.ListCitiesByPatternHandler;
import com.pug.geo.usecase.get.byPattern.ListCitiesByPatternQuery;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CityResourceTest {

  @InjectMock ListCitiesByPatternHandler listCities;
  @InjectMock GetCityByIbgeCodeHandler getCityByIbgeCode;

  private static City city(String name, String ibge) {
    return City.builder().id(UUID.randomUUID()).name(name).ibgeCode(ibge).build();
  }

  @Test
  void listOkMapsToApiResponseAndUsesHandler() {
    when(listCities.handle(
            argThat(
                q ->
                    q instanceof ListCitiesByPatternQuery l
                        && "flo".equals(l.query())
                        && Integer.valueOf(2).equals(l.limit()))))
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

    verify(listCities)
        .handle(
            argThat(
                q ->
                    q instanceof ListCitiesByPatternQuery l
                        && "flo".equals(l.query())
                        && Integer.valueOf(2).equals(l.limit())));
    verifyNoMoreInteractions(listCities, getCityByIbgeCode);
  }

  @Test
  void listLimitValidationViolationReturns422() {
    given().when().get("/cities?limit=0").then().statusCode(422);

    verifyNoInteractions(listCities, getCityByIbgeCode);
  }

  @Test
  void getByIbgeCodeOkMapsToApiResponseAndUsesHandler() {
    when(getCityByIbgeCode.handle(
            argThat(q -> q instanceof GetCityByIbgeCodeQuery g && "4205407".equals(g.ibgeCode()))))
        .thenReturn(city("Florianópolis", "4205407"));

    given()
        .when()
        .get("/cities/4205407")
        .then()
        .statusCode(200)
        .body("success", equalTo(true))
        .body("data.name", equalTo("Florianópolis"))
        .body("data.ibgeCode", equalTo("4205407"));

    verify(getCityByIbgeCode)
        .handle(
            argThat(q -> q instanceof GetCityByIbgeCodeQuery g && "4205407".equals(g.ibgeCode())));
    verifyNoMoreInteractions(getCityByIbgeCode, listCities);
  }
}
