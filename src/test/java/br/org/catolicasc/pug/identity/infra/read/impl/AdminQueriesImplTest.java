package br.org.catolicasc.pug.identity.infra.read.impl;

import br.org.catolicasc.pug.shared.domain.enums.Campi;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


@QuarkusTest
class AdminQueriesImplTest {

    @Inject
    AdminQueriesImpl queries;

    @Test
    @DisplayName("Should project full nested AdminView for system admin")
    void shouldGetAdminView() {
        var admin = queries.findOptionalByEmail("admin@pug.com");

        assertThat(admin).isPresent();
        assertThat(admin.get().campus()).isEqualTo(Campi.JARAGUA_DO_SUL);
        assertThat(admin.get().accountView().email()).isEqualTo("admin@pug.com");
    }

    @Test
    @DisplayName("Should list system admin via AdminQueries")
    void shouldListAdmins() {
        var admins = queries.listAllAdmins();
        assertThat(admins).anyMatch(a -> a.accountView().email().equals("admin@pug.com"));
    }
}