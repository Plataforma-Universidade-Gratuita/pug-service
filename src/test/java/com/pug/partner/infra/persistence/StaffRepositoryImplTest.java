package com.pug.partner.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.partner.domain.Cnpj;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.Staff;
import com.pug.shared.infra.persistence.PageRequest;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StaffRepositoryImplTest {

  @Inject StaffRepositoryImpl repo;
  @Inject PartnerEntityRepositoryImpl entities;
  @Inject EntityManager em;

  private String anyCpf() {
    long n = Math.abs(System.nanoTime()) % 1_000_000_00000L;
    return String.format("%011d", n); // DB does not validate CPF algorithm
  }

  private UUID newUser(String name) {
    UUID id = UUID.randomUUID();
    em.createNativeQuery("insert into users(id,cpf,name) values (?,?,?)")
        .setParameter(1, id)
        .setParameter(2, anyCpf())
        .setParameter(3, name)
        .executeUpdate();
    return id;
  }

  private UUID newCity(String name, String ibge) {
    UUID id = UUID.randomUUID();
    em.createNativeQuery("insert into cities(id,name,ibge_code) values (?,?,?)")
        .setParameter(1, id)
        .setParameter(2, name)
        .setParameter(3, ibge)
        .executeUpdate();
    return id;
  }

  private UUID newEntity() {
    var city = newCity("São José-" + System.nanoTime(), "4216609");
    var e =
        PartnerEntity.newActive()
            .cnpj(Cnpj.of("11222333000181"))
            .name("Ent " + System.nanoTime())
            .cityId(city)
            .build();
    return entities.save(e).getId();
  }

  @Test
  @TestTransaction
  void persistFindByEmailExistsAndList() {
    var userId = newUser("Alice " + System.nanoTime());
    var entityId = newEntity();

    var s =
        Staff.newActive()
            .userId(userId)
            .email("Person@Example.com")
            .entityId(entityId)
            .build(); // no id on create

    var saved = repo.save(s);
    assertNotNull(saved.getId());

    assertTrue(repo.findOptionalById(saved.getId()).isPresent());
    assertTrue(repo.findByEmail("person@example.com").isPresent());
    assertFalse(repo.existsByEmailForAnother("PERSON@EXAMPLE.COM", saved.getId()));

    var page = repo.listByEntity(entityId, new PageRequest(0, 10));
    assertTrue(page.items().stream().anyMatch(x -> x.getId().equals(saved.getId())));
  }
}
