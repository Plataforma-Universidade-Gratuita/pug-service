package br.org.catolicasc.pug.identity.presenter.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.identity.infra.read.dtos.AccountView;
import br.org.catolicasc.pug.identity.infra.read.dtos.AdminView;
import br.org.catolicasc.pug.identity.presenter.dtos.AdminCreateRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.AdminResponse;
import br.org.catolicasc.pug.identity.presenter.dtos.AdminUpdateRequest;
import br.org.catolicasc.pug.identity.service.dtos.AdminCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.AdminUpdateCommand;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
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
@DisplayName("AdminPresenter Coverage")
class AdminPresenterTest {

  @Inject I18n i18n;

  @Nested
  @DisplayName("Command Mapping Tests")
  class CommandMappingTests {

    @Test
    @DisplayName("Should map CreateRequest to AdminCreateCommand")
    void toCreateCommand() {
      String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
      AdminCreateRequest req =
          new AdminCreateRequest(cpf, "Admin User", "a@a.com", "pass", Campi.JOINVILLE);
      AdminCreateCommand cmd = AdminPresenter.toCommand(req, "hashedPass");

      assertThat(cmd.campus()).isEqualTo(Campi.JOINVILLE);
      assertThat(cmd.accountCommand().emailString()).isEqualTo("a@a.com");
      assertThat(cmd.accountCommand().passwordHash()).isEqualTo("hashedPass");
      assertThat(cmd.accountCommand().userCommand().cpfString()).isEqualTo(cpf);
    }

    @Test
    @DisplayName("Should map UpdateRequest to AdminUpdateCommand")
    void toUpdateCommand() {
      AdminUpdateRequest req =
          new AdminUpdateRequest("New Name", "new@a.com", "newPass", Campi.JARAGUA_DO_SUL);
      AdminUpdateCommand cmd = AdminPresenter.toCommand(req, "hashedPass");

      assertThat(cmd.campus()).isEqualTo(Campi.JARAGUA_DO_SUL);
      assertThat(cmd.accountCommand().emailString()).isEqualTo("new@a.com");
      assertThat(cmd.accountCommand().userCommand().name()).isEqualTo("New Name");
    }
  }

  @Nested
  @DisplayName("Response Mapping Tests")
  class ResponseMappingTests {

    @Test
    @DisplayName("Should return null if AdminView is null")
    void toResponseNull() {
      assertThat(AdminPresenter.toResponse(null, Locale.US, i18n)).isNull();
    }

    @Test
    @DisplayName("Should map AdminView to AdminResponse correctly")
    void toResponseSuccess() {
      AccountView acc =
          new AccountView(
              UUID.randomUUID(),
              UUID.randomUUID(),
              "a@a.com",
              AccountType.ADMIN,
              OffsetDateTime.now(),
              OffsetDateTime.now(),
              true);
      AdminView view = new AdminView(acc, OffsetDateTime.now(), Campi.JOINVILLE);

      AdminResponse response = AdminPresenter.toResponse(view, Locale.US, i18n);

      assertThat(response).isNotNull();
      assertThat(response.campus().campus()).isEqualTo(Campi.JOINVILLE);
      assertThat(response.accountResponse().email()).isEqualTo("a@a.com");
      assertThat(response.grantedAtFormatted()).isNotBlank();
    }
  }
}
