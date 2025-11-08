package com.pug.identity.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.helpers.domainGenerators.UserGenerator;
import com.pug.identity.domain.Admin;
import com.pug.identity.domain.User;
import com.pug.identity.infra.persistence.AdminsEntity;
import com.pug.identity.infra.persistence.UsersEntity;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class AdminMapperTest {

  private final UserGenerator userGen = new UserGenerator();

  @Test
  void toEntity_null_returns_null() {
    assertNull(AdminMapper.toEntity(null));
  }

  @Test
  void toEntity_sets_userId_and_reference_only() {
    User u = userGen.createRandomPersistedUser();
    var admin = Admin.builder().user(u).grantedAt(OffsetDateTime.now().minusMinutes(1)).build();

    AdminsEntity e = AdminMapper.toEntity(admin);

    assertNotNull(e);
    assertEquals(u.getId(), e.getUserId());
    assertNull(e.getUser());
    assertEquals(admin.getGrantedAt(), e.getGrantedAt());
  }

  @Test
  void toDomain_maps_all_fields() {
    var userId = UuidCreator.getTimeOrderedEpoch();
    UsersEntity ue =
        UsersEntity.builder()
            .cpf("11144477735")
            .name("Alice Admin")
            .email("alice.admin@example.com")
            .accountType("ADMIN")
            .passwordHash(null)
            .active(Boolean.TRUE)
            .build();
    ue.setId(userId);
    ue.setCreatedAt(OffsetDateTime.now().minusHours(2));

    var granted = OffsetDateTime.now().minusMinutes(10);
    AdminsEntity ae = AdminsEntity.builder().userId(userId).user(ue).grantedAt(granted).build();

    var d = AdminMapper.toDomain(ae);

    assertNotNull(d);
    assertNotNull(d.getUser());
    assertEquals(userId, d.getUser().getId());
    assertEquals("alice.admin@example.com", d.getUser().getEmail().toString());
    assertEquals("11144477735", d.getUser().getCpf().toString());
    assertEquals(granted, d.getGrantedAt());
  }
}
