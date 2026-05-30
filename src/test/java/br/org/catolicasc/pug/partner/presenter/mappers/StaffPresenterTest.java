package br.org.catolicasc.pug.partner.presenter.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.identity.infra.read.dtos.AccountComplexSearchView;
import br.org.catolicasc.pug.identity.infra.read.dtos.AccountView;
import br.org.catolicasc.pug.partner.infra.read.dtos.EntityComplexSearchView;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffComplexSearchView;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffView;
import br.org.catolicasc.pug.partner.presenter.dtos.staff.StaffComplexSearchResponse;
import br.org.catolicasc.pug.partner.presenter.dtos.staff.StaffCreateRequest;
import br.org.catolicasc.pug.partner.presenter.dtos.staff.StaffResponse;
import br.org.catolicasc.pug.partner.presenter.dtos.staff.StaffUpdateRequest;
import br.org.catolicasc.pug.partner.service.dtos.staff.StaffCreateCommand;
import br.org.catolicasc.pug.partner.service.dtos.staff.StaffUpdateCommand;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.i18n.I18n;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("StaffPresenter Coverage")
class StaffPresenterTest {

  @Inject I18n i18n;

  @Nested
  @DisplayName("Create Command Mapping Tests")
  class CreateCommandMappingTests {

    @Test
    @DisplayName("Should map StaffCreateRequest to StaffCreateCommand")
    void toCreateCommand() {
      UUID entityId = UuidCreator.getTimeOrderedEpoch();
      String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
      StaffCreateRequest req = new StaffCreateRequest(cpf, "Staff User", "staff@pug.com", entityId);

      StaffCreateCommand cmd = StaffPresenter.toCommand(req);

      assertThat(cmd).isNotNull();
      assertThat(cmd.entityId()).isEqualTo(entityId);
      assertThat(cmd.accountCommand()).isNotNull();
      assertThat(cmd.accountCommand().emailString()).isEqualTo("staff@pug.com");
      assertThat(cmd.accountCommand().passwordHash()).isNull();
      assertThat(cmd.accountCommand().userCommand()).isNotNull();
      assertThat(cmd.accountCommand().userCommand().cpfString()).isEqualTo(cpf);
      assertThat(cmd.accountCommand().userCommand().name()).isEqualTo("Staff User");
    }

    @Test
    @DisplayName("Should return null when StaffCreateRequest is null")
    void toCreateCommandNull() {
      assertThat(StaffPresenter.toCommand((StaffCreateRequest) null)).isNull();
    }
  }

  @Nested
  @DisplayName("Update Command Mapping Tests")
  class UpdateCommandMappingTests {

    @Test
    @DisplayName("Should map StaffUpdateRequest to StaffUpdateCommand")
    void toUpdateCommand() {
      UUID entityId = UuidCreator.getTimeOrderedEpoch();
      StaffUpdateRequest req = new StaffUpdateRequest("New Name", "new@pug.com", entityId);

      StaffUpdateCommand cmd = StaffPresenter.toCommand(req);

      assertThat(cmd).isNotNull();
      assertThat(cmd.entityId()).isEqualTo(entityId);
      assertThat(cmd.accountCommand()).isNotNull();
      assertThat(cmd.accountCommand().emailString()).isEqualTo("new@pug.com");
      assertThat(cmd.accountCommand().passwordHash()).isNull();
      assertThat(cmd.accountCommand().active()).isNull();
      assertThat(cmd.accountCommand().userCommand()).isNotNull();
      assertThat(cmd.accountCommand().userCommand().name()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("Should map partial StaffUpdateRequest")
    void toUpdateCommandPartial() {
      StaffUpdateRequest req = new StaffUpdateRequest(null, null, null);

      StaffUpdateCommand cmd = StaffPresenter.toCommand(req);

      assertThat(cmd).isNotNull();
      assertThat(cmd.entityId()).isNull();
      assertThat(cmd.accountCommand().emailString()).isNull();
      assertThat(cmd.accountCommand().passwordHash()).isNull();
      assertThat(cmd.accountCommand().active()).isNull();
      assertThat(cmd.accountCommand().userCommand().name()).isNull();
    }

    @Test
    @DisplayName("Should return null when StaffUpdateRequest is null")
    void toUpdateCommandNull() {
      assertThat(StaffPresenter.toCommand((StaffUpdateRequest) null)).isNull();
    }
  }

  @Nested
  @DisplayName("Response Mapping Tests")
  class ResponseMappingTests {

    @Test
    @DisplayName("Should return null when StaffView is null")
    void toResponseNullView() {
      assertThat(StaffPresenter.toResponse(null, Locale.US, i18n)).isNull();
    }

    @Test
    @DisplayName("Should map StaffView to StaffResponse correctly")
    void toResponseSuccess() {
      UUID entityId = UuidCreator.getTimeOrderedEpoch();
      UUID cityId = UuidCreator.getTimeOrderedEpoch();
      StaffView view = new StaffView(buildAccountView(), entityId, cityId);

      StaffResponse response = StaffPresenter.toResponse(view, Locale.US, i18n);

      assertThat(response).isNotNull();
      assertThat(response.entityId()).isEqualTo(entityId);
      assertThat(response.cityId()).isEqualTo(cityId);
      assertThat(response.account()).isNotNull();
      assertThat(response.account().email()).isEqualTo("staff@pug.com");
      assertThat(response.account().accountType()).isEqualTo(AccountType.PARTNER);
      assertThat(response.account().active()).isTrue();
    }

    @Test
    @DisplayName("Should map StaffComplexSearchView to StaffComplexSearchResponse correctly")
    void toComplexSearchResponseSuccess() {
      UUID entityId = UuidCreator.getTimeOrderedEpoch();
      StaffComplexSearchView view =
          new StaffComplexSearchView(
              new AccountComplexSearchView(
                  UuidCreator.getTimeOrderedEpoch(),
                  UuidCreator.getTimeOrderedEpoch(),
                  "Staff User",
                  "staff@pug.com",
                  AccountType.PARTNER,
                  OffsetDateTime.now(),
                  OffsetDateTime.now(),
                  true),
              new EntityComplexSearchView(
                  entityId,
                  TestBrazilianIdentifierGenerator.generateValidCnpj(),
                  "Entity A",
                  "Rua A, 123",
                  UuidCreator.getTimeOrderedEpoch(),
                  "Blumenau",
                  "1234567",
                  OffsetDateTime.now(),
                  OffsetDateTime.now()));

      StaffComplexSearchResponse response =
          StaffPresenter.toComplexSearchResponse(view, Locale.US, i18n);

      assertThat(response).isNotNull();
      assertThat(response.account()).isNotNull();
      assertThat(response.account().email()).isEqualTo("staff@pug.com");
      assertThat(response.entity()).isNotNull();
      assertThat(response.entity().id()).isEqualTo(entityId);
      assertThat(response.entity().name()).isEqualTo("Entity A");
    }

    private AccountView buildAccountView() {
      return new AccountView(
          UuidCreator.getTimeOrderedEpoch(),
          UuidCreator.getTimeOrderedEpoch(),
          "staff@pug.com",
          AccountType.PARTNER,
          OffsetDateTime.now(),
          OffsetDateTime.now(),
          true);
    }
  }
}
