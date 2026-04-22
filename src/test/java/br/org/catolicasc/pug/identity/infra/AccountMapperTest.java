package br.org.catolicasc.pug.identity.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.AbstractMapperTest;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.vos.Email;
import br.org.catolicasc.pug.identity.infra.persistence.AccountEntity;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import com.github.f4b6a3.uuid.UuidCreator;
import org.junit.jupiter.api.DisplayName;

@DisplayName("AccountMapper Tests")
class AccountMapperTest extends AbstractMapperTest<Account, AccountEntity> {

  @Override
  protected Account createDomain() {
    return Account.factory(
        UuidCreator.getTimeOrderedEpoch(),
        Email.factory("test@pug.com"),
        AccountType.ADMIN,
        "hashed-password");
  }

  @Override
  protected Account mapToDomain(AccountEntity entity) {
    return AccountMapper.toDomain(entity);
  }

  @Override
  protected AccountEntity mapToEntity(Account domain) {
    return AccountMapper.toEntity(domain);
  }

  @Override
  protected void assertRoundTrip(Account original, Account mapped) {
    assertThat(mapped.getId()).isEqualTo(original.getId());
    assertThat(mapped.getEmail()).isEqualTo(original.getEmail());
    assertThat(mapped.getUserId()).isEqualTo(original.getUserId());
  }
}
