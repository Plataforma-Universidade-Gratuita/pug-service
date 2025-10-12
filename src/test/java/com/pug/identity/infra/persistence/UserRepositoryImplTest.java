// src/test/java/com/pug/identity/infra/persistence/UserRepositoryImplTest.java
package com.pug.identity.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.identity.domain.Cpf;
import com.pug.identity.domain.User;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserRepositoryImplTest {

  @Inject UserRepositoryImpl repo;

  private static final String CPF_A = "93541134780";
  private static final String CPF_B = "52998224725";

  private static User newUser(String name, String cpfMaskedOrDigits) {
    return User.builder().cpf(Cpf.of(cpfMaskedOrDigits)).name(name).build();
  }

  @Test
  @TestTransaction
  void saveAndFindByCpfAndId() {
    var saved = repo.save(newUser("Alice", "935.411.347-80"));
    assertNotNull(saved.getId());

    Optional<User> byCpf = repo.findByCpf(CPF_A);
    assertTrue(byCpf.isPresent());
    assertEquals("Alice", byCpf.get().getName());

    var byId = repo.findOptionalById(saved.getId());
    assertTrue(byId.isPresent());
    assertEquals(saved.getId(), byId.get().getId());
  }

  @Test
  @TestTransaction
  void saveUpdatesExistingMergesChanges() {
    var saved = repo.save(newUser("Alice", CPF_A));
    var updated = saved.toBuilder().name("Alice B").build();
    var out = repo.save(updated);
    assertEquals(saved.getId(), out.getId());
    assertEquals("Alice B", out.getName());
    assertEquals(CPF_A, out.getCpf().getValue());
  }

  @Test
  @TestTransaction
  void saveCanChangeCpfWhenNotUsedByAnother() {
    var u1 = repo.save(newUser("Alice", CPF_A));
    var out = repo.save(u1.toBuilder().cpf(Cpf.of(CPF_B)).build());
    assertEquals(CPF_B, out.getCpf().getValue());
  }

  @Test
  @TestTransaction
  void existsByCpfAndExistsByCpfForAnotherWork() {
    var a = repo.save(newUser("Alice", CPF_A));
    var b = repo.save(newUser("Bruna", CPF_B));

    assertTrue(repo.existsByCpf("935.411.347-80"));
    assertFalse(repo.existsByCpf("11111111111"));

    assertFalse(repo.existsByCpfForAnother(CPF_A, a.getId()));
    assertTrue(repo.existsByCpfForAnother(CPF_A, b.getId()));
  }

  @Test
  @TestTransaction
  void findByCpfReturnsEmptyWhenNotFound() {
    assertTrue(repo.findByCpf(CPF_A).isEmpty());
  }

  @Test
  @TestTransaction
  void findOptionalByIdReturnsEmptyWhenNotFound() {
    assertTrue(repo.findOptionalById(UUID.randomUUID()).isEmpty());
  }
}
