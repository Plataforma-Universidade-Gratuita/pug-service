package br.org.catolicasc.pug.identity.presenter.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.identity.infra.read.dtos.UserView;
import br.org.catolicasc.pug.identity.presenter.dtos.UserResponse;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("UserPresenter Coverage")
class UserPresenterTest {

  @Nested
  @DisplayName("CPF Formatting Tests")
  class CpfFormattingTests {

    @Test
    @DisplayName("Should return null when UserView is null")
    void shouldReturnNull() {
      assertThat(UserPresenter.toResponse(null, Locale.US)).isNull();
    }

    @Test
    @DisplayName("Should format valid 11-digit CPF correctly")
    void shouldFormatValidCpf() {
      UserView view =
          new UserView(
              UUID.randomUUID(), "11144477735", "Test", OffsetDateTime.now(), OffsetDateTime.now());
      UserResponse response = UserPresenter.toResponse(view, Locale.US);

      assertThat(response.cpfFormatted()).isEqualTo("111.444.777-35");
    }

    @Test
    @DisplayName("Should return raw CPF if length is not 11")
    void shouldReturnRawCpfIfInvalid() {
      // Testing the edge case in UserPresenter.cpfFormatted(String cpf)
      UserView view =
          new UserView(
              UUID.randomUUID(), "123", "Test", OffsetDateTime.now(), OffsetDateTime.now());
      UserResponse response = UserPresenter.toResponse(view, Locale.US);

      assertThat(response.cpfFormatted()).isEqualTo("123");
    }

    @Test
    @DisplayName("Should return null if CPF is null")
    void shouldReturnNullCpf() {
      UserView view =
          new UserView(UUID.randomUUID(), null, "Test", OffsetDateTime.now(), OffsetDateTime.now());
      UserResponse response = UserPresenter.toResponse(view, Locale.US);

      assertThat(response.cpfFormatted()).isNull();
    }
  }

  @Nested
  @DisplayName("Response Mapping Tests")
  class MappingTests {

    @Test
    @DisplayName("Should map all fields correctly to UserResponse")
    void shouldMapAllFields() {
      UUID id = UUID.randomUUID();
      OffsetDateTime now = OffsetDateTime.now();
      UserView view = new UserView(id, "11144477735", "John Doe", now, now);

      UserResponse response = UserPresenter.toResponse(view, Locale.US);

      assertThat(response.id()).isEqualTo(id);
      assertThat(response.name()).isEqualTo("John Doe");
      assertThat(response.auditInfo()).isNotNull();
      assertThat(response.auditInfo().createdAt()).isEqualTo(now);
    }
  }
}
