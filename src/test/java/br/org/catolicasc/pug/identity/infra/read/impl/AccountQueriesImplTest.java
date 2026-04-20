package br.org.catolicasc.pug.identity.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AccountQueriesImplTest {

  @Inject AccountQueriesImpl queries;

  @Test
  @DisplayName("Should project account data joined with user name")
  void shouldProjectView() {
    var view = queries.findOptionalByEmail("admin@pug.com");

    assertThat(view).isPresent();
    assertThat(view.get().email()).isEqualTo("admin@pug.com");
    assertThat(view.get().id()).isNotNull();
  }

  @Test
  @DisplayName("Should list accounts sorted by user name")
  void shouldListSorted() {
    var list = queries.listAllAccounts();
    assertThat(list).isNotEmpty();
  }
}
