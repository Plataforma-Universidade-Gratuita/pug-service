package com.pug.partner.presenter.rest.staff;

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

import com.pug.identity.domain.Role;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.Staff;
import com.pug.partner.presenter.rest.dto.RegisterStaffRequest;
import com.pug.partner.usecase.staff.create.RegisterStaffCommand;
import com.pug.partner.usecase.staff.create.RegisterStaffHandler;
import com.pug.partner.usecase.staff.get.RetrieveStaffByUserRoleIdQuery;
import com.pug.partner.usecase.staff.get.RetrieveStaffHandler;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@QuarkusTest
class StaffResourceTest {

  @InjectMock RegisterStaffHandler createHandler;
  @InjectMock RetrieveStaffHandler retrieveHandler;

  private static Staff staff(UUID id, UUID userRoleId, UUID entityId) {
    return Staff.builder()
        .id(id)
        .userRole(Role.builder().id(userRoleId).build())
        .entity(PartnerEntity.builder().id(entityId).build())
        .build();
  }

  @Test
  void createReturns201WithEnvelopeAndLocation() {
    var id = UUID.randomUUID();
    var userRoleId = UUID.randomUUID();
    var entityId = UUID.randomUUID();

    when(createHandler.handle(any(RegisterStaffCommand.class)))
        .thenReturn(staff(id, userRoleId, entityId));

    given()
        .header("X-Correlation-Id", "cid-staff-create")
        .contentType(MediaType.APPLICATION_JSON)
        .body(new RegisterStaffRequest(userRoleId, entityId))
        .when()
        .post("/partners/staff")
        .then()
        .statusCode(201)
        .header("X-Correlation-Id", equalTo("cid-staff-create"))
        .header("Location", endsWith("/partners/staff/" + id))
        .body("success", equalTo(true))
        .body("data.id", equalTo(id.toString()))
        .body("error", nullValue());

    var cap = ArgumentCaptor.forClass(RegisterStaffCommand.class);
    verify(createHandler).handle(cap.capture());
    assert cap.getValue().userRoleId().equals(userRoleId);
    assert cap.getValue().entityId().equals(entityId);
  }

  @Test
  void getByUserRoleIdSuccessReturnsEnvelope() {
    var id = UUID.randomUUID();
    var userRoleId = UUID.randomUUID();
    var entityId = UUID.randomUUID();

    when(retrieveHandler.handle(any(RetrieveStaffByUserRoleIdQuery.class)))
        .thenReturn(staff(id, userRoleId, entityId));

    given()
        .header("X-Correlation-Id", "cid-staff-get-200")
        .when()
        .get("/partners/staff?userRoleId={id}", userRoleId)
        .then()
        .statusCode(200)
        .header("X-Correlation-Id", equalTo("cid-staff-get-200"))
        .body("success", equalTo(true))
        .body("data.id", equalTo(id.toString()))
        .body("data.userRoleId", equalTo(userRoleId.toString()))
        .body("data.entityId", equalTo(entityId.toString()))
        .body("error", nullValue());

    verify(retrieveHandler).handle(any(RetrieveStaffByUserRoleIdQuery.class));
  }

  @Test
  void getByUserRoleIdNotFoundMaps404WithDetail() {
    var missing = UUID.randomUUID();
    when(retrieveHandler.handle(any(RetrieveStaffByUserRoleIdQuery.class)))
        .thenThrow(new com.pug.partner.domain.exceptions.StaffNotFoundException(missing));

    given()
        .header("Accept-Language", "en-US")
        .header("X-Correlation-Id", "cid-staff-404")
        .when()
        .get("/partners/staff?userRoleId={id}", missing)
        .then()
        .statusCode(404)
        .header("X-Correlation-Id", "cid-staff-404")
        .body("success", equalTo(false))
        .body("data", nullValue())
        .body("error.code", equalTo("STAFF_NOT_FOUND"))
        .body("error.details.userRoleId", equalTo(missing.toString()))
        .body("timestamp", notNullValue());
  }

  @Test
  void getByUserRoleIdValidationErrorMaps422Envelope() {
    given()
        .header("X-Correlation-Id", "cid-staff-422")
        .when()
        .get("/partners/staff?userRoleId=")
        .then()
        .statusCode(422)
        .header("X-Correlation-Id", "cid-staff-422")
        .body("success", equalTo(false))
        .body("error.code", equalTo("VALIDATION_ERROR"))
        .body("error.details.violations", not(empty()));
    verifyNoInteractions(retrieveHandler);
  }
}
