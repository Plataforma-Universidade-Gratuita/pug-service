package br.org.catolicasc.pug.identity.infra;

import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.vos.Email;
import br.org.catolicasc.pug.identity.infra.persistence.AccountEntity;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AccountMapper Tests")
class AccountMapperTest {

    @Test
    @DisplayName("Should perform round-trip mapping for Account")
    void shouldPerformRoundTrip() {
        Account account = Account.factory(
                UUID.randomUUID(),
                Email.factory("test@pug.com"),
                AccountType.ADMIN,
                "hashed-password"
        );

        AccountEntity entity = AccountMapper.toEntity(account);
        Account mappedAccount = AccountMapper.toDomain(entity);

        assertThat(mappedAccount.getId()).isEqualTo(account.getId());
        assertThat(mappedAccount.getEmail()).isEqualTo(account.getEmail());
        assertThat(mappedAccount.getUserId()).isEqualTo(account.getUserId());
    }
}