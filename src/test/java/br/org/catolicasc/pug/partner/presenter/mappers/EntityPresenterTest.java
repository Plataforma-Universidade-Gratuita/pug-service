package br.org.catolicasc.pug.partner.presenter.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.geo.presenter.dtos.CityResponse;
import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.partner.infra.read.dtos.EntityComplexSearchView;
import br.org.catolicasc.pug.partner.infra.read.dtos.EntityView;
import br.org.catolicasc.pug.partner.presenter.dtos.entities.EntityComplexSearchResponse;
import br.org.catolicasc.pug.partner.presenter.dtos.entities.EntityCreateRequest;
import br.org.catolicasc.pug.partner.presenter.dtos.entities.EntityResponse;
import br.org.catolicasc.pug.partner.presenter.dtos.entities.EntityUpdateRequest;
import br.org.catolicasc.pug.partner.service.dtos.entities.EntityCreateCommand;
import br.org.catolicasc.pug.partner.service.dtos.entities.EntityUpdateCommand;
import com.github.f4b6a3.uuid.UuidCreator;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("EntityPresenter Coverage")
class EntityPresenterTest {

  @Nested
  @DisplayName("Create Command Mapping Tests")
  class CreateCommandMappingTests {

    @Test
    @DisplayName("Should map EntityCreateRequest to EntityCreateCommand")
    void toCreateCommand() {
      UUID cityId = UuidCreator.getTimeOrderedEpoch();
      String cnpj = TestBrazilianIdentifierGenerator.generateValidCnpj();
      EntityCreateRequest req = new EntityCreateRequest(cnpj, "Acme Corp", cityId, "Rua A, 123");

      EntityCreateCommand cmd = EntityPresenter.toCommand(req);

      assertThat(cmd).isNotNull();
      assertThat(cmd.cnpjString()).isEqualTo(cnpj);
      assertThat(cmd.name()).isEqualTo("Acme Corp");
      assertThat(cmd.cityId()).isEqualTo(cityId);
      assertThat(cmd.address()).isEqualTo("Rua A, 123");
    }

    @Test
    @DisplayName("Should return null when EntityCreateRequest is null")
    void toCreateCommandNull() {
      assertThat(EntityPresenter.toCommand((EntityCreateRequest) null)).isNull();
    }
  }

  @Nested
  @DisplayName("Update Command Mapping Tests")
  class UpdateCommandMappingTests {

    @Test
    @DisplayName("Should map EntityUpdateRequest to EntityUpdateCommand")
    void toUpdateCommand() {
      UUID cityId = UuidCreator.getTimeOrderedEpoch();
      EntityUpdateRequest req = new EntityUpdateRequest("New Name", cityId, "Rua B, 456");

      EntityUpdateCommand cmd = EntityPresenter.toCommand(req);

      assertThat(cmd).isNotNull();
      assertThat(cmd.name()).isEqualTo("New Name");
      assertThat(cmd.cityId()).isEqualTo(cityId);
      assertThat(cmd.address()).isEqualTo("Rua B, 456");
    }

    @Test
    @DisplayName("Should map partial EntityUpdateRequest (nulls allowed)")
    void toUpdateCommandPartial() {
      EntityUpdateRequest req = new EntityUpdateRequest(null, null, null);

      EntityUpdateCommand cmd = EntityPresenter.toCommand(req);

      assertThat(cmd).isNotNull();
      assertThat(cmd.name()).isNull();
      assertThat(cmd.cityId()).isNull();
      assertThat(cmd.address()).isNull();
    }

    @Test
    @DisplayName("Should return null when EntityUpdateRequest is null")
    void toUpdateCommandNull() {
      assertThat(EntityPresenter.toCommand((EntityUpdateRequest) null)).isNull();
    }
  }

  @Nested
  @DisplayName("Response Mapping Tests")
  class ResponseMappingTests {

    @Test
    @DisplayName("Should return null when EntityView is null")
    void toResponseNull() {
      assertThat(EntityPresenter.toResponse(null, Locale.US)).isNull();
    }

    @Test
    @DisplayName("Should map EntityView to EntityResponse correctly")
    void toResponseSuccess() {
      UUID id = UuidCreator.getTimeOrderedEpoch();
      UUID cityId = UuidCreator.getTimeOrderedEpoch();
      OffsetDateTime now = OffsetDateTime.now();
      String cnpj = TestBrazilianIdentifierGenerator.generateValidCnpj();
      EntityView view = new EntityView(id, cnpj, "Acme Corp", "Rua A, 123", cityId, now, now);

      EntityResponse response = EntityPresenter.toResponse(view, Locale.US);

      assertThat(response).isNotNull();
      assertThat(response.id()).isEqualTo(id);
      assertThat(response.cnpj()).isEqualTo(cnpj);
      assertThat(response.cnpjFormatted()).isNotBlank();
      assertThat(response.cnpjFormatted()).contains(".");
      assertThat(response.name()).isEqualTo("Acme Corp");
      assertThat(response.address()).isEqualTo("Rua A, 123");
      assertThat(response.cityId()).isEqualTo(cityId);
      assertThat(response.auditInfo()).isNotNull();
      assertThat(response.auditInfo().createdAt()).isEqualTo(now);
      assertThat(response.auditInfo().updatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should map EntityComplexSearchView to EntityComplexSearchResponse correctly")
    void toComplexSearchResponseSuccess() {
      UUID id = UuidCreator.getTimeOrderedEpoch();
      UUID cityId = UuidCreator.getTimeOrderedEpoch();
      OffsetDateTime now = OffsetDateTime.now();
      String cnpj = TestBrazilianIdentifierGenerator.generateValidCnpj();
      EntityComplexSearchView view =
          new EntityComplexSearchView(
              id, cnpj, "Acme Corp", "Rua A, 123", cityId, "Blumenau", "1234567", now, now);

      EntityComplexSearchResponse response =
          EntityPresenter.toComplexSearchResponse(view, Locale.US);

      assertThat(response).isNotNull();
      assertThat(response.id()).isEqualTo(id);
      assertThat(response.cnpj()).isEqualTo(cnpj);
      assertThat(response.cnpjFormatted()).isNotBlank();
      assertThat(response.name()).isEqualTo("Acme Corp");
      assertThat(response.address()).isEqualTo("Rua A, 123");
      assertThat(response.city()).isEqualTo(new CityResponse(cityId, "Blumenau", "1234567"));
      assertThat(response.auditInfo()).isNotNull();
      assertThat(response.auditInfo().createdAt()).isEqualTo(now);
    }

    @Test
    @DisplayName(
        "Should map EntityComplexSearchView to EntitySimpleComplexSearchResponse correctly")
    void toSimpleComplexSearchResponseSuccess() {
      UUID id = UuidCreator.getTimeOrderedEpoch();
      EntityComplexSearchView view =
          new EntityComplexSearchView(
              id,
              TestBrazilianIdentifierGenerator.generateValidCnpj(),
              "Acme Corp",
              "Rua A, 123",
              UuidCreator.getTimeOrderedEpoch(),
              "Blumenau",
              "1234567",
              OffsetDateTime.now(),
              OffsetDateTime.now());

      var response = EntityPresenter.toSimpleComplexSearchResponse(view);

      assertThat(response).isNotNull();
      assertThat(response.id()).isEqualTo(id);
      assertThat(response.name()).isEqualTo("Acme Corp");
    }

    @Test
    @DisplayName("Should handle CNPJ with invalid length gracefully")
    void toResponseCnpjInvalidLength() {
      UUID id = UuidCreator.getTimeOrderedEpoch();
      UUID cityId = UuidCreator.getTimeOrderedEpoch();
      OffsetDateTime now = OffsetDateTime.now();
      EntityView view = new EntityView(id, "123", "Short CNPJ", "Addr", cityId, now, now);

      EntityResponse response = EntityPresenter.toResponse(view, Locale.US);

      assertThat(response).isNotNull();
      assertThat(response.cnpj()).isEqualTo("123");
      assertThat(response.cnpjFormatted()).isEqualTo("123");
    }

    @Test
    @DisplayName("Should handle null CNPJ gracefully")
    void toResponseCnpjNull() {
      UUID id = UuidCreator.getTimeOrderedEpoch();
      UUID cityId = UuidCreator.getTimeOrderedEpoch();
      OffsetDateTime now = OffsetDateTime.now();
      EntityView view = new EntityView(id, null, "No CNPJ", "Addr", cityId, now, now);

      EntityResponse response = EntityPresenter.toResponse(view, Locale.US);

      assertThat(response).isNotNull();
      assertThat(response.cnpj()).isNull();
      assertThat(response.cnpjFormatted()).isNull();
    }

    @Test
    @DisplayName("Should handle null audit timestamps")
    void toResponseNullTimestamps() {
      UUID id = UuidCreator.getTimeOrderedEpoch();
      UUID cityId = UuidCreator.getTimeOrderedEpoch();
      String cnpj = TestBrazilianIdentifierGenerator.generateValidCnpj();
      EntityView view = new EntityView(id, cnpj, "Name", "Addr", cityId, null, null);

      EntityResponse response = EntityPresenter.toResponse(view, Locale.US);

      assertThat(response).isNotNull();
      assertThat(response.id()).isEqualTo(id);
    }
  }
}
