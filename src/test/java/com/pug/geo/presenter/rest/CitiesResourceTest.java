package com.pug.geo.presenter.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.geo.domain.City;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.geo.presenter.dtos.CityCreateOrUpdateRequest;
import com.pug.geo.service.CitiesService;
import com.pug.helpers.domainGenerators.CityGenerator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CitiesResourceTest {

  @InjectMock CitiesService service;

  private final CityGenerator gen = new CityGenerator();

  @Test
  void create_returns_201_location_and_body() {
    CityCreateOrUpdateRequest req = new CityCreateOrUpdateRequest("Joinville", "4209102");
    City created =
        City.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .name("Joinville")
            .ibgeCode(new IbgeCode("4209102"))
            .build();
    when(service.save(any(City.class))).thenReturn(created);

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/geo/cities")
        .then()
        .statusCode(201)
        .header("Location", endsWith("/geo/cities/" + created.getId()))
        .body(containsString("Joinville"))
        .body(containsString("4209102"));

    verify(service)
        .save(
            argThat(
                c ->
                    "Joinville".equals(c.getName())
                        && "4209102".equals(c.getIbgeCode().toString())));
  }

  @Test
  void create_invalid_payload_triggers_400() {
    CityCreateOrUpdateRequest bad = new CityCreateOrUpdateRequest("   ", "   ");

    given()
        .contentType(ContentType.JSON)
        .body(bad)
        .when()
        .post("/geo/cities")
        .then()
        .statusCode(422);

    verifyNoInteractions(service);
  }

  @Test
  void bulk_create_returns_201_and_calls_service_with_mapped_list() {
    var r1 = new CityCreateOrUpdateRequest("Araquari", "4201307");
    var r2 = new CityCreateOrUpdateRequest("Jaraguá do Sul", "4208906");

    given()
        .contentType(ContentType.JSON)
        .body(new com.pug.shared.presenter.dtos.BulkCreateRequest<>(List.of(r1, r2)))
        .when()
        .post("/geo/cities/bulk")
        .then()
        .statusCode(201)
        .body(containsString("2"));

    verify(service)
        .saveAll(
            argThat(
                it -> {
                  int[] count = {0};
                  it.forEach(
                      c -> {
                        count[0]++;
                        if (c.getName().equals("Araquari")) {
                          assert "4201307".equals(c.getIbgeCode().toString());
                        }
                        if (c.getName().equals("Jaraguá do Sul")) {
                          assert "4208906".equals(c.getIbgeCode().toString());
                        }
                      });
                  return count[0] == 2;
                }));
  }

  @Test
  void update_returns_200_and_body() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    CityCreateOrUpdateRequest req = new CityCreateOrUpdateRequest("New Name", "7654321");
    City updated = City.builder().id(id).name("New Name").ibgeCode(new IbgeCode("7654321")).build();
    when(service.update(eq(id), any(City.class))).thenReturn(updated);

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .put("/geo/cities/{id}", id)
        .then()
        .statusCode(200)
        .body(containsString(id.toString()))
        .body(containsString("New Name"))
        .body(containsString("7654321"));

    verify(service)
        .update(
            eq(id),
            argThat(
                c ->
                    "New Name".equals(c.getName())
                        && "7654321".equals(c.getIbgeCode().toString())));
  }

  @Test
  void list_without_q_calls_listAll_and_returns_200() {
    City c1 = gen.randomCityWithId();
    City c2 = gen.randomCityWithId();
    when(service.listAll()).thenReturn(List.of(c1, c2));

    given()
        .when()
        .get("/geo/cities")
        .then()
        .statusCode(200)
        .body(containsString(c1.getIbgeCode().toString()))
        .body(containsString(c2.getIbgeCode().toString()));

    verify(service).listAll();
    verify(service, never()).search(anyString());
  }

  @Test
  void list_with_q_calls_search_and_returns_200() {
    String q = "jara";
    City c = gen.randomCityWithId();
    when(service.search(q)).thenReturn(List.of(c));

    given()
        .when()
        .get("/geo/cities?q={q}", q)
        .then()
        .statusCode(200)
        .body(containsString(c.getIbgeCode().toString()))
        .body(containsString(c.getName()));

    verify(service).search(q);
    verify(service, never()).listAll();
  }

  @Test
  void get_by_id_returns_200() {
    City c = gen.randomCityWithId();
    when(service.getById(c.getId())).thenReturn(c);

    given()
        .when()
        .get("/geo/cities/{id}", c.getId())
        .then()
        .statusCode(200)
        .body(containsString(c.getId().toString()))
        .body(containsString(c.getIbgeCode().toString()));

    verify(service).getById(c.getId());
  }

  @Test
  void get_by_ibge_returns_200() {
    City c = gen.randomCityWithId();
    when(service.getByIbgeCode(c.getIbgeCode().toString())).thenReturn(c);

    given()
        .when()
        .get("/geo/cities/ibge/{code}", c.getIbgeCode().toString())
        .then()
        .statusCode(200)
        .body(containsString(c.getIbgeCode().toString()))
        .body(containsString(c.getName()));

    verify(service).getByIbgeCode(c.getIbgeCode().toString());
  }

  @Test
  void delete_returns_200_and_calls_service() {
    UUID a = UuidCreator.getTimeOrderedEpoch();
    UUID b = UuidCreator.getTimeOrderedEpoch();
    when(service.deleteByIds(any())).thenReturn(2L);

    record UuidsRequest(List<UUID> ids) {}
    var req = new UuidsRequest(List.of(a, b));

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .delete("/geo/cities")
        .then()
        .statusCode(200)
        .body(containsString("2"));

    verify(service).deleteByIds(argThat(ids -> ids != null && ids.iterator().hasNext()));
  }
}
