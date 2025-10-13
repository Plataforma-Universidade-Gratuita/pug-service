package com.pug.partner.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.partner.domain.Address;
import com.pug.partner.domain.Cnpj;
import com.pug.partner.domain.PartnerEntity;
import com.pug.shared.infra.persistence.PageRequest;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PartnerEntityRepositoryImplTest {

  @Inject PartnerEntityRepositoryImpl repo;
  @Inject EntityManager em;

  private UUID newCity(String name, String ibge) {
    UUID id = UUID.randomUUID();
    em.createNativeQuery("insert into cities(id,name,ibge_code) values (?,?,?)")
        .setParameter(1, id)
        .setParameter(2, name)
        .setParameter(3, ibge)
        .executeUpdate();
    return id;
  }

  @Test
  @TestTransaction
  void persistFindExistsSearchAndList() {
    var city = newCity("Florianópolis-" + System.nanoTime(), "4215407");

    var p =
        PartnerEntity.newActive()
            .cnpj(Cnpj.of("19131243000197"))
            .name("Associação São João")
            .cityId(city)
            .address(Address.of("Rua A, 100"))
            .build();

    var saved = repo.save(p);
    assertNotNull(saved.getId());

    assertTrue(repo.findOptionalById(saved.getId()).isPresent());
    assertTrue(repo.findByCnpj("19.131.243/0001-97").isPresent());
    assertFalse(repo.existsByCnpjForAnother("19131243000197", saved.getId()));

    var page = repo.listByCity(city, new PageRequest(0, 10));
    assertTrue(page.items().stream().anyMatch(x -> x.getId().equals(saved.getId())));

    var srch = repo.searchByName("sao joao", new PageRequest(0, 10));
    assertTrue(srch.items().stream().anyMatch(x -> x.getId().equals(saved.getId())));
  }
}
