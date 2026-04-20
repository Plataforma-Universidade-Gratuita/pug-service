package br.org.catolicasc.pug.identity.infra;

import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.domain.vos.Cpf;
import br.org.catolicasc.pug.identity.infra.persistence.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserMapper Tests")
class UserMapperTest {

    @Test
    @DisplayName("Should perform a perfect round-trip (Domain -> Entity -> Domain)")
    void shouldPerformRoundTrip() {
        User user = User.factory(Cpf.factory("11144477735"), "System Administrator");

        UserEntity entity = UserMapper.toEntity(user);
        User mappedUser = UserMapper.toDomain(entity);

        assertThat(mappedUser).isEqualTo(user);
        assertThat(mappedUser.getCpf().getValue()).isEqualTo(user.getCpf().getValue());
    }
}