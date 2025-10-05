package com.pug.identity.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.identity.domain.User;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserRepositoryTest {

  @Inject UserRepository repo;
  @Inject EntityManager em;

  private static final String CPF_A = "93541134780";
  private static final String CPF_B = "39053344705";

  @Test
  @TestTransaction
  void existsByCpfTrueWhenPresentFalseWhenAbsent() {
    persist("Grace", CPF_A);

    assertTrue(repo.existsByCpf(CPF_A));
    assertFalse(repo.existsByCpf(CPF_B));
  }

  @Test
  @TestTransaction
  void existsByCpfForAnotherIgnoresSameEntityChecksOthers() {
    var u1 = persist("Ada", CPF_A);
    var u2 = persist("Alan", CPF_B);

    assertFalse(repo.existsByCpfForAnother(CPF_A, u1.getId()));
    assertTrue(repo.existsByCpfForAnother(CPF_A, u2.getId()));
  }

  @Test
  @TestTransaction
  void findByCpfReturnsWhenPresentEmptyWhenAbsent() {
    persist("Ada", CPF_A);

    assertTrue(repo.findByCpf(CPF_A).isPresent());
    assertTrue(repo.findByCpf(CPF_B).isEmpty());
  }

  private User persist(String name, String cpf) {
    var u = User.builder().cpf(cpf).name(name).build();
    em.persist(u);
    em.flush();
    return u;
  }
}
