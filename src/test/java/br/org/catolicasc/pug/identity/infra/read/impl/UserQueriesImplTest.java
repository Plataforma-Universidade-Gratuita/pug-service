package br.org.catolicasc.pug.identity.infra.read.impl;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class UserQueriesImplTest {

    @Inject
    UserQueriesImpl queries;

    @Test
    @DisplayName("Should retrieve system admin via UserQueries")
    void shouldFindAdmin() {
        var user = queries.findOptionalByCpf("00000000000");

        assertThat(user).isPresent();
        assertThat(user.get().name()).isEqualTo("System Administrator");
    }

    @Test
    @DisplayName("Should list all users including seeded admin")
    void shouldListAll() {
        var users = queries.listAllUsers();
        assertThat(users).isNotEmpty();
    }
}