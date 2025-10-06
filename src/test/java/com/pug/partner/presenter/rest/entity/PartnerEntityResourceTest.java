package com.pug.partner.presenter.rest.entity;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.exceptions.DuplicateCnpjException;
import com.pug.partner.domain.exceptions.PartnerEntityNotFoundException;
import com.pug.partner.presenter.rest.dto.AttPartnerEntityRequest;
import com.pug.partner.presenter.rest.dto.RegisterPartnerEntityRequest;
import com.pug.partner.usecase.entity.create.RegisterPartnerEntityCommand;
import com.pug.partner.usecase.entity.create.RegisterPartnerEntityHandler;
import com.pug.partner.usecase.entity.get.RetrievePartnerEntityByCnpjQuery;
import com.pug.partner.usecase.entity.get.RetrievePartnerEntityByIdQuery;
import com.pug.partner.usecase.entity.get.RetrievePartnerEntityHandler;
import com.pug.partner.usecase.entity.update.AttPartnerEntityCommand;
import com.pug.partner.usecase.entity.update.AttPartnerEntityHandler;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@QuarkusTest
class PartnerEntityResourceTest {

  @InjectMock RegisterPartnerEntityHandler createHandler;
  @InjectMock AttPartnerEntityHandler updateHandler;
  @InjectMock RetrievePartnerEntityHandler retrieveHandler;

  @Test
  void createReturns201WithEnvelopeAndLocation() {
    var id = UUID.randomUUID();
    var cityId = UUID.randomUUID();
    when(createHandler.handle(any(RegisterPartnerEntityCommand.class))).thenReturn(id);

    given()
        .header("X-Correlation-Id", "cid-create-1")
        .contentType(MediaType.APPLICATION_JSON)
        .body(new RegisterPartnerEntityRequest("11.222.333/0001-81", "Org", cityId, "Addr"))
        .when()
        .post("/partners/entities")
        .then()
        .statusCode(201)
        .header("X-Correlation-Id", equalTo("cid-create-1"))
        .header("Location", endsWith("/partners/entities/" + id))
        .body("success", equalTo(true))
        .body("data.id", equalTo(id.toString()))
        .body("error", nullValue());

    var cap = ArgumentCaptor.forClass(RegisterPartnerEntityCommand.class);
    verify(createHandler).handle(cap.capture());
    assert cap.getValue().cnpj().equals("11.222.333/0001-81");
    assert cap.getValue().name().equals("Org");
    assert cap.getValue().cityId().equals(cityId);
    assert cap.getValue().address().equals("Addr");
  }

  @Test
  void getByIdNotFoundMapsTo404EnvelopeWithIdDetail() {
    var id = UUID.randomUUID();
    when(retrieveHandler.handle(any(RetrievePartnerEntityByIdQuery.class)))
        .thenThrow(new PartnerEntityNotFoundException(id));

    given()
        .header("Accept-Language", "en-US")
        .header("X-Correlation-Id", "cid-getid-404")
        .when()
        .get("/partners/entities/{id}", id)
        .then()
        .statusCode(404)
        .header("X-Correlation-Id", "cid-getid-404")
        .body("success", equalTo(false))
        .body("data", nullValue())
        .body("error.code", equalTo("ENTITY_NOT_FOUND"))
        .body("error.details.id", equalTo(id.toString()))
        .body("timestamp", notNullValue());
  }

  @Test
  void getByCnpjValidationErrorMapsTo422Envelope() {
    given()
        .header("X-Correlation-Id", "cid-cnpj-422")
        .when()
        .get("/partners/entities?cnpj=")
        .then()
        .statusCode(422)
        .header("X-Correlation-Id", "cid-cnpj-422")
        .body("success", equalTo(false))
        .body("error.code", equalTo("VALIDATION_ERROR"))
        .body("error.details.violations", not(empty()));
    verifyNoInteractions(retrieveHandler);
  }

  @Test
  void getByCnpjSuccessReturnsEnvelopeAndNormalizedValue() {
    var id = UUID.randomUUID();
    var cityId = UUID.randomUUID();
    var ent =
        PartnerEntity.builder()
            .id(id)
            .cnpj("11222333000181")
            .name("Org")
            .city(
                com.pug.geo.domain.City.builder()
                    .id(cityId)
                    .name("City")
                    .ibgeCode("4200000")
                    .build())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    when(retrieveHandler.handle(any(RetrievePartnerEntityByCnpjQuery.class))).thenReturn(ent);

    given()
        .header("X-Correlation-Id", "cid-cnpj-200")
        .when()
        .get("/partners/entities?cnpj=11.222.333/0001-81")
        .then()
        .statusCode(200)
        .header("X-Correlation-Id", "cid-cnpj-200")
        .body("success", equalTo(true))
        .body("data.id", equalTo(id.toString()))
        .body("data.cnpj", equalTo("11222333000181"))
        .body("data.name", equalTo("Org"))
        .body("data.cityId", equalTo(cityId.toString()))
        .body("error", nullValue());

    verify(retrieveHandler).handle(any(RetrievePartnerEntityByCnpjQuery.class));
  }

  @Test
  void update_success_returns_200_with_envelope() {
    var id = UUID.randomUUID();
    var cityId = UUID.randomUUID();
    var ent =
        PartnerEntity.builder()
            .id(id)
            .cnpj("11222333000181")
            .name("Org Updated")
            .city(
                com.pug.geo.domain.City.builder()
                    .id(cityId)
                    .name("City")
                    .ibgeCode("4200000")
                    .build())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    when(updateHandler.handle(any(AttPartnerEntityCommand.class))).thenReturn(ent);

    given()
        .header("X-Correlation-Id", "cid-upd-200")
        .contentType(MediaType.APPLICATION_JSON)
        .body(new AttPartnerEntityRequest("11.222.333/0001-81", "Org Updated", cityId, "Addr"))
        .when()
        .put("/partners/entities/{id}", id)
        .then()
        .statusCode(200)
        .header("X-Correlation-Id", "cid-upd-200")
        .body("success", equalTo(true))
        .body("data.id", equalTo(id.toString()))
        .body("data.cnpj", equalTo("11222333000181"))
        .body("data.name", equalTo("Org Updated"))
        .body("data.cityId", equalTo(cityId.toString()))
        .body("error", nullValue());

    verify(updateHandler).handle(any(AttPartnerEntityCommand.class));
  }

  @Test
  void updateNotFoundMaps404WithIdDetail() {
    var id = UUID.randomUUID();
    when(updateHandler.handle(any(AttPartnerEntityCommand.class)))
        .thenThrow(new PartnerEntityNotFoundException(id));

    given()
        .header("Accept-Language", "en-US")
        .header("X-Correlation-Id", "cid-upd-404")
        .contentType(MediaType.APPLICATION_JSON)
        .body(new AttPartnerEntityRequest("11.222.333/0001-81", "Org", UUID.randomUUID(), "Addr"))
        .when()
        .put("/partners/entities/{id}", id)
        .then()
        .statusCode(404)
        .body("success", equalTo(false))
        .body("error.code", equalTo("ENTITY_NOT_FOUND"))
        .body("error.details.id", equalTo(id.toString()));
  }

  @Test
  void updateDuplicateCnpjMaps409() {
    var id = UUID.randomUUID();
    when(updateHandler.handle(any(AttPartnerEntityCommand.class)))
        .thenThrow(new DuplicateCnpjException("11222333000181"));

    given()
        .header("X-Correlation-Id", "cid-upd-409")
        .contentType(MediaType.APPLICATION_JSON)
        .body(new AttPartnerEntityRequest("11.222.333/0001-81", "Org", UUID.randomUUID(), "Addr"))
        .when()
        .put("/partners/entities/{id}", id)
        .then()
        .statusCode(409)
        .body("success", equalTo(false))
        .body("error.code", notNullValue());
  }

  @Test
  void getByIdSuccessReturnsEnvelope() {
    var id = UUID.randomUUID();
    var cityId = UUID.randomUUID();
    var ent =
        PartnerEntity.builder()
            .id(id)
            .cnpj("11222333000181")
            .name("Org")
            .city(
                com.pug.geo.domain.City.builder()
                    .id(cityId)
                    .name("City")
                    .ibgeCode("4200000")
                    .build())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    when(retrieveHandler.handle(any(RetrievePartnerEntityByIdQuery.class))).thenReturn(ent);

    given()
        .header("X-Correlation-Id", "cid-getid-200")
        .when()
        .get("/partners/entities/{id}", id)
        .then()
        .statusCode(200)
        .header("X-Correlation-Id", equalTo("cid-getid-200"))
        .body("success", equalTo(true))
        .body("data.id", equalTo(id.toString()))
        .body("data.cnpj", equalTo("11222333000181"))
        .body("data.name", equalTo("Org"))
        .body("error", nullValue());

    verify(retrieveHandler).handle(any(RetrievePartnerEntityByIdQuery.class));
  }
}
