package br.org.catolicasc.pug.identity.service.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.service.dtos.AccountCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.UserCreateCommand;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AccountProcessor Tests")
class AccountProcessorTest {

  @Test
  @DisplayName("Should extract missing user commands correctly")
  void shouldExtractMissing() {
    String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
    UserCreateCommand cmd = new UserCreateCommand(cpf, "Name");
    AccountCreateCommand accCmd =
        new AccountCreateCommand("a@a.com", AccountType.STUDENT, "hash", cmd);

    List<UserCreateCommand> missing =
        AccountProcessor.extractMissingUserCommands(List.of(accCmd), Map.of());
    assertThat(missing).hasSize(1);
  }

  @Test
  @DisplayName("Should build bulk accounts successfully")
  void shouldBuildBulk() {
    String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
    UserCreateCommand cmd = new UserCreateCommand(cpf, "Name");
    AccountCreateCommand accCmd =
        new AccountCreateCommand("a@a.com", AccountType.STUDENT, "hash", cmd);

    List<Account> accounts =
        AccountProcessor.processBulkCreateInput(
            List.of(accCmd), Map.of(cpf, UuidCreator.getTimeOrderedEpoch()));
    assertThat(accounts).hasSize(1);
  }

  @Test
  @DisplayName("Should throw validation exception on bulk error")
  void shouldThrowOnBulkError() {
    AccountCreateCommand accCmd =
        new AccountCreateCommand("", null, null, new UserCreateCommand("111", "Name"));
    assertThrows(
        AppValidationException.class,
        () -> AccountProcessor.processBulkCreateInput(List.of(accCmd), Map.of()));
  }

  @Test
  @DisplayName("Should mutate email and password correctly")
  void shouldUpdateAccount() {
    Account acc =
        AccountProcessor.processCreateInput(
            UuidCreator.getTimeOrderedEpoch(), "old@test.com", "STUDENT", "hash");

    Account updated = AccountProcessor.processUpdateInput(acc, "new@test.com", "newhash");

    assertThat(updated.getEmail().getValue()).isEqualTo("new@test.com");
    assertThat(updated.getPasswordHash()).isEqualTo("newhash");
  }

  @Test
  @DisplayName("Should return same instance if update values are null")
  void shouldIgnoreNullUpdates() {
    Account acc =
        AccountProcessor.processCreateInput(
            UuidCreator.getTimeOrderedEpoch(), "old@test.com", "STUDENT", "hash");
    assertThat(AccountProcessor.processUpdateInput(acc, null, null)).isEqualTo(acc);
  }
}
