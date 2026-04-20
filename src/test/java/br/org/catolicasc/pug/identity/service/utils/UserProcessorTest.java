package br.org.catolicasc.pug.identity.service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.service.dtos.UserCreateCommand;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UserProcessor Tests")
class UserProcessorTest {

  @Test
  @DisplayName("Should process create input successfully")
  void shouldProcessCreateInput() {
    User user = UserProcessor.processCreateInput("11144477735", "Test User");

    assertThat(user.hasFieldErrors()).isFalse();
    assertThat(user.getName()).isEqualTo("Test User");
    assertThat(user.getCpf().getValue()).isEqualTo("11144477735");
  }

  @Test
  @DisplayName("Should throw AppValidationException on invalid bulk input")
  void shouldThrowOnInvalidBulk() {
    List<UserCreateCommand> cmds = List.of(new UserCreateCommand("111", "Name"));

    org.junit.jupiter.api.Assertions.assertThrows(
        AppValidationException.class,
        () -> {
          UserProcessor.processBulkCreateInput(cmds);
        });
  }
}
