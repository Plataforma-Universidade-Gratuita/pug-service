package br.org.catolicasc.pug.identity.service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AccountProcessor Tests")
class AccountProcessorTest {

  @Test
  @DisplayName("Should process create input and build valid Account")
  void shouldBuildAccount() {
    Account account =
        AccountProcessor.processCreateInput(
            UUID.randomUUID(), "test@pug.com", "ADMIN", "password-hash");

    assertThat(account.hasFieldErrors()).isFalse();
    assertThat(account.getAccountType()).isEqualTo(AccountType.ADMIN);
  }

  @Test
  @DisplayName("Should mutate account state correctly via update")
  void shouldUpdateAccount() {
    Account acc =
        Account.factory(
            UUID.randomUUID(),
            br.org.catolicasc.pug.identity.domain.vos.Email.factory("old@test.com"),
            AccountType.STUDENT,
            "hash");

    Account updated = AccountProcessor.processUpdateInput(acc, "new@test.com", null);

    assertThat(updated.getEmail().getValue()).isEqualTo("new@test.com");
    assertThat(updated.getPasswordHash()).isEqualTo("hash");
  }
}
