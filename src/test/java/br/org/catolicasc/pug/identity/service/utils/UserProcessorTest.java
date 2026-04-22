package br.org.catolicasc.pug.identity.service.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.service.dtos.UserCreateCommand;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UserProcessor Tests")
class UserProcessorTest {

  @Test
  @DisplayName("Should process create input successfully")
  void shouldProcessCreateInput() {
    User user =
        UserProcessor.processCreateInput(
            TestBrazilianIdentifierGenerator.generateValidCpf(), "Test User");
    assertThat(user.hasFieldErrors()).isFalse();
    assertThat(user.getName()).isEqualTo("Test User");
  }

  @Test
  @DisplayName("Should process bulk create successfully")
  void shouldProcessBulkCreate() {
    List<UserCreateCommand> cmds =
        List.of(
            new UserCreateCommand(TestBrazilianIdentifierGenerator.generateValidCpf(), "User 1"));
    List<User> users = UserProcessor.processBulkCreateInput(cmds);
    assertThat(users).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty list for null/empty bulk input")
  void shouldReturnEmptyForEmptyBulk() {
    assertThat(UserProcessor.processBulkCreateInput(null)).isEmpty();
    assertThat(UserProcessor.processBulkCreateInput(Collections.emptyList())).isEmpty();
  }

  @Test
  @DisplayName("Should throw AppValidationException on invalid bulk input")
  void shouldThrowOnInvalidBulk() {
    List<UserCreateCommand> cmds = List.of(new UserCreateCommand("111", "Name"));
    assertThrows(AppValidationException.class, () -> UserProcessor.processBulkCreateInput(cmds));
  }

  @Test
  @DisplayName("Should rename user successfully")
  void shouldRenameUser() {
    User user =
        UserProcessor.processCreateInput(
            TestBrazilianIdentifierGenerator.generateValidCpf(), "Old Name");
    User updated = UserProcessor.processUpdateInput(user, "New Name");
    assertThat(updated.getName()).isEqualTo("New Name");
  }

  @Test
  @DisplayName("Should return same instance if rename name is same or empty")
  void shouldReturnSameInstanceOnInvalidUpdate() {
    User user =
        UserProcessor.processCreateInput(
            TestBrazilianIdentifierGenerator.generateValidCpf(), "Same Name");
    assertThat(UserProcessor.processUpdateInput(user, "Same Name")).isEqualTo(user);
    assertThat(UserProcessor.processUpdateInput(user, null)).isEqualTo(user);
  }
}
