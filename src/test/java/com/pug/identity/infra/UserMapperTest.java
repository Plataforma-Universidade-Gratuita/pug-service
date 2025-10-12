package com.pug.identity.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.pug.identity.domain.Cpf;
import com.pug.identity.domain.User;
import com.pug.identity.infra.persistence.UserEntity;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserMapperTest {

  private static final String CPF_VALID = "93541134780";

  @Test
  void toDomainMapsFields() {
    var id = UUID.randomUUID();
    var e = UserEntity.builder().id(id).cpf(CPF_VALID).name("Alan").build();
    var d = UserMapper.toDomain(e);
    assertEquals(id, d.getId());
    assertEquals(CPF_VALID, d.getCpf().getValue());
    assertEquals("Alan", d.getName());
  }

  @Test
  void toEntityMapsFields() {
    var id = UUID.randomUUID();
    var d = User.builder().id(id).cpf(Cpf.of(CPF_VALID)).name("Bruna").build();
    var e = UserMapper.toEntity(d);
    assertEquals(id, e.getId());
    assertEquals(CPF_VALID, e.getCpf());
    assertEquals("Bruna", e.getName());
  }

  @Test
  void nullSafety() {
    assertNull(UserMapper.toDomain(null));
    assertNull(UserMapper.toEntity(null));
  }
}
