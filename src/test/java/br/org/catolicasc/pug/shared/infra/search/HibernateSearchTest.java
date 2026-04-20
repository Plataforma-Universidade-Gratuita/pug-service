package br.org.catolicasc.pug.shared.infra.search;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.identity.infra.persistence.UserEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class HibernateSearchTest {

  @Inject EntityManager em;

  @Test
  @Transactional
  @DisplayName("Should find the pre-seeded System Administrator via search")
  void shouldFindSystemAdmin() {
    List<UserEntity> results = HibernateSearchUtils.searchByName(em, UserEntity.class, "System");

    assertThat(results)
        .isNotEmpty()
        .extracting(UserEntity::getName)
        .contains("System Administrator");
  }

  @Test
  @Transactional
  @DisplayName("Should find user even with partial name (fuzzy search)")
  void shouldFindWithFuzzySearch() {
    List<UserEntity> results = HibernateSearchUtils.searchByName(em, UserEntity.class, "Admin");

    assertThat(results)
        .isNotEmpty()
        .extracting(UserEntity::getName)
        .contains("System Administrator");
  }
}
