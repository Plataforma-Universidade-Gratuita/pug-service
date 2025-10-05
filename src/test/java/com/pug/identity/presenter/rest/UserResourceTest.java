package com.pug.identity.presenter.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.pug.identity.domain.User;
import com.pug.identity.domain.exceptions.DuplicateCpfException;
import com.pug.identity.domain.exceptions.UserNotFoundException;
import com.pug.identity.presenter.rest.dto.AttUserRequest;
import com.pug.identity.presenter.rest.dto.RegisterUserRequest;
import com.pug.identity.usecase.user.create.RegisterUserCommand;
import com.pug.identity.usecase.user.create.RegisterUserHandler;
import com.pug.identity.usecase.user.get.RetrieveUserByCpfQuery;
import com.pug.identity.usecase.user.get.RetrieveUserByIdQuery;
import com.pug.identity.usecase.user.get.RetrieveUserHandler;
import com.pug.identity.usecase.user.update.AttUserCommand;
import com.pug.identity.usecase.user.update.AttUserHandler;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@QuarkusTest
class UserResourceTest {

  @InjectMock RegisterUserHandler createUser;
  @InjectMock AttUserHandler updateUser;
  @InjectMock RetrieveUserHandler handler;

  @Test
  void createReturns201WithEnvelopeAndLocation() {
    var id = UUID.randomUUID();
    when(createUser.handle(any(RegisterUserCommand.class))).thenReturn(id);

    given()
        .header("X-Correlation-Id", "cid-1")
        .contentType(MediaType.APPLICATION_JSON)
        .body(new RegisterUserRequest("935.411.347-80", "Ada"))
        .when()
        .post("/identity/users")
        .then()
        .statusCode(201)
        .header("X-Correlation-Id", equalTo("cid-1"))
        .header("Location", endsWith("/identity/users/" + id));

    var cap = ArgumentCaptor.forClass(RegisterUserCommand.class);
    verify(createUser).handle(cap.capture());
    assert cap.getValue().cpf().equals("935.411.347-80");
    assert cap.getValue().name().equals("Ada");
  }

  @Test
  void getByIdNotFoundMapsTo404EnvelopeWithIdDetail() {
    var id = UUID.randomUUID();
    when(handler.handle(any(RetrieveUserByIdQuery.class))).thenThrow(new UserNotFoundException(id));

    given()
        .header("Accept-Language", "en-US")
        .header("X-Correlation-Id", "cid-2")
        .when()
        .get("/identity/users/{id}", id)
        .then()
        .statusCode(404)
        .header("X-Correlation-Id", "cid-2")
        .body("success", equalTo(false))
        .body("data", nullValue())
        .body("error.code", equalTo("USER_NOT_FOUND"))
        .body("error.message", anything())
        .body("error.details.id", equalTo(id.toString()))
        .body("timestamp", notNullValue());
  }

  @Test
  void getByCpfValidationErrorMapsTo422Envelope() {
    given()
        .header("X-Correlation-Id", "cid-3")
        .when()
        .get("/identity/users?cpf=") // blank
        .then()
        .statusCode(422)
        .header("X-Correlation-Id", "cid-3")
        .body("success", equalTo(false))
        .body("error.code", equalTo("VALIDATION_ERROR"))
        .body("error.details.violations", not(empty()));
    verifyNoInteractions(handler);
  }

  @Test
  void getByCpfSuccessReturnsEnvelopeAndNormalizedValue() {
    var u = User.builder().id(UUID.randomUUID()).cpf("93541134780").name("Ada").build();
    when(handler.handle(any(RetrieveUserByCpfQuery.class))).thenReturn(u);

    given()
        .header("X-Correlation-Id", "cid-4")
        .when()
        .get("/identity/users?cpf=935.411.347-80")
        .then()
        .statusCode(200)
        .header("X-Correlation-Id", "cid-4")
        .body("success", equalTo(true))
        .body("data.id", equalTo(u.getId().toString()))
        .body("data.cpf", equalTo("93541134780"))
        .body("data.name", equalTo("Ada"))
        .body("error", nullValue());
  }

  @Test
  void update_success_returns_200_with_envelope() {
    var id = UUID.randomUUID();
    var u =
        User.builder()
            .id(id)
            .cpf("93541134780")
            .name("Ada Lovelace")
            .createdAt(java.time.Instant.now())
            .updatedAt(java.time.Instant.now())
            .build();
    when(updateUser.handle(any(AttUserCommand.class))).thenReturn(u);

    given()
        .header("X-Correlation-Id", "cid-upd-1")
        .contentType(MediaType.APPLICATION_JSON)
        .body(new AttUserRequest("935.411.347-80", "Ada Lovelace"))
        .when()
        .put("/identity/users/{id}", id)
        .then()
        .statusCode(200)
        .header("X-Correlation-Id", "cid-upd-1")
        .body("success", equalTo(true))
        .body("data.id", equalTo(id.toString()))
        .body("data.cpf", equalTo("93541134780"))
        .body("data.name", equalTo("Ada Lovelace"))
        .body("error", nullValue());

    verify(updateUser).handle(any(AttUserCommand.class));
  }

  @Test
  void updateNotFoundMaps404WithIdDetail() {
    var id = UUID.randomUUID();
    when(updateUser.handle(any(AttUserCommand.class))).thenThrow(new UserNotFoundException(id));

    given()
        .header("Accept-Language", "en-US")
        .header("X-Correlation-Id", "cid-upd-2")
        .contentType(MediaType.APPLICATION_JSON)
        .body(new AttUserRequest("935.411.347-80", "Ada"))
        .when()
        .put("/identity/users/{id}", id)
        .then()
        .statusCode(404)
        .body("success", equalTo(false))
        .body("error.code", equalTo("USER_NOT_FOUND"))
        .body("error.details.id", equalTo(id.toString()));
  }

  @Test
  void updateDuplicateCpfMaps409() {
    var id = UUID.randomUUID();
    when(updateUser.handle(any(AttUserCommand.class)))
        .thenThrow(new DuplicateCpfException("93541134780"));

    given()
        .header("X-Correlation-Id", "cid-upd-3")
        .contentType(MediaType.APPLICATION_JSON)
        .body(new AttUserRequest("935.411.347-80", "Ada"))
        .when()
        .put("/identity/users/{id}", id)
        .then()
        .statusCode(409)
        .body("success", equalTo(false))
        .body("error.code", equalTo("USER_DUPLICATE_CPF"));
  }

  @Test
  void getByIdSuccessReturnsEnvelope() {
    var id = UUID.randomUUID();
    var u =
        User.builder()
            .id(id)
            .cpf("93541134780")
            .name("Ada")
            .createdAt(java.time.Instant.now())
            .updatedAt(java.time.Instant.now())
            .build();
    when(handler.handle(any(RetrieveUserByIdQuery.class))).thenReturn(u);

    given()
        .header("X-Correlation-Id", "cid-get-1")
        .when()
        .get("/identity/users/{id}", id)
        .then()
        .statusCode(200)
        .header("X-Correlation-Id", equalTo("cid-get-1"))
        .body("success", equalTo(true))
        .body("data.id", equalTo(id.toString()))
        .body("data.cpf", equalTo("93541134780"))
        .body("data.name", equalTo("Ada"))
        .body("error", nullValue());

    verify(handler).handle(any(RetrieveUserByIdQuery.class));
  }
}
