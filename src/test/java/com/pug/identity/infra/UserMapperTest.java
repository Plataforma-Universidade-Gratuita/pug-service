package com.pug.identity.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.helpers.domainGenerators.UserGenerator;
import com.pug.helpers.entityGenerators.UsersEntityGenerator;
import com.pug.identity.domain.User;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.identity.infra.persistence.UsersEntity;
import com.pug.shared.domain.enums.AccountType;
import java.time.OffsetDateTime;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class UserMapperTest {

  private final UserGenerator domainGen = new UserGenerator();
  private final UsersEntityGenerator entityGen = new UsersEntityGenerator();

  @Test
  void toDomain_null_returnsNull() {
    assertNull(UserMapper.toDomain(null));
  }

  @Test
  void toDomain_maps_and_normalizes_email_and_cpf_and_enum() {
    UsersEntity e =
        UsersEntity.builder()
            .cpf("529.982.247-25")
            .name("Alice Example")
            .email("Alice.Example@Example.COM")
            .accountType("student")
            .passwordHash("hash-1")
            .active(true)
            .createdAt(OffsetDateTime.now().minusDays(1))
            .build();

    User d = UserMapper.toDomain(e);

    assertNotNull(d);
    assertEquals(new Cpf("52998224725").toString(), d.getCpf().toString());
    assertEquals(new Email("alice.example@example.com").toString(), d.getEmail().toString());
    assertEquals("Alice Example", d.getName());
    assertEquals(AccountType.STUDENT, d.getAccountType());
    assertEquals("hash-1", d.getPasswordHash());
    assertTrue(d.getActive());
    assertEquals(e.getCreatedAt(), d.getCreatedAt());
  }

  @Test
  void toEntity_maps_from_domain_without_createdAt() {
    User d =
        domainGen.randomUser().toBuilder()
            .name("Bob Test")
            .email(new Email("Bob.TEST@Example.com"))
            .accountType(AccountType.ADMIN)
            .passwordHash("hash-2")
            .build();

    UsersEntity e = UserMapper.toEntity(d);

    assertNotNull(e);
    assertEquals(d.getCpf().toString(), e.getCpf());
    assertEquals("Bob Test", e.getName());
    assertEquals(d.getEmail().toString(), e.getEmail());
    assertEquals("ADMIN", e.getAccountType());
    assertEquals("hash-2", e.getPasswordHash());
    assertEquals(d.getActive(), e.getActive());
    assertNull(e.getCreatedAt());
  }

  @Test
  void copy_updates_mutable_fields_and_does_not_touch_createdAt() {
    UsersEntity e = entityGen.createRandomUsersEntity();
    e.setAccountType("STUDENT");
    OffsetDateTime originalCreatedAt = OffsetDateTime.now().minusDays(2);
    e.setCreatedAt(originalCreatedAt);

    User d =
        User.builder()
            .id(null)
            .cpf(new Cpf("15350946056"))
            .name("Charlie Update")
            .email(new Email("charlie.update@example.com"))
            .accountType(AccountType.PARTNER)
            .passwordHash("hash-3")
            .active(Boolean.FALSE)
            .createdAt(null)
            .build();

    UserMapper.copy(d, e);

    assertEquals("15350946056", e.getCpf());
    assertEquals("Charlie Update", e.getName());
    assertEquals("charlie.update@example.com", e.getEmail());
    assertEquals("PARTNER", e.getAccountType());
    assertEquals("hash-3", e.getPasswordHash());
    assertFalse(e.getActive());
    assertEquals(originalCreatedAt, e.getCreatedAt());
  }

  @Test
  void roundTrip_entity_to_domain_to_entity_preserves_semantics() {
    UsersEntity src = entityGen.createRandomUsersEntity();
    src.setAccountType("STUDENT");
    src.setEmail(src.getEmail().toUpperCase(Locale.ROOT));

    User d = UserMapper.toDomain(src);
    UsersEntity back = UserMapper.toEntity(d);

    assertEquals(src.getCpf().replaceAll("\\D", ""), back.getCpf());
    assertEquals(src.getName(), back.getName());
    assertEquals(src.getEmail().toLowerCase(Locale.ROOT), back.getEmail());
    assertEquals("STUDENT", back.getAccountType());
    assertEquals(src.getActive(), back.getActive());
    assertNull(back.getCreatedAt());
  }
}
