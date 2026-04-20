package br.org.catolicasc.pug.identity.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.identity.domain.enums.IdentityFieldErrorCodes;
import br.org.catolicasc.pug.identity.domain.vos.Cpf;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("User Aggregate Tests")
class UserTest {

  @Test
  @DisplayName("Should create valid User")
  void shouldCreateUser() {
    User user = User.factory(Cpf.factory("11144477735"), "John Doe");
    assertThat(user.hasFieldErrors()).isFalse();
  }

  @Test
  @DisplayName("Should collect errors when data is invalid")
  void shouldCollectValidationErrors() {
    User user = User.factory(Cpf.factory("111"), "");

    assertThat(user.hasFieldErrors()).isTrue();
    assertThat(user.getFieldErrors())
        .contains(
            IdentityFieldErrorCodes.INVALID_CPF_FORMAT,
            IdentityFieldErrorCodes.INVALID_USER_ID_BLANK);
  }
}
