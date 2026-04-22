package br.org.catolicasc.pug.shared.presenter.rest.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.shared.domain.enums.SharedErrorCodes;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.Response;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("PersistenceExceptionMapper Coverage")
class PersistenceExceptionMapperTest {

  @Inject PersistenceExceptionMapper mapper;

  @Test
  @DisplayName("Should map Unique Constraint violation to 409")
  void shouldMapUniqueConstraint() {
    ConstraintViolationException cve =
        new ConstraintViolationException("msg", null, "uq_test_constraint");
    PersistenceException pe = new PersistenceException(cve);

    Response response = mapper.toResponse(pe);

    assertThat(response.getStatus()).isEqualTo(409);
    // Verifique se o código do erro retornado é o de duplicidade
    assertThat(response.getEntity().toString())
        .contains(SharedErrorCodes.DUPLICATED_RESOURCE_ERROR.getCode());
  }

  @Test
  @DisplayName("Should map Foreign Key violation to 409")
  void shouldMapForeignKeyConstraint() {
    ConstraintViolationException cve =
        new ConstraintViolationException("msg", null, "fk_test_constraint");
    PersistenceException pe = new PersistenceException(cve);

    Response response = mapper.toResponse(pe);

    assertThat(response.getStatus()).isEqualTo(409);
    assertThat(response.getEntity().toString())
        .contains(SharedErrorCodes.DATA_INTEGRITY_ERROR.getCode());
  }

  @Test
  @DisplayName("Should map DataException to 409")
  void shouldMapDataException() {
    DataException de = new DataException("msg", null);
    PersistenceException pe = new PersistenceException(de);

    Response response = mapper.toResponse(pe);

    assertThat(response.getStatus()).isEqualTo(409);
    assertThat(response.getEntity().toString())
        .contains(SharedErrorCodes.DATA_INTEGRITY_ERROR.getCode());
  }

  @Test
  @DisplayName("Should map Generic PersistenceException to 500")
  void shouldMapGenericPersistenceException() {
    PersistenceException pe = new PersistenceException("generic error");

    Response response = mapper.toResponse(pe);

    assertThat(response.getStatus()).isEqualTo(500);
    assertThat(response.getEntity().toString()).contains(SharedErrorCodes.INTERNAL_ERROR.getCode());
  }

  @Test
  @DisplayName("Should handle null constraint name gracefully")
  void shouldHandleNullConstraintName() {
    ConstraintViolationException cve = new ConstraintViolationException("msg", null, null);
    PersistenceException pe = new PersistenceException(cve);

    Response response = mapper.toResponse(pe);

    assertThat(response.getStatus()).isEqualTo(409);
  }
}
