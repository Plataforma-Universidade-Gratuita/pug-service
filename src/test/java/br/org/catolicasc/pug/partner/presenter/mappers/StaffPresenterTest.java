package br.org.catolicasc.pug.partner.presenter.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.identity.infra.read.dtos.AccountView;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffView;
import br.org.catolicasc.pug.partner.presenter.dtos.StaffCreateRequest;
import br.org.catolicasc.pug.partner.presenter.dtos.StaffResponse;
import br.org.catolicasc.pug.partner.presenter.dtos.StaffUpdateRequest;
import br.org.catolicasc.pug.partner.service.dtos.StaffCreateCommand;
import br.org.catolicasc.pug.partner.service.dtos.StaffUpdateCommand;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.i18n.I18n;
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
      UUID entityId = UUID.randomUUID();
      String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
      StaffCreateRequest req =
          new StaffCreateRequest(cpf, "Staff User", "staff@pug.com", "password123", entityId);

      StaffCreateCommand cmd = StaffPresenter.toCommand(req, "hashedPass");

      assertThat(cmd).isNotNull();
      assertThat(cmd.entityId()).isEqualTo(entityId);
      assertThat(cmd.accountCommand()).isNotNull();
      assertThat(cmd.accountCommand().emailString()).isEqualTo("staff@pug.com");
      assertThat(cmd.accountCommand().passwordHash()).isEqualTo("hashedPass");
      assertThat(cmd.accountCommand().userCommand()).isNotNull();
      assertThat(cmd.accountCommand().userCommand().cpfString()).isEqualTo(cpf);
      assertThat(cmd.accountCommand().userCommand().name()).isEqualTo("Staff User");
    }

    @Test
    @DisplayName("Should return null when StaffCreateRequest is null")
    void toCreateCommandNull() {
      assertThat(StaffPresenter.toCommand((StaffCreateRequest) null, "hash")).isNull();
    }
  }

  @Nested
  @DisplayName("Update Command Mapping Tests")
  class UpdateCommandMappingTests {

    @Test
    @DisplayName("Should map StaffUpdateRequest to StaffUpdateCommand")
    void toUpdateCommand() {
      StaffUpdateRequest req = new StaffUpdateRequest("New Name", "new@pug.com", "newPass");

      StaffUpdateCommand cmd = StaffPresenter.toCommand(req, "hashedNew");

      assertThat(cmd).isNotNull();
      assertThat(cmd.accountCommand()).isNotNull();
      assertThat(cmd.accountCommand().emailString()).isEqualTo("new@pug.com");
      assertThat(cmd.accountCommand().passwordHash()).isEqualTo("hashedNew");
      assertThat(cmd.accountCommand().userCommand()).isNotNull();
      assertThat(cmd.accountCommand().userCommand().name()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("Should map partial StaffUpdateRequest (all nulls)")
    void toUpdateCommandPartial() {
      StaffUpdateRequest req = new StaffUpdateRequest(null, null, null);

      StaffUpdateCommand cmd = StaffPresenter.toCommand(req, null);

      assertThat(cmd).isNotNull();
      assertThat(cmd.accountCommand().emailString()).isNull();
      assertThat(cmd.accountCommand().passwordHash()).isNull();
      assertThat(cmd.accountCommand().userCommand().name()).isNull();
    }

    @Test
    @DisplayName("Should return null when StaffUpdateRequest is null")
    void toUpdateCommandNull() {
      assertThat(StaffPresenter.toCommand((StaffUpdateRequest) null, "hash")).isNull();
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
    @DisplayName("Should return null when locale is null")
    void toResponseNullLocale() {
      AccountView acc = buildAccountView();
      StaffView view = new StaffView(acc, UUID.randomUUID(), UUID.randomUUID());

      assertThat(StaffPresenter.toResponse(view, null, i18n)).isNull();
    }

    @Test
    @DisplayName("Should return null when i18n is null")
    void toResponseNullI18n() {
      AccountView acc = buildAccountView();
      StaffView view = new StaffView(acc, UUID.randomUUID(), UUID.randomUUID());

      assertThat(StaffPresenter.toResponse(view, Locale.US, null)).isNull();
    }

    @Test
    @DisplayName("Should map StaffView to StaffResponse correctly")
    void toResponseSuccess() {
      UUID entityId = UUID.randomUUID();
      UUID cityId = UUID.randomUUID();
      AccountView acc = buildAccountView();
      StaffView view = new StaffView(acc, entityId, cityId);

      StaffResponse response = StaffPresenter.toResponse(view, Locale.US, i18n);

      assertThat(response).isNotNull();
      assertThat(response.entityId()).isEqualTo(entityId);
      assertThat(response.cityId()).isEqualTo(cityId);
      assertThat(response.account()).isNotNull();
      assertThat(response.account().email()).isEqualTo("staff@pug.com");
      assertThat(response.account().accountType()).isEqualTo(AccountType.PARTNER);
      assertThat(response.account().active()).isTrue();
    }

    private AccountView buildAccountView() {
      return new AccountView(
          UUID.randomUUID(),
          UUID.randomUUID(),
          "staff@pug.com",
          AccountType.PARTNER,
          OffsetDateTime.now(),
          OffsetDateTime.now(),
          true);
    }
  }
}
