package br.org.catolicasc.pug.identity.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.AbstractMapperTest;
import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.domain.vos.Cpf;
import br.org.catolicasc.pug.identity.infra.persistence.UserEntity;
import org.junit.jupiter.api.DisplayName;

@DisplayName("UserMapper Tests")
class UserMapperTest extends AbstractMapperTest<User, UserEntity> {

  @Override
  protected User createDomain() {
    return User.factory(
        Cpf.factory(TestBrazilianIdentifierGenerator.generateValidCpf()), "System Administrator");
  }

  @Override
  protected User mapToDomain(UserEntity entity) {
    return UserMapper.toDomain(entity);
  }

  @Override
  protected UserEntity mapToEntity(User domain) {
    return UserMapper.toEntity(domain);
  }

  @Override
  protected void assertRoundTrip(User original, User mapped) {
    assertThat(mapped).isEqualTo(original);
    assertThat(mapped.getCpf().getValue()).isEqualTo(original.getCpf().getValue());
  }
}
